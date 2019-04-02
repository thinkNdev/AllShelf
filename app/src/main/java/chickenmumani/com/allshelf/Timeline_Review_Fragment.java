package chickenmumani.com.allshelf;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import java.util.Map;

public class Timeline_Review_Fragment extends Fragment{
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private FirebaseUser user;
    private String uid, uname, uimgurl;
    private Drawable user_Img;
    private ArrayList<Post_Item> myList;
    private ArrayList<String> reviewlist;
    private DatabaseReference mDatabase1 , mDatabase2, mDatabase3;
    private int count, allcount;
    private boolean isfav;

    public Timeline_Review_Fragment(){}

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        user = FirebaseAuth.getInstance().getCurrentUser();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_timeline_review ,container,false);

        uid = getArguments().getString("uid");
        uname = getArguments().getString("uname");

        mRecyclerView = (RecyclerView) view.findViewById(R.id.timeline_fragment_review_recyclerview);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
        mRecyclerView.setHasFixedSize(true);

        myList = new ArrayList<Post_Item>();
        reviewlist = new ArrayList<String>();

        mDatabase3 = FirebaseDatabase.getInstance().getReference("User_Info").child(uid).child("Profile_Image");
        mDatabase3.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                uimgurl = (String)dataSnapshot.getValue();
                getImageFromURL();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        mDatabase1 = FirebaseDatabase.getInstance().getReference("Review").child("User").child(uid);
        mDatabase1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                reviewlist.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    try {
                        reviewlist.add(ds.getValue().toString());
                    } catch (Exception e) {}
                }
                allcount = reviewlist.size();
                ((TextView)getActivity().findViewById(R.id.timeline_reviewcount)).setText(String.valueOf(allcount));
                for(String n : reviewlist) {
                    mDatabase2 = FirebaseDatabase.getInstance().getReference("Review").child("ReviewList").child(n);
                    mDatabase2.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            Map<String,Object> map = (Map<String,Object>) dataSnapshot.getValue();
                            Map<String,Object> mapUser = (Map<String,Object>) map.get("UserInfo");
                            Map<String,Object> mapFav = (Map<String,Object>) map.get("Good");
                            if(dataSnapshot.child("Good").child(user.getUid()).getValue() != null) isfav = true;
                            else isfav = false;
                            myList.add(new Post_Item(dataSnapshot.getKey(), mapUser.get("uid").toString(), map.get("Book").toString(),
                                    map.get("ISBN").toString(), mapUser.get("proimg").toString(), mapUser.get("name").toString(),
                                    Integer.parseInt(map.get("Rate").toString()), map.get("Time").toString(),
                                    isfav, (int)dataSnapshot.child("Good").getChildrenCount()-1,
                                    map.get("Image").toString(), map.get("Text").toString()
                            ));
                            mDatabase2.child("Good").child("Count").setValue((int)dataSnapshot.child("Good").getChildrenCount()-1);
                            count++;
                            if(allcount == count){
                                Collections.sort(myList,new TimingP());
                                mAdapter = new Post_Adapter(myList, uid, uname, user_Img);
                                mRecyclerView.setAdapter(mAdapter);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView mRecyclerView = (RecyclerView) view;

            mRecyclerView.setHasFixedSize(true);

            // use a linear layout manager
            mLayoutManager = new LinearLayoutManager(context);
            mRecyclerView.setLayoutManager(mLayoutManager);

            mAdapter = new Post_Adapter(myList, uid, uname, user_Img);
            mRecyclerView.setAdapter(mAdapter);
        }
        return view;
    }

    public void getImageFromURL(){
        Thread mThread = new Thread() {
            @Override
            public void run() {
                try {
                    URL url = new URL(uimgurl);

                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setDoInput(true);
                    conn.connect();

                    InputStream is = conn.getInputStream();
                    Bitmap bitmap = BitmapFactory.decodeStream(is);
                    user_Img = new BitmapDrawable(bitmap);
                } catch (Exception e) { e.printStackTrace(); }
            }
        };
        mThread.start();
        try {
            mThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    class TimingP implements Comparator<Post_Item> {
        @Override
        public int compare(Post_Item o1, Post_Item o2) {
            return o2.getDate().compareTo(o1.getDate());
        }
    }
}
