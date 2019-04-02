package chickenmumani.com.allshelf;

import android.content.Intent;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Friend_ListActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private DatabaseReference mDatabase;
    ArrayList<Friend_ListItem> myList;
    String fol, uid;
    int sumcount, count;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_list);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Intent intent = getIntent();
        fol = intent.getStringExtra("fol");
        uid = intent.getStringExtra("uid");
        myList = new ArrayList<Friend_ListItem>();
        count = 0;
        sumcount = -1;

        mRecyclerView = (RecyclerView) findViewById(R.id.flist_recyclerview);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(Friend_ListActivity.this, DividerItemDecoration.VERTICAL));
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        if(fol.equals("follower")) {
            setTitle("팔로워");
            mDatabase = FirebaseDatabase.getInstance().getReference("User_Friend")
                    .child("Follower").child(uid);
        } else {
            setTitle("팔로잉");
            mDatabase = FirebaseDatabase.getInstance().getReference("User_Friend")
                    .child("Following").child(uid);
        }

        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                sumcount = (int)dataSnapshot.getChildrenCount();
                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                    final String uidi = ds.getKey();
                    final String unamei = ds.getValue().toString();
                    DatabaseReference mDatabase2 = FirebaseDatabase.getInstance().getReference("User_Info")
                            .child(ds.getKey()).child("Profile_Image");
                    mDatabase2.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            myList.add(new Friend_ListItem(uidi, unamei, dataSnapshot.getValue().toString()));
                            count++;
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {}
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });

        final Thread mThread = new Thread() {
            public void run() {
                while(true) {
                    if(count == sumcount) {
                        Runnable runui = new Runnable(){
                            @Override
                            public void run() {
                                mAdapter = new Friend_ListAdapter(myList);
                                mRecyclerView.setAdapter(mAdapter);
                                synchronized(this)
                                {
                                    this.notify();
                                }
                            }
                        };

                        synchronized( runui ) {
                            runOnUiThread(runui);
                            try {
                                runui.wait();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                        }
                        break;
                    }

                    try {
                        sleep(300);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }
        };
        mThread.start();

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
}
