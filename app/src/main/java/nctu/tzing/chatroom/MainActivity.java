package nctu.tzing.chatroom;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import nctu.fintech.appmate.Table;
import nctu.fintech.appmate.Tuple;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    EditText mEt;    // input string
    TextView mTv;   // show message

    Table mTable;   // server connection

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTable = new Table("HOST", "TABLE", "USER", "PASSWD"); // server connection
        mEt = (EditText) findViewById(R.id.message);    // input string
        findViewById(R.id.send).setOnClickListener(this); // set button listener
        mTv = (TextView) findViewById(R.id.show_msg);  // place to show message

        // timer
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                new Get().execute();
            }
        }, 0, 3000);
    }

    @Override
    public void onClick(View v) {
        new Post().execute(mEt.getText().toString());
    }

    class Post extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {
            for (String msg : params) {
                Tuple tuple = new Tuple();
                tuple.put("char32", "demo");
                tuple.put("char64", msg);
                try {
                    mTable.add(tuple);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            mEt.getText().clear();
        }
    }

    class Get extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... params) {
            try {
                Tuple[] items = mTable.get();

                StringBuilder sb = new StringBuilder();
                for (Tuple item : items) {
                    String nm = item.get("char32");
                    if (nm == null) {
                        nm = "NOBODY";
                    }

                    String msg = item.get("char64");
                    if (msg == null) {
                        msg = "null";
                    }

                    sb.append(nm + ": " + msg + "\n");
                }

                return sb.toString();

            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String items) {
            super.onPostExecute(items);
            if (items == null) {
                return;
            }
            mTv.setText(items);
        }
    }
}
