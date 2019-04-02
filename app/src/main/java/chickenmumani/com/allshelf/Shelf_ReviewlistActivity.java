package chickenmumani.com.allshelf;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;

import static java.lang.Boolean.FALSE;

public class Shelf_ReviewlistActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private DatabaseReference mDatabase;
    private int count, allcount, sumrate;
    String uname, bookname, isbn, coverurl;
    Thread mThread1;
    boolean isfav;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shelf_reviewlist);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        bookname = intent.getStringExtra("bookname");
        isbn = intent.getStringExtra("isbn");
        coverurl = intent.getStringExtra("cover");
        user = FirebaseAuth.getInstance().getCurrentUser();

        ((TextView)findViewById(R.id.reviewlist_name)).setText(bookname);
        setTitle(bookname);

        final ImageView proimg = (ImageView) findViewById(R.id.reviewlist_img);
        final ArrayList<String> reviewlist = new ArrayList<String>();

        ((Button)findViewById(R.id.reviewlist_button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Shelf_ReviewlistActivity.this, BookInfo_Activity.class);
                intent.putExtra("barcodeContents",isbn);
                startActivity(intent);
            }
        });

        mThread1 = new Thread() {
            @Override
            public void run() {
                try {
                    URL url = new URL(coverurl);

                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setDoInput(true);
                    conn.connect();

                    InputStream is = conn.getInputStream();
                    final Bitmap apro = BitmapFactory.decodeStream(is);
                    runOnUiThread(new Runnable(){
                        @Override
                        public void run() {
                            proimg.setImageBitmap(apro);
                        }
                    });
                } catch (Exception e) { e.printStackTrace(); }
            }
        };

        mThread1.start();

        mRecyclerView = (RecyclerView) findViewById(R.id.timeline_recyclerview);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(Shelf_ReviewlistActivity.this, DividerItemDecoration.VERTICAL));
        mRecyclerView.setHasFixedSize(true);

        final ArrayList<Post_Item> myList = new ArrayList<Post_Item>();

        mDatabase = FirebaseDatabase.getInstance().getReference("Review").child("Book").child(isbn);

        final Thread mThread = new Thread() {
            @Override
            public void run() {
                myList.clear();
                count = 0;
                sumrate = 0;
                allcount = reviewlist.size();
                runOnUiThread(new Runnable(){
                    @Override
                    public void run() {
                        ((TextView)findViewById(R.id.reviewlist_reviewcount)).setText(String.valueOf(allcount));
                    }
                });
                for(String n : reviewlist) {
                    DatabaseReference mDatabase2 = FirebaseDatabase.getInstance().getReference("Review")
                            .child("ReviewList").child(n);
                    ValueEventListener postListener2 = new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Map<String,Object> map = (Map<String,Object>) dataSnapshot.getValue();
                            Map<String,Object> mapUser = (Map<String,Object>) map.get("UserInfo");
                            Map<String,Object> mapFav = (Map<String,Object>) map.get("Good");
                            if(dataSnapshot.child("Good").child(user.getUid()).getValue() != null) isfav = true;
                            else isfav = false;
                            if(mapUser.get("uid").toString().equals(user.getUid())
                                    || (map.get("OpenRange").toString().equals("1")) && Integer.parseInt(map.get("OpenRange").toString()) <= 4)  {
                                myList.add(new Post_Item(dataSnapshot.getKey(), mapUser.get("uid").toString(), map.get("Book").toString(),
                                        map.get("ISBN").toString(), mapUser.get("proimg").toString(), mapUser.get("name").toString(),
                                        Integer.parseInt(map.get("Rate").toString()), map.get("Time").toString(),
                                        isfav, Integer.parseInt(mapFav.get("Count").toString()),
                                        map.get("Image").toString(), map.get("Text").toString()
                                ));
                            }

                            sumrate += Integer.parseInt(map.get("Rate").toString());
                            count++;
                            if(allcount == count) {
                                DecimalFormat REAL_FORMATTER = new DecimalFormat("0.00");
                                Double avgrate = (double) sumrate / count;
                                ((TextView)findViewById(R.id.reviewlist_sumrate)).setText(REAL_FORMATTER.format(avgrate));
                                Collections.sort(myList,new TimingP());
                                mAdapter = new Post_Adapter(myList, isbn, bookname);
                                mRecyclerView.setAdapter(mAdapter);
                            }
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    };
                    mDatabase2.addValueEventListener(postListener2);
                }
            }
        };


        ValueEventListener postListener3 = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                    try {
                        reviewlist.add(ds.getValue().toString());
                    } catch(Exception e) {
                    }
                }

                mThread.start();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };

        mDatabase.addListenerForSingleValueEvent(postListener3);

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
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

    class TimingP implements Comparator<Post_Item> {
        @Override
        public int compare(Post_Item o1, Post_Item o2) {
            return o2.getDate().compareTo(o1.getDate());
        }

    }
}
