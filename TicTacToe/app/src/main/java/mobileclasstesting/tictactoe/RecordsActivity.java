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

public class RecordsActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_records);

        // need to hide the status bar
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);



        Intent requestIntent = this.getIntent();

        //gets records from Main activity
        String SPWins = requestIntent.getStringExtra("SPW");
        String SPDraws = requestIntent.getStringExtra("SPD");
        String SPLosses = requestIntent.getStringExtra("SPL");

        String MPWins = requestIntent.getStringExtra("MPW");
        String MPDraws = requestIntent.getStringExtra("MPD");
        String MPLosses = requestIntent.getStringExtra("MPL");

        TextView SPWView = (TextView)findViewById(R.id.SPWinsView);
        TextView SPDView = (TextView)findViewById(R.id.SPDrawsView);
        TextView SPLView = (TextView)findViewById(R.id.SPLossesView);

        TextView MPWView = (TextView)findViewById(R.id.MPWinsView);
        TextView MPDView = (TextView)findViewById(R.id.MPDrawsView);
        TextView MPLView = (TextView)findViewById(R.id.MPLossesView);

        SPWView.setText(SPWins);
        SPDView.setText(SPDraws);
        SPLView.setText(SPLosses);

        MPWView.setText(MPWins);
        MPDView.setText(MPDraws);
        MPLView.setText(MPLosses);


        Button backButton = (Button)findViewById(R.id.RecordsBackButton);
        backButton.setOnClickListener(new backAction());


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_records, menu);
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


    //listener for the back button
    public class backAction implements View.OnClickListener{

        public void onClick(View view){

            finish();

        }

    }


}
