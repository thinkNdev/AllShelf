package chickenmumani.com.allshelf;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.provider.ContactsContract;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import static java.lang.Boolean.FALSE;

public class Shelf_TimelineActivity extends AppCompatActivity {
    private DatabaseReference mDatabase , mDatabase2, mDatabase4;
    private ViewPager mViewPager;
    private TabLayout mTabLayout;
    private String uid, uname, userImageUrls;
    private Thread userImageThread;
    private Drawable upro;
    private ImageView proimg;
    private TextView nameT;

    private int count, allcount;
    FirebaseUser user;
    boolean following, isfav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shelf_timeline);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        uid = intent.getStringExtra("uid");
        uname = intent.getStringExtra("uname");
        user = FirebaseAuth.getInstance().getCurrentUser();
        setTitle(uname);

        proimg = (ImageView)findViewById(R.id.timeline_img);
        nameT = (TextView) findViewById(R.id.timeline_name);
        nameT.setText(uname);

        mViewPager = (ViewPager)findViewById(R.id.timeline_pager);
        mViewPager.setAdapter(new PagerAdapter(getSupportFragmentManager()));
        mViewPager.setCurrentItem(0);

        mTabLayout = (TabLayout)findViewById(R.id.timeline_tab_layout);
        mTabLayout.setupWithViewPager(mViewPager);
        mTabLayout.getTabAt(0).setText("리뷰");
        mTabLayout.getTabAt(1).setText("서재");
        mTabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mViewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });

        userImageThread = new Thread() {
            @Override
            public void run() {
                try {
                    URL url = new URL(userImageUrls);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setDoInput(true);
                    conn.connect();

                    InputStream is = conn.getInputStream();
                    final Bitmap apro = BitmapFactory.decodeStream(is);
                    runOnUiThread(new Runnable(){
                        @Override
                        public void run() {
                            proimg.setImageBitmap(apro);
                            proimg.setBackground(new ShapeDrawable(new OvalShape()));
                            proimg.setClipToOutline(true);
                            upro = proimg.getDrawable();
                        }
                    });
                } catch (Exception e) { e.printStackTrace(); }
            }
        };

        mDatabase = FirebaseDatabase.getInstance().getReference("User_Info").child(uid).child("Profile_Image");
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userImageUrls = (String)dataSnapshot.getValue();
                userImageThread.start();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        mDatabase.addListenerForSingleValueEvent(postListener);

        if(user.getUid().equals(uid)) {
            ((Button)findViewById(R.id.timeline_button)).setClickable(false);
            ((Button)findViewById(R.id.timeline_button)).setBackgroundTintList(ContextCompat.getColorStateList(getApplicationContext(), android.R.color.darker_gray));
            ((Button)findViewById(R.id.timeline_button)).setTextColor(Color.BLACK);
        } else {
            mDatabase4 = FirebaseDatabase.getInstance().getReference("User_Friend").child("Following").child(uid).child(user.getUid());
            ValueEventListener postListener4 = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot.getValue() != null) {
                        ((Button)findViewById(R.id.timeline_button)).setText("팔로우 해제");
                        ((Button)findViewById(R.id.timeline_button)).setBackgroundTintList
                                (ContextCompat.getColorStateList(getApplicationContext(), android.R.color.white));
                        ((Button)findViewById(R.id.timeline_button)).setTextColor(Color.BLACK);
                        following = true;
                    } else following = false;
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {}
            };
            mDatabase4.addListenerForSingleValueEvent(postListener4);
            ((Button)findViewById(R.id.timeline_button)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(following == false) {
                        mDatabase4.setValue(user.getDisplayName());
                        FirebaseDatabase.getInstance().getReference("User_Friend").
                                child("Follower").child(user.getUid()).child(uid).setValue(uname);
                        ((Button)findViewById(R.id.timeline_button)).setText("팔로우 해제");
                        ((Button)findViewById(R.id.timeline_button)).setBackgroundTintList
                                (ContextCompat.getColorStateList(getApplicationContext(), android.R.color.white));
                        ((Button)findViewById(R.id.timeline_button)).setTextColor(Color.BLACK);
                        following = true;
                    } else {
                        mDatabase4.setValue(null);
                        FirebaseDatabase.getInstance().getReference("User_Friend").
                                child("Follower").child(user.getUid()).child(uid).setValue(null);
                        ((Button)findViewById(R.id.timeline_button)).setText("팔로우");
                        ((Button)findViewById(R.id.timeline_button)).setBackgroundTintList
                                (ContextCompat.getColorStateList(getApplicationContext(), R.color.colorAccent));
                        ((Button)findViewById(R.id.timeline_button)).setTextColor(Color.WHITE);
                        following = false;
                    }
                }
            });
        }

        ValueEventListener postListener1 = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ((TextView)findViewById(R.id.timeline_followercount)).setText(String.valueOf(dataSnapshot.getChildrenCount()));
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        };
        FirebaseDatabase.getInstance().getReference("User_Friend").child("Follower").child(uid).addValueEventListener(postListener1);

        ValueEventListener postListener2 = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ((TextView)findViewById(R.id.timeline_followingcount)).setText(String.valueOf(dataSnapshot.getChildrenCount()));
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        };
        FirebaseDatabase.getInstance().getReference("User_Friend").child("Following").child(uid).addValueEventListener(postListener2);

        ((TextView)findViewById(R.id.timeline_followercount)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Shelf_TimelineActivity.this, Friend_ListActivity.class);
                intent.putExtra("fol", "follower");
                intent.putExtra("uid", uid);
                startActivity(intent);
            }
        });

        ((TextView)findViewById(R.id.timeline_followingcount)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Shelf_TimelineActivity.this, Friend_ListActivity.class);
                intent.putExtra("fol", "following");
                intent.putExtra("uid", uid);
                startActivity(intent);
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

    private class PagerAdapter extends FragmentStatePagerAdapter{
        public PagerAdapter(FragmentManager fm) { super(fm); }

        @Override public Fragment getItem(int position) {
            if( position == 0 ) {
                Bundle bundle = new Bundle();
                bundle.putString("uid", uid);
                bundle.putString("uname", uname);
                Timeline_Review_Fragment fragment = new Timeline_Review_Fragment();
                fragment.setArguments(bundle);
                return fragment; }
            else {
                Bundle bundle = new Bundle();
                bundle.putString("uid", uid);
                Timeline_Library_Fragment fragment = new Timeline_Library_Fragment();
                fragment.setArguments(bundle);
                return fragment; }
        }

        @Override public int getCount() {
            return 2;
        }
    }
}

/*
Intent intent = getIntent();
        final String uid = intent.getStringExtra("uid");
        uname = intent.getStringExtra("uname");

        user = FirebaseAuth.getInstance().getCurrentUser();

        final ImageView proimg = (ImageView) findViewById(R.id.timeline_img);
        TextView nameT = (TextView) findViewById(R.id.timeline_name);
        nameT.setText(uname);
        final ArrayList<String> reviewlist = new ArrayList<String>();

        mThread1 = new Thread() {
            @Override
            public void run() {
                try {
                    URL url = new URL(urls);

                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setDoInput(true);
                    conn.connect();

                    InputStream is = conn.getInputStream();
                    final Bitmap apro = BitmapFactory.decodeStream(is);
                    runOnUiThread(new Runnable(){
                        @Override
                        public void run() {
                            proimg.setImageBitmap(apro);
                            proimg.setBackground(new ShapeDrawable(new OvalShape()));
                            proimg.setClipToOutline(true);
                            upro = proimg.getDrawable();
                        }
                    });
                } catch (Exception e) { e.printStackTrace(); }
            }
        };

        mDatabase = FirebaseDatabase.getInstance().getReference("User_Info").child(uid).child("Profile_Image");
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                urls = (String)dataSnapshot.getValue();
                mThread1.start();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        mDatabase.addListenerForSingleValueEvent(postListener);

        final ArrayList<Post_Item> myList = new ArrayList<Post_Item>();

        mDatabase = FirebaseDatabase.getInstance().getReference("Review")
                .child("User").child(uid);
        try {
            mThread1.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        final Thread mThread = new Thread() {
            @Override
            public void run() {
                myList.clear();
                count = 0;
                allcount = reviewlist.size();
                runOnUiThread(new Runnable(){
                    @Override
                    public void run() {
                        ((TextView)findViewById(R.id.timeline_reviewcount)).setText(String.valueOf(allcount));
                    }
                });
                for(String n : reviewlist) {
                    mDatabase2 = FirebaseDatabase.getInstance().getReference("Review")
                            .child("ReviewList").child(n);
                    mDatabase2.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Map<String,Object> map = (Map<String,Object>) dataSnapshot.getValue();
                            Map<String,Object> mapUser = (Map<String,Object>) map.get("UserInfo");
                            Map<String,Object> mapFav = (Map<String,Object>) map.get("Good");
                            if(dataSnapshot.child("Good").child(user.getUid()).getValue() != null) isfav = true;
                            else isfav = false;
                            if(dataSnapshot.child(uid).equals(user.getUid())
                                    || (map.get("OpenRange").toString().equals("1")) && Integer.parseInt(map.get("OpenRange").toString()) <= 4)  {
                                myList.add(new Post_Item(dataSnapshot.getKey(), mapUser.get("uid").toString(), map.get("Book").toString(),
                                        map.get("ISBN").toString(), mapUser.get("proimg").toString(), mapUser.get("name").toString(),
                                        Integer.parseInt(map.get("Rate").toString()), map.get("Time").toString(),
                                        isfav, (int)dataSnapshot.child("Good").getChildrenCount()-1,
                                        map.get("Image").toString(), map.get("Text").toString()
                                ));
                                mDatabase2.child("Good").child("Count").setValue((int)dataSnapshot.child("Good").getChildrenCount()-1);
                            }
                            count++;
                            if(allcount == count) {
                                Collections.sort(myList,new TimingP());
                                try {
                                    mThread1.join();
                                } catch (InterruptedException e) {}
                                mAdapter = new Post_Adapter(myList, uid, uname, upro);
                                //mRecyclerView.setAdapter(mAdapter);
                            }
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });
                }
            }
        };


        ValueEventListener postListener3 = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                    try {
                        reviewlist.add(ds.getValue().toString());
                    } catch(Exception e) {}
                }
                mThread.start();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        mDatabase.addListenerForSingleValueEvent(postListener3);



        if(user.getUid().equals(uid)) {
            ((Button)findViewById(R.id.timeline_button)).setClickable(false);
            ((Button)findViewById(R.id.timeline_button)).setBackgroundTintList
                    (ContextCompat.getColorStateList(getApplicationContext(), android.R.color.darker_gray));
            ((Button)findViewById(R.id.timeline_button)).setTextColor(Color.BLACK);
        } else {
            mDatabase4 = FirebaseDatabase.getInstance().getReference("User_Friend").
                    child("Following").child(uid).child(user.getUid());
            ValueEventListener postListener4 = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot.getValue() != null) {
                        ((Button)findViewById(R.id.timeline_button)).setText("팔로우 해제");
                        ((Button)findViewById(R.id.timeline_button)).setBackgroundTintList
                                (ContextCompat.getColorStateList(getApplicationContext(), android.R.color.white));
                        ((Button)findViewById(R.id.timeline_button)).setTextColor(Color.BLACK);
                        following = true;
                    } else following = false;
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {}
            };
            mDatabase4.addListenerForSingleValueEvent(postListener4);
            ((Button)findViewById(R.id.timeline_button)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(following == false) {
                        mDatabase4.setValue(user.getDisplayName());
                        FirebaseDatabase.getInstance().getReference("User_Friend").
                                child("Follower").child(user.getUid()).child(uid).setValue(uname);
                        ((Button)findViewById(R.id.timeline_button)).setText("팔로우 해제");
                        ((Button)findViewById(R.id.timeline_button)).setBackgroundTintList
                                (ContextCompat.getColorStateList(getApplicationContext(), android.R.color.white));
                        ((Button)findViewById(R.id.timeline_button)).setTextColor(Color.BLACK);
                        following = true;
                    } else {
                        mDatabase4.setValue(null);
                        FirebaseDatabase.getInstance().getReference("User_Friend").
                                child("Follower").child(user.getUid()).child(uid).setValue(null);
                        ((Button)findViewById(R.id.timeline_button)).setText("팔로우");
                        ((Button)findViewById(R.id.timeline_button)).setBackgroundTintList
                                (ContextCompat.getColorStateList(getApplicationContext(), R.color.colorAccent));
                        ((Button)findViewById(R.id.timeline_button)).setTextColor(Color.WHITE);
                        following = false;
                    }
                }
            });
        }

        ValueEventListener postListener5 = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ((TextView)findViewById(R.id.timeline_followercount)).setText(String.valueOf(dataSnapshot.getChildrenCount()));
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        };
        FirebaseDatabase.getInstance().getReference("User_Friend").child("Follower").child(uid).addValueEventListener(postListener5);

        ValueEventListener postListener6 = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ((TextView)findViewById(R.id.timeline_followingcount)).setText(String.valueOf(dataSnapshot.getChildrenCount()));
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        };
        FirebaseDatabase.getInstance().getReference("User_Friend").
                child("Following").child(uid).addValueEventListener(postListener6);

        mLayoutManager = new LinearLayoutManager(this);
        //mRecyclerView.setLayoutManager(mLayoutManager);

        ((TextView)findViewById(R.id.timeline_followercount)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Shelf_TimelineActivity.this, Friend_ListActivity.class);
                intent.putExtra("fol", "follower");
                intent.putExtra("uid", uid);
                startActivity(intent);
            }
        });

        ((TextView)findViewById(R.id.timeline_followingcount)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Shelf_TimelineActivity.this, Friend_ListActivity.class);
                intent.putExtra("fol", "following");
                intent.putExtra("uid", uid);
                startActivity(intent);
            }
        });
 */
