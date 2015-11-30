package mobileclasstesting.tictactoe;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class SinglePlayerActivity extends Activity {

    private String GAMEBOARD;

    private int CANVAS_WIDTH;
    private int CANVAS_HEIGHT;
    private int CANVAS_Y; //needed for height offset of canvas

    private static char PlayersTurn;
    private static char AITurn;
    private static char currentTurn;
    private static boolean gameEnded;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_player);

        // need to hide the status bar
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);


        int turnGenerator = (int)(Math.random()*1.99);
        if(turnGenerator == 0) {
            PlayersTurn = 'X';
            AITurn = 'O';
        }
        else {
            PlayersTurn = 'O';
            AITurn = 'X';
        }

        TextView teamView = (TextView)findViewById(R.id.SPTeamView);
        teamView.setText("You Are Team: " + PlayersTurn);

        GAMEBOARD = "EEEEEEEEE"; //blank board

        currentTurn = 'O'; //X always goes first, start this with O and invert to get labels right
        switchTurn(); //call now to get the labels right

        gameEnded = false;


        Button quitButton = (Button)findViewById(R.id.SPQuitButton);
        quitButton.setOnClickListener(new quitAction());

        Thread control = new controlThread();
        control.start(); //run thread concurrently to control the game flow



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_single_player, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onBackPressed() {
    }



    //listener for the quit button
    public class quitAction implements View.OnClickListener{

        public void onClick(View view){

            if(gameEnded){

                finish();

            }
            else{ //player leaves before game ends, no decision

                Intent resultIntent = new Intent(Intent.ACTION_PICK);
                resultIntent.putExtra("fate", "N"); // N for NO DECISION
                setResult(RESULT_OK, resultIntent);

                finish();

            }


        }

    }





    //listen for screen touches that are probably game moves
    @Override
    public boolean onTouchEvent(MotionEvent e){

        //need to get size of gameboard
        GameBoardCanvas GameCanvas = (GameBoardCanvas)findViewById(R.id.SPBoardCanvas);
        CANVAS_WIDTH = GameCanvas.getWidth();
        CANVAS_HEIGHT = GameCanvas.getHeight();

        CANVAS_Y = (int)GameCanvas.getY(); // needed because title bar causes vertical offset


        int x = (int)e.getX();
        int y = (int)e.getY();



        //screen clicked
        if(e.getAction() == 0 && currentTurn == PlayersTurn){

            int spot = -1; //start at -1,

            //check if user clicked spot on gameboard
            if(x < CANVAS_WIDTH/3 && y < (CANVAS_HEIGHT/3)+CANVAS_Y)
                spot = 0; //top left
            else if(x >= CANVAS_WIDTH/3 && x < (2*CANVAS_WIDTH)/3 && y < (CANVAS_HEIGHT/3)+CANVAS_Y)
                spot = 1; //top middle
            else if(x >= (2*CANVAS_WIDTH)/3 && y < (CANVAS_HEIGHT/3)+CANVAS_Y)
                spot = 2; //top right
            else if(x < CANVAS_WIDTH/3 && y >= (CANVAS_HEIGHT/3)+CANVAS_Y && y < ((2*CANVAS_HEIGHT)/3)+CANVAS_Y)
                spot = 3; //middle left
            else if(x >= CANVAS_WIDTH/3 && x < (2*CANVAS_WIDTH)/3 && y >= (CANVAS_HEIGHT/3)+CANVAS_Y && y < ((2*CANVAS_HEIGHT)/3)+CANVAS_Y)
                spot = 4; //middle middle
            else if(x >= (2*CANVAS_WIDTH)/3 && y >= (CANVAS_HEIGHT/3)+CANVAS_Y && y < ((2*CANVAS_HEIGHT)/3)+CANVAS_Y)
                spot = 5; //middle right
            else if(x < CANVAS_WIDTH/3 && y >= ((2*CANVAS_HEIGHT)/3)+CANVAS_Y)
                spot = 6; //bottom left
            else if(x >= CANVAS_WIDTH/3 && x < (2*CANVAS_WIDTH)/3 && y >= ((2*CANVAS_HEIGHT)/3)+CANVAS_Y)
                spot = 7; //bottom middle
            else if(x >= (2*CANVAS_WIDTH)/3 && y >= ((2*CANVAS_HEIGHT)/3)+CANVAS_Y)
                spot = 8; //bottom right


            if(spot != -1){

                int success = makeMove(spot, PlayersTurn);

                if(success == 0) {
                    runOnUiThread(new Runnable() { // this looks really ugly but this thread needs access to Views

                        @Override
                        public void run() {

                            switchTurn();

                        }

                    });

                }

            }

        }

        return true;

    }


    private void switchTurn(){

        if(currentTurn == 'X')
            currentTurn = 'O';
        else
            currentTurn = 'X';

        char status = checkVictory();

        boolean decision = (status != 'I');


        TextView statusView = (TextView)findViewById(R.id.SPStatusView);

        if(PlayersTurn == currentTurn && !decision){

            statusView.setText("It is your turn.");

        }
        else if(PlayersTurn != currentTurn && !decision){

            statusView.setText("The ai is making a move . . .");

        }
        else{

            statusView.setText(""); // this will be set by the control thread

        }

    }

    // runs concurrently to control the flow of the game
    private class controlThread extends Thread{

        public void run(){

            while(!gameEnded){

                try{
                    Thread.sleep(3000); //sleep to give the illusion the ai is thinking
                }
                catch(InterruptedException e){

                    //do nothing, another fatal is probably coming if this is happening

                }


                // check here is the game is over
                char status = checkVictory();

                if(status != 'I')
                    gameEnded = true;

                if(currentTurn != PlayersTurn && !gameEnded){ //play for the ai

                    SPAI singlePlayerAI = new SPAI();

                    int move = singlePlayerAI.getMove(GAMEBOARD);

                    int success = makeMove(move, AITurn);

                    if(success == 0){

                        runOnUiThread(new Runnable() { // this looks really ugly but this thread needs access to Views

                            @Override
                            public void run() {

                                switchTurn();

                            }

                        });

                    }

                }

            }

            char status = checkVictory();
            String fate = "";


            if(status == PlayersTurn){

                fate += 'W'; //player has won
                runOnUiThread(new Runnable() { // this looks really ugly but this thread needs access to Views

                    @Override
                    public void run() {

                        TextView statusView = (TextView)findViewById(R.id.SPStatusView);
                        statusView.setText("YOU HAVE WON!");

                    }

                });

            }
            else if(status == 'D'){

                fate += 'D'; //player has tied
                runOnUiThread(new Runnable() { // this looks really ugly but this thread needs access to Views

                    @Override
                    public void run() {

                        TextView statusView = (TextView)findViewById(R.id.SPStatusView);
                        statusView.setText("The Game is a Draw.");

                    }

                });

            }
            else{

                fate += 'L'; //player has tied
                runOnUiThread(new Runnable() { // this looks really ugly but this thread needs access to Views

                    @Override
                    public void run() {

                        TextView statusView = (TextView)findViewById(R.id.SPStatusView);
                        statusView.setText("You have lost.");

                    }

                });

            }



            Intent resultIntent = new Intent(Intent.ACTION_PICK);
            resultIntent.putExtra("fate", fate);
            setResult(RESULT_OK, resultIntent);
            //finish();

        }

    }


    private int makeMove(int index, char character){

        int success = 0;

        GameBoardCanvas GameCanvas = (GameBoardCanvas)findViewById(R.id.SPBoardCanvas);


        if(GAMEBOARD.charAt(index) == 'E'){ //spot is empty, make move

            GAMEBOARD = changeCharacter(GAMEBOARD, index, character);
            GameCanvas.setGameBoard(GAMEBOARD);

        }
        else{

            success = 1; //failed

        }

        return success;

    }







    //used to change a character on the gameboard string
    private static String changeCharacter(String given, int atIndex, char character){

        String result = "";

        for(int index = 0; index < given.length(); index++){

            if(index == atIndex)
                result += character;
            else
                result += given.charAt(index);

        }


        return result;

    }


    //parses board for victory conditions
    private char checkVictory(){

        char result = 'I'; //incomplete, X, Y, D = X wins, Y wins , Draw

        //first check if a line has been made anywhere
        //there are 8 possible lines to win with
        if(GAMEBOARD.charAt(0) != 'E'){

            //top horizontal line
            if(GAMEBOARD.charAt(0) == GAMEBOARD.charAt(1) && GAMEBOARD.charAt(0) == GAMEBOARD.charAt(2)){

                result = GAMEBOARD.charAt(0);

            }
            //left vertical line
            else if(GAMEBOARD.charAt(0) == GAMEBOARD.charAt(3) && GAMEBOARD.charAt(0) == GAMEBOARD.charAt(6)){

                result = GAMEBOARD.charAt(0);

            }
            //diagonal from top left
            else if(GAMEBOARD.charAt(0) == GAMEBOARD.charAt(4) && GAMEBOARD.charAt(0) == GAMEBOARD.charAt(8)){

                result = GAMEBOARD.charAt(0);

            }

        }



        if(GAMEBOARD.charAt(1) != 'E'){

            //middle vertical line
            if(GAMEBOARD.charAt(1) == GAMEBOARD.charAt(4) && GAMEBOARD.charAt(1) == GAMEBOARD.charAt(7)){

                result = GAMEBOARD.charAt(1);

            }

        }


        if(GAMEBOARD.charAt(2) != 'E'){

            //diagonal from top right
            if(GAMEBOARD.charAt(2) == GAMEBOARD.charAt(4) && GAMEBOARD.charAt(2) == GAMEBOARD.charAt(6)){

                result = GAMEBOARD.charAt(2);

            }
            //right vertial
            else if(GAMEBOARD.charAt(2) == GAMEBOARD.charAt(5) && GAMEBOARD.charAt(2) == GAMEBOARD.charAt(8)){

                result = GAMEBOARD.charAt(2);

            }

        }



        if(GAMEBOARD.charAt(3) != 'E'){

            //middle horizontal
            if(GAMEBOARD.charAt(3) == GAMEBOARD.charAt(4) && GAMEBOARD.charAt(3) == GAMEBOARD.charAt(5)){

                result = GAMEBOARD.charAt(3);

            }


        }



        if(GAMEBOARD.charAt(6) != 'E'){

            //bottom horizontal
            if(GAMEBOARD.charAt(6) == GAMEBOARD.charAt(7) && GAMEBOARD.charAt(6) == GAMEBOARD.charAt(8)){

                result = GAMEBOARD.charAt(6);

            }


        }
        //check whether game is a draw

        boolean checkGameEnd = true;

        for(int index = 0; index < 9; index++){

            if(GAMEBOARD.charAt(index) == 'E')
                checkGameEnd = false;

        }

        if(checkGameEnd && result == 'I')
            result = 'D';



        Log.i("GAME STATE", "" + result);


        return result;

    }


}
