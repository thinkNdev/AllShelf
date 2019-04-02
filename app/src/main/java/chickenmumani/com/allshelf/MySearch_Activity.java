package chickenmumani.com.allshelf;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class MySearch_Activity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private DatabaseReference mDatabase;

    private EditText searchEditText;
    private ImageButton searchButton;

    private String searchText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mysearch);
        setTitle("내서재 검색");
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = new Intent(this.getIntent());
        searchText = intent.getStringExtra("search");
        searchEditText = (EditText)findViewById(R.id.mysearch_edittext);
        searchEditText.setHint("찾으시는 책 제목을 입력해주세요.");
        searchEditText.setText(searchText);
        searchButton = (ImageButton)findViewById(R.id.mysearch_button);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchText = searchEditText.getText().toString();
                mDatabase = FirebaseDatabase.getInstance().getReference("User_Book").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                mDatabase.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        final ArrayList<Shelf_Item> myList = new ArrayList<Shelf_Item>();
                        myList.clear();
                        for(DataSnapshot ds : dataSnapshot.getChildren()) {
                            Map<String,Object> map = (Map<String,Object>) ds.getValue();
                            try{
                                if (map.get("title").toString().contains(searchText)) {
                                    myList.add(new Shelf_Item(map.get("isbn").toString(), map.get("title").toString(), map.get("author").toString(), map.get("imgurl").toString(), map.get("isbn").toString()));
                                }
                            }
                            catch(NullPointerException e){
                                e.printStackTrace();
                            }
                        }
                        Collections.sort(myList, new TimingN3());
                        mAdapter = new Shelf_ImageAdapter(myList, "t");
                        mRecyclerView.setAdapter(mAdapter);

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });

        mRecyclerView = (RecyclerView)findViewById(R.id.mysearch_recyclerview);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        final ArrayList<Shelf_Item> myList = new ArrayList<Shelf_Item>();

        mDatabase = FirebaseDatabase.getInstance().getReference("User_Book").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                myList.clear();
                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                    Map<String,Object> map = (Map<String,Object>) ds.getValue();
                    try{
                        if (map.get("title").toString().contains(searchText)) {
                            myList.add(new Shelf_Item(map.get("isbn").toString(), map.get("title").toString(), map.get("author").toString(), map.get("imgurl").toString(), map.get("isbn").toString()));
                        }
                    }
                    catch(NullPointerException e){
                        e.printStackTrace();
                    }
                }
                Collections.sort(myList, new TimingN3());
                mAdapter = new Shelf_ImageAdapter(myList, "t");
                mRecyclerView.setAdapter(mAdapter);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

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

    class TimingN3 implements Comparator<Shelf_Item> {
        @Override
        public int compare(Shelf_Item o1, Shelf_Item o2) {
            return o2.getTime().compareTo(o1.getTime());
        }
    }
}