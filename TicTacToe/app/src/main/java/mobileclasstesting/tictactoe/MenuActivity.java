package mobileclasstesting.tictactoe;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MenuActivity extends Activity {

    public static int SETTINGS_REQUEST = 4444;
    public static int SP_WDL_REQUEST = 4445; //request for WIN DRAW OR LOSS
    public static int MP_WDL_REQUEST = 4446; //request for WIN DRAW OR LOSS

    private static String userName;
    private static String userID; //generate random user ID for Multiplayer
    private static String IP;

    private static String[] Records;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        // need to hide the status bar
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);

        recordsDBhelper helper = new recordsDBhelper(this);

        IP = "184.144.81.180:12876"; //default location, the server's ip at the time

        userID = generateID();

        userName = helper.getRecord()[0];//calls db for saved userName

        Records = new String[6];

        //gets wins draws and losses
        for(int index = 0; index < Records.length; index++){

            Records[index] = helper.getRecord()[index+1];

        }

        TextView userView = (TextView)findViewById(R.id.MenuUsernameView);
        userView.setText("Username: " + userName);

        Button SPButton = (Button)findViewById(R.id.SPButton);
        SPButton.setOnClickListener(new singleAction());

        Button MPButton = (Button)findViewById(R.id.MPButton);
        MPButton.setOnClickListener(new multiAction());

        Button recordsButton = (Button)findViewById(R.id.RecordsButton);
        recordsButton.setOnClickListener(new recordsAction());

        Button settingsButton = (Button)findViewById(R.id.SettingsButton);
        settingsButton.setOnClickListener(new settingsAction());



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_menu, menu);
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

    //listener for the SP button
    public class singleAction implements View.OnClickListener{

        public void onClick(View view){

            Intent singleIntent = new Intent(MenuActivity.this, SinglePlayerActivity.class);

            singleIntent.putExtra("userName", userName);

            startActivityForResult(singleIntent, SP_WDL_REQUEST);


        }

    }


    //listener for the MP button
    public class multiAction implements View.OnClickListener{

        public void onClick(View view){

            userID = generateID(); //regen every queue for unique ID every game (for stability reasons)

            Intent multiIntent = new Intent(MenuActivity.this, MatchActivity.class);

            multiIntent.putExtra("userName", userName);
            multiIntent.putExtra("userID", userID);
            multiIntent.putExtra("IP", IP);

            startActivityForResult(multiIntent, MP_WDL_REQUEST);


        }

    }



    //listener for the records button
    public class recordsAction implements View.OnClickListener{

        public void onClick(View view){

            Intent recordIntent = new Intent(MenuActivity.this, RecordsActivity.class);

            recordIntent.putExtra("SPW", Records[0]);
            recordIntent.putExtra("SPD", Records[1]);
            recordIntent.putExtra("SPL", Records[2]);

            recordIntent.putExtra("MPW", Records[3]);
            recordIntent.putExtra("MPD", Records[4]);
            recordIntent.putExtra("MPL", Records[5]);


            startActivity(recordIntent);


        }

    }



    //listener for the settings button
    public class settingsAction implements View.OnClickListener{

        public void onClick(View view){

            Intent settingsIntent = new Intent(MenuActivity.this, SettingsActivity.class);

            settingsIntent.putExtra("userName", userName);
            settingsIntent.putExtra("IP", IP);

            startActivityForResult(settingsIntent, SETTINGS_REQUEST);


        }

    }






    public void onActivityResult(int requestCode, int responseCode, Intent resultIntent) {
        super.onActivityResult(requestCode, responseCode, resultIntent);

        if (responseCode == RESULT_OK && requestCode == SETTINGS_REQUEST) {

            userName = resultIntent.getStringExtra("userName");
            IP = resultIntent.getStringExtra("IP");

            TextView userView = (TextView)findViewById(R.id.MenuUsernameView);
            userView.setText("Username: " + userName);

            recordsDBhelper helper = new recordsDBhelper(this);

            helper.updateUsername(userName);

        }
        else if (responseCode == RESULT_OK && requestCode == SP_WDL_REQUEST) {

            String outcome = resultIntent.getStringExtra("fate");

            if(!outcome.equals("N")){ // a decision was made that needs to be recorded

                if(outcome.equals("W")){ //player has won in SP

                    int mid = Integer.parseInt(Records[0]);
                    mid++;

                    Records[0] = ""+mid;

                    recordsDBhelper helper = new recordsDBhelper(this);

                    helper.updateRecord(Records);

                }
                else if(outcome.equals("D")){ //player has won in SP

                    int mid = Integer.parseInt(Records[1]);
                    mid++;

                    Records[1] = ""+mid;

                    recordsDBhelper helper = new recordsDBhelper(this);

                    helper.updateRecord(Records);

                }
                else if(outcome.equals("L")){ //player has won in SP

                    int mid = Integer.parseInt(Records[2]);
                    mid++;

                    Records[2] = ""+mid;

                    recordsDBhelper helper = new recordsDBhelper(this);

                    helper.updateRecord(Records);

                }


            }


        }
        else if (responseCode == RESULT_OK && requestCode == MP_WDL_REQUEST) {

            String outcome = resultIntent.getStringExtra("fate");

            if(!outcome.equals("N")){ // a decision was made that needs to be recorded

                if(outcome.equals("W")){ //player has won in SP

                    int mid = Integer.parseInt(Records[3]);
                    mid++;

                    Records[3] = ""+mid;

                    recordsDBhelper helper = new recordsDBhelper(this);

                    helper.updateRecord(Records);

                }
                else if(outcome.equals("D")){ //player has won in SP

                    int mid = Integer.parseInt(Records[4]);
                    mid++;

                    Records[4] = ""+mid;

                    recordsDBhelper helper = new recordsDBhelper(this);

                    helper.updateRecord(Records);

                }
                else if(outcome.equals("L")){ //player has won in SP

                    int mid = Integer.parseInt(Records[5]);
                    mid++;

                    Records[5] = ""+mid;

                    recordsDBhelper helper = new recordsDBhelper(this);

                    helper.updateRecord(Records);

                }


            }


        }

    }






    private String generateID(){

        String result = "";

        //arbitrarily make 12 character string
        for(int index = 0; index < 12; index++){

            char character = (char)((int)(Math.random()*26 + 97));

            result += character;

        }

        return result;

    }



}





