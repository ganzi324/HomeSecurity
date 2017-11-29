package kr.ac.hansung.homesecurity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.iid.FirebaseInstanceId;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by sky on 2017-11-21.
 */

public class MainActivity extends AppCompatActivity {

    TextView txt_result;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        TextView txt_idToken = findViewById(R.id.id_token);
        txt_idToken.setText(refreshedToken);

        txt_result = findViewById(R.id.result);

        Button btn_send = findViewById(R.id.send);
        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new HttpPost().execute(refreshedToken);
            }
        });

    }

    public class HttpPost extends AsyncTask<String, Void, Void> {
        HttpURLConnection conn = null;

        @Override
        public Void doInBackground(String... params) {
            try {
                EditText edit_uri = findViewById(R.id.url);
                String url = "http://" + edit_uri.getText().toString().trim();

                URL obj = new URL(url);
                conn = (HttpURLConnection) obj.openConnection();
                conn.setReadTimeout(8000);
                conn.setConnectTimeout(8000);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);
                conn.setRequestProperty("Accept-Charset", "UTF-8");
                conn.setRequestProperty("Content-Type","text/html");
                conn.setRequestProperty("Context_Type", "application/x-www-form-urlencoded;cahrset=UTF-8");

                String param = "id_token=" + params[0];
                Log.i("httpPost", "params[0]:" + params[0]);

                OutputStream os = conn.getOutputStream();
                os.write(param.getBytes("UTF-8"));
                os.flush();
                os.close();

                if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    Log.w("httpPost", "connection failed");
                    return null;
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (conn != null)
                    conn.disconnect();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));

                String line;
                String page = "";
                while ((line = reader.readLine()) != null) {
                    page += line;
                }
                txt_result.setText("response:" + page);
            } catch (IOException e) {
                txt_result.setText("connection failed");
            }
        }
    }
}
