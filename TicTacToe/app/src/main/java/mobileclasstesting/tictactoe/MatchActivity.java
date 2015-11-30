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

//contacts server and waits for match to be made
public class MatchActivity extends Activity {

    public static int MP_WDL_REQUEST = 4446; //request for WIN DRAW OR LOSS

    private static boolean responded;
    private static String threadResponse;

    private static String userID;
    private static String userName;
    private static String IP;

    boolean activityActive;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match);

        activityActive = true;

        threadResponse = "";

        Button quitButton = (Button)findViewById(R.id.MatchQuitButton);
        quitButton.setOnClickListener(new quitAction());

        Intent requestIntent = this.getIntent();

        userName = requestIntent.getStringExtra("userName");
        userID = requestIntent.getStringExtra("userID");
        IP = requestIntent.getStringExtra("IP");

        String request = "http://" + IP + "/?request=MatchMake&ID=" + userID + "&userName=" + userName;

        Thread inetThread = new InternetThread(request);

        responded = false;

        inetThread.start();

        while(!responded){

            //wait for response

        }

        String response = threadResponse;

        if(response.equals("You have been added to the Queue")) {

            Thread waitThread = new WaitThread();
            waitThread.start();

        }
        else{

            //TODO server not found make proper statement
            Intent resultIntent = new Intent(Intent.ACTION_PICK);
            resultIntent.putExtra("fate", "N"); // game not completed
            setResult(RESULT_OK, resultIntent);
            finish();

        }



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_match, menu);
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

            String request = "http://" + IP + "/?request=LeaveQueue&ID=" + userID;

            Thread inetThread = new InternetThread(request);

            responded = false;

            inetThread.start();

            while(!responded){

                //wait for response

            }

            String response = threadResponse;

            activityActive = false;

            Intent resultIntent = new Intent(Intent.ACTION_PICK);
            resultIntent.putExtra("fate", "N"); // game not completed
            setResult(RESULT_OK, resultIntent);
            finish();

        }

    }



    //pass result of game back to menu
    public void onActivityResult(int requestCode, int responseCode, Intent resultIntent) {
        super.onActivityResult(requestCode, responseCode, resultIntent);


        if (responseCode == RESULT_OK && requestCode == MP_WDL_REQUEST) {

            String outcome = resultIntent.getStringExtra("fate");

            Intent passIntent = new Intent(Intent.ACTION_PICK);
            passIntent.putExtra("fate", outcome); // game not completed
            setResult(RESULT_OK, passIntent);
            finish();

        }

    }



    public class WaitThread extends Thread{

        public void run() {

            String opponent = "Not Found";

            boolean connected = false;

            //wait for opponent to be found
            Log.i("ActivityActive", ""+activityActive);
            while (!connected && activityActive) {

                Log.i("ActivityActive", ""+activityActive);

                try {

                    Thread.sleep(5000);

                } catch (Exception e) {

                    //do nothing

                }

                String request = "http://" + IP + "/?request=MatchMakeStatus&ID=" + userID;

                Thread inetThread = new InternetThread(request);

                responded = false;

                inetThread.start();

                while (!responded) {

                    //wait for response

                }

                String response = threadResponse;

                if (!response.equals("Player still in queue") && !response.equals("Player not found")) {

                    opponent = response.split("-")[1];

                    //opponent found start next activity
                    Intent singleIntent = new Intent(MatchActivity.this, MultiPlayerActivity.class);

                    singleIntent.putExtra("userName", userName);
                    singleIntent.putExtra("userID", userID);
                    singleIntent.putExtra("IP", IP);

                    startActivityForResult(singleIntent, MP_WDL_REQUEST);

                    connected = true;

                }


            }


        }


    }



    //thread uses internets
    public class InternetThread extends Thread {

        private String request;

        public InternetThread(String r){

            request  = r;

        }


        public void run() {

            ServerHelper SH = new ServerHelper();

            Log.i("Request", request);
            threadResponse = SH.Get(request);
            Log.i("Response", threadResponse);

            responded = true;

        }

    }


}
