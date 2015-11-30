package com.runtime;

public class Game {
	
	private int GameID;
	
	private String GAMEBOARD; //status of gameboard
	private String playerX; //player ID of team X
	private String playerO; //player ID of team O
	private int finishCount; // if =2, 
	//then both players have acknowledged they are done with the game
	//so it can be disposed of
	
	public Game(int ID, String X, String O){
		
		GameID = ID;
		
		GAMEBOARD = "EEEEEEEEE"; //empty board
		
		playerX = X;
		playerO = O;
		
		finishCount = 0;
		
	}
	
	public int getID(){
		
		return GameID;
		
	}
	
	public String getGameBoard(){
		
		return GAMEBOARD;
		
	}
	
	public void setGAMEBOARD(String newBoard){
		
		GAMEBOARD = newBoard;
		
	}
	
	
	
	public String getPlayers(){
		
		return playerX + "-" + playerO;
		
	}
	
	public void playerFinished(){
		
		finishCount++;
		
	}
	
	public boolean isFinished(){
		
		boolean result = (finishCount >= 2);
		
		return result;
		
	}
	
	public boolean oneLeft(){
		
		boolean result = (finishCount == 1);
		
		return result;
		
	}
	
	
}
