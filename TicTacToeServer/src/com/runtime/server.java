package com.runtime;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

public class server {
	
	private static int currentID;
	
	private static Game GAMES[];
	
	private static String matchQueue[];
	
	private static String[][] idNameKey; // used to match players with their IDS
	
	public static void main(String[] args) throws IOException {
		
		GAMES = new Game[0];
		matchQueue = new String[0];
		
		idNameKey = new String[0][2];
		
		currentID = 1;
		
	    InetSocketAddress addr = new InetSocketAddress(12876);
	    HttpServer server = HttpServer.create(addr, 0);
	
	    server.createContext("/", new gameHandler());
	    server.setExecutor(Executors.newCachedThreadPool());
	    server.start();
	    System.out.println("Server is listening on port 12876" );
	}

	
	private static class gameHandler implements HttpHandler {
		public void handle(HttpExchange exchange) throws IOException {
			String requestMethod = exchange.getRequestMethod();
			
			System.out.println("Client connected");
			
			
			//this server will only handle GET requests for ease of implementation
			if (requestMethod.equalsIgnoreCase("GET")) {
				
				String raw = exchange.getRequestURI().getQuery();
				
				//need to add split for & to include multiple variables
				
				String breakdown[] = raw.split("&");
				
				String values[][] = new String[breakdown.length][2]; //name then value
				
				for(int index = 0; index < breakdown.length; index++){
					
					String breakdownMID[] = breakdown[index].split("=");
					
					values[index][0] = breakdownMID[0];
					values[index][1] = breakdownMID[1];
					
					values[index][1].replaceAll("%20", " ");
					
				}
				
				//parse params and return message
				String message = "Bad Request";
				
				if(values[0][0].equals("request")){
					
					//user has requested to be matched with an opponent
					if(values[0][1].equals("MatchMake")){
						
						if(values[1][0].equals("ID") && values[2][0].equals("userName")){
							
							String userID = values[1][1];
							String userName = values[2][1];
							
							matchQueue = enqueue(matchQueue, userID);
							
							String newKey[] = new String[]{userID, userName};
							
							idNameKey = addNameKey(idNameKey, newKey);
							
							message = "You have been added to the Queue";
							
							attemptMatch();
							
						}
						
						
					}
					//user wants status of matchmaking
					else if(values[0][1].equals("MatchMakeStatus")){
						
						if(values[1][0].equals("ID")){
							
							String userID = values[1][1];
							
							String status = findUser(userID);
							
							if(status.equals("Not Found")){
								
								message = "Player not found";
								
							}
							else if(status.equals("In Queue")){
								
								message = "Player still in queue";
								
							}
							else{
								//get players opponents ID
								int PlayersIndex = Integer.parseInt(status);
								
								Game PlayersGame = GAMES[PlayersIndex];
								
								String players[] = PlayersGame.getPlayers().split("-");
								
								if(players[0].equals(userID)){
									
									message = "opponent-" + players[1];
									
								}
								else{
									
									message = "opponent-" + players[0];
									
								}
								
							}
							
						}
						
						
					}
					// user wants the board of their game
					else if(values[0][1].equals("MyGame")){
						
						if(values[1][0].equals("ID")){
							
							String userID = values[1][1];
							
							String status = findUser(userID);
							
							if(!status.equals("Not Found") && !status.equals("In Queue")){
								
								int PlayersIndex = Integer.parseInt(status);
								
								Game PlayersGame = GAMES[PlayersIndex];
								
								message = PlayersGame.getGameBoard();
								
							}
							
						}
						
					}
					// user notifying the server they are done with the game
					else if(values[0][1].equals("ImDone")){
						
						if(values[1][0].equals("ID")){
							
							String userID = values[1][1];
							
							String status = findUser(userID);
							
							if(!status.equals("Not Found") && !status.equals("In Queue")){
								
								int PlayersIndex = Integer.parseInt(status);
								
								GAMES[PlayersIndex].playerFinished();
								
								message = "Acknowledged";
								
								cleanGames(); // 50% of the time a game is done at this point
								
							}
							
						}
						
					}
					// user made a move
					else if(values[0][1].equals("MyMove")){
						
						if(values[1][0].equals("ID") && values[2][0].equals("Board")){
							
							String userID = values[1][1];
							String board = values[2][1];
							
							String status = findUser(userID);
							
							if(!status.equals("Not Found") && !status.equals("In Queue")){
								
								int PlayersIndex = Integer.parseInt(status);
								
								GAMES[PlayersIndex].setGAMEBOARD(board);
								
								message = "Acknowledged";
								
							}
							
						}
						
					}
					else if(values[0][1].equals("OtherUserStatus")){
						
						if(values[1][0].equals("ID")){
							
							String userID = values[1][1];
							
							String status = findUser(userID);
							
							if(!status.equals("Not Found") && !status.equals("In Queue")){
								
								int PlayersIndex = Integer.parseInt(status);
								
								boolean oneLeft = GAMES[PlayersIndex].oneLeft();
								
								if(oneLeft)
									message = "Your opponent left";
								else
									message = "Game still active";
								
							}
							
							
						}
						
						
					}
					else if(values[0][1].equals("MyTeam")){
						
						if(values[1][0].equals("ID")){
							
							String userID = values[1][1];
							
							String status = findUser(userID);
							
							if(!status.equals("Not Found") && !status.equals("In Queue")){
								
								int PlayersIndex = Integer.parseInt(status);
								
								String teams[] = GAMES[PlayersIndex].getPlayers().split("-");
								
								if(teams[0].equals(userID))
									message = "X";
								else
									message = "O";
								
							}
							
							
						}
						
						
					}
					else if(values[0][1].equals("LeaveQueue")){
						
						if(values[1][0].equals("ID")){
							
							String userID = values[1][1];
							
							String status = findUser(userID);
							
							if(status.equals("In Queue")){
								
								String newQueue[] = new String[matchQueue.length-1];
								boolean found = false;
								
								
								for(int index = 0; index < matchQueue.length; index++){
									
									if(userID.equals(matchQueue[index])){
										
										found = true;
									}
									else{
										if(found)
											newQueue[index-1] = matchQueue[index];
										else
											newQueue[index] = matchQueue[index];
									}
									
								}
								
								matchQueue = newQueue;
								
								message = "Acknowledged";
								
							}
							
							
						}
					
				}
				else if(values[0][1].equals("GetOpponentName")){
					
					if(values[1][0].equals("ID")){
						
						String userID = values[1][1];
						
						String status = findUser(userID);
						
						if(!status.equals("Not Found") && !status.equals("In Queue")){
							
							int PlayersIndex = Integer.parseInt(status);
							
							String teams[] = GAMES[PlayersIndex].getPlayers().split("-");
							
							if(teams[0].equals(userID))
								message = findName(teams[1]);
							else
								message = findName(teams[0]);
								
							
						}
						
						
					}
				
				
				}	
					
				
				
				Headers responseHeaders = exchange.getResponseHeaders();
				responseHeaders.set("Content-Type", "text/plain");
				exchange.sendResponseHeaders(200, message.length());
				
				OutputStream responseBody = exchange.getResponseBody();
				
				//send response
				responseBody.write(message.getBytes());
				
				responseBody.close();
				
				System.out.println("output sent: " + message);
				
			}
			
		}
		
	}
	
	
	// if 2 or more players are in a game ... match them in a game
	private static void attemptMatch(){
		
		//match can be made
		if(matchQueue.length >= 2){
			
			String PX = matchQueue[0];
			matchQueue = dequeue(matchQueue);
			String PO = matchQueue[0];
			matchQueue = dequeue(matchQueue);
			
			Game newGame = new Game(currentID, PX, PO);
			currentID++; 
			
			GAMES = addGame(GAMES, newGame);
			
		}
		
	}
	
	//finds whether user is in the Queue or in a game
	private static String findUser(String userID){
		
		String result = "Not Found";
		
		boolean found = false;
		
		//first look in queue
		for(int index = 0; index < matchQueue.length; index++){
			
			if(matchQueue[index].equals(userID)){
				
				result = "In Queue";
				found = true;
				
			}
			
			
		}
		
		if(!found){
		
			//if its not found .. check if its in a game
			for(int index = 0; index < GAMES.length; index++){
				
				String raw = GAMES[index].getPlayers();
				
				String breakdown[] = raw.split("-");
				
				if(breakdown[0].equals(userID) || breakdown[1].equals(userID)){
					
					result = ""+index;
					found = true;
					
				}
				
			}
			
		}
		
		
		return result;
		
	}
	
	
	//removes finished games from the gameboard
	private static void cleanGames(){
		
		for(int index = 0; index < GAMES.length; index++){
			
			if(GAMES[index].isFinished())
				GAMES = removeGame(GAMES, GAMES[index]);
			
		}
		
	}
	
	
	/////////////////////////////////////
	//
	//  UTILITY FUNCTIONS
	//
	/////////////////////////////////////
	private static String[] enqueue(String[] old, String element){
		
		String result[] = new String[old.length+1];
		
		
		for(int index = 0; index < old.length; index++){
			
			result[index] = old[index];
			
		}
		
		result[old.length] = element;
		
		return result;
		
	}
	
	// note that front of queue must be read first
	private static String[] dequeue(String[] old){
		
		String result[] = new String[old.length-1];
		
		for(int index = 0; index < result.length; index++){
			
			result[index] = old[index+1];
			
		}
		
		return result;
		
	}
	
	
	
	private static Game[] addGame(Game[] old, Game element){
		
		Game result[] = new Game[old.length+1];
		
		for(int index = 0; index < old.length; index++){
			
			result[index] = old[index];
			
		}
		
		result[old.length] = element;
		
		return result;
		
	}
	
	// need to find element to dispose of
	private static Game[] removeGame(Game[] old, Game element){
		
		Game result[] = new Game[old.length-1];
		
		boolean found = false;
		
		for(int index = 0; index < result.length; index++){
			
			if(element.getID() == old[index].getID())
				found = true;
			
			if(found)
				result[index] = old[index+1];
			else
				result[index] = old[index];
			
		}
		
		if(found)
			return result;
		else
			return old;
		
	}
	
	
	
	
	
	private static String[][] addNameKey(String[][] old, String[] element){
		
		String result[][] = new String[old.length+1][2];
		
		for(int index = 0; index < old.length; index++){
			
			result[index] = old[index];
			
		}
		
		result[old.length] = element;
		
		return result;
		
	}
	
	private static String findName(String ID){
		
		String result = "Not Found";
		
		for(int index = 0; index < idNameKey.length; index++){
			
			if(idNameKey[index][0].equals(ID))
				result = idNameKey[index][1];
			
			
		}
		
		return result;
		
	}
	
	
	
	
	
	
	
	}

}



