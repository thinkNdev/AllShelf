package chickenmumani.com.allshelf;

import android.content.Context;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class Search_Activity extends AppCompatActivity {

    private String TTBKey = "ttbybson20100846003"; // Aladin Open API TTBKEY 값
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        setTitle("검색");
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mRecyclerView = (RecyclerView) findViewById(R.id.search_recyclerview);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(Search_Activity.this, DividerItemDecoration.VERTICAL));

        mRecyclerView.setHasFixedSize(true);

        ImageButton btn = (ImageButton) findViewById(R.id.mysearch_button);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                search(view);
            }
        });

        EditText etn = (EditText) findViewById(R.id.search_edittext);
        etn.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    search(v);
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void search(View view) {
        String Query = null;
        String str = null;

        EditText etn = (EditText) findViewById(R.id.search_edittext);
        ArrayList<Search_Item> searchInfos = new ArrayList<Search_Item>();

        for(int j = 0; j != 4; j++) {

            Query = "http://www.aladin.co.kr/ttb/api/ItemSearch.aspx?ttbkey="+TTBKey+
                    "&Query="+etn.getText()+"&QueryType=Title&MaxResults=50&start=" +
                    j + "&SearchTarget=Book&output=js&Version=20131101";

            try{
                URL url = new URL(Query);

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");

                if (android.os.Build.VERSION.SDK_INT > 9)
                {
                    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                    StrictMode.setThreadPolicy(policy);
                }

                if (conn.getResponseCode() == conn.HTTP_OK){
                    InputStreamReader temp = new InputStreamReader(conn.getInputStream(), "UTF-8");
                    BufferedReader bReader = new BufferedReader(temp);
                    StringBuffer buffer = new StringBuffer();

                    while((str=bReader.readLine()) != null){
                        buffer.append(str);
                    }
                    bReader.close();
                    str = buffer.toString();

                    JSONArray jarray = new JSONObject(str).getJSONArray("item");

                    for (int i = 0; i < jarray.length(); i++) {
                        HashMap hs = new HashMap<>();
                        JSONObject jObject = jarray.getJSONObject(i);

                        String tBf = null;
                        String aBf = null;
                        String pBf = null;
                        String iBf = null;
                        String cBf = null;

                        tBf = jObject.optString("title");
                        pBf = jObject.optString("publisher");
                        aBf = jObject.optString("author");
                        iBf = jObject.optString("isbn13");
                        cBf = jObject.optString("cover");

                        searchInfos.add(new Search_Item(tBf, pBf, aBf, iBf, cBf));

                    }
                    Log.d("w",String.valueOf(searchInfos.size()));

                    Context context = view.getContext();
                    mRecyclerView = (RecyclerView) findViewById(R.id.search_recyclerview);

                    mRecyclerView.setHasFixedSize(true);

                    // use a linear layout manager
                    mLayoutManager = new LinearLayoutManager(context);
                    mRecyclerView.setLayoutManager(mLayoutManager);

                    // specify an adapter (see also next example)
                    mAdapter = new Search_Adapter(searchInfos);
                    mRecyclerView.setAdapter(mAdapter);

                } else {
                    Log.i("Err = ", conn.getResponseCode() + "Error");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
}
