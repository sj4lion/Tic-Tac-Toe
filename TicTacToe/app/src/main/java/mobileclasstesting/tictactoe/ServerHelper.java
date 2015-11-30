package mobileclasstesting.tictactoe;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;


public class ServerHelper {

    public String Get(String request){

        String response = "FAILED";

        try {

            URL url = new URL(request);
            URLConnection conn = url.openConnection();
            InputStream is = conn.getInputStream();

            BufferedReader reader = new BufferedReader(new InputStreamReader(is));

            response = reader.readLine();

            System.out.println(response);

        }
        catch(Exception e){

            e.printStackTrace();

            response = "EXCEPTION CAUGHT";

        }

        return response;

    }

}
