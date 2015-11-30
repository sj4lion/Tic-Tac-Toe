package mobileclasstesting.tictactoe;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

public class SettingsActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // need to hide the status bar
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);



        //get intent to find out what to put into the settings fields
        Intent givenIntent = this.getIntent();
        String userName = givenIntent.getStringExtra("userName");
        String IP = givenIntent.getStringExtra("IP");

        TextView userView = (TextView)findViewById(R.id.usernameField);
        userView.setText(userName);

        TextView IPView = (TextView)findViewById(R.id.IPField);
        IPView.setText(IP);

        Button applyButton = (Button)findViewById(R.id.ApplyButton);
        applyButton.setOnClickListener(new applyAction());

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_settings, menu);
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






    //listener for the apply button
    public class applyAction implements View.OnClickListener{

        public void onClick(View view){

            TextView userView = (TextView)findViewById(R.id.usernameField);
            TextView IPView = (TextView)findViewById(R.id.IPField);

            String userName = userView.getText().toString();
            String IP = IPView.getText().toString();

            if(!userName.equals("") && !IP.equals("")){

                Intent resultIntent = new Intent(Intent.ACTION_PICK);
                resultIntent.putExtra("userName", userName);
                resultIntent.putExtra("IP", IP);
                setResult(RESULT_OK, resultIntent);
                finish();

            }
            else{

                //TODO add action to notify user to put non-blank username

            }


        }

    }






}
