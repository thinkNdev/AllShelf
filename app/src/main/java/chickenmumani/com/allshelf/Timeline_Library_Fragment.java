package chickenmumani.com.allshelf;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;

public class Timeline_Library_Fragment extends android.support.v4.app.Fragment{
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private DatabaseReference mDatabase;
    private ArrayList<Shelf_Item> myList;
    private String uid;

    public Timeline_Library_Fragment(){ }

    @Override
    public void onCreate(Bundle savedInstanceState){ super.onCreate(savedInstanceState); }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_timeline_library ,container,false);

        uid = getArguments().getString("uid");

        mRecyclerView = (RecyclerView) view.findViewById(R.id.timeline_fragment_library_recyclerview);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
        mRecyclerView.setHasFixedSize(true);

        myList = new ArrayList<Shelf_Item>();

        mDatabase = FirebaseDatabase.getInstance().getReference("User_Book").child(uid);
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                myList.clear();
                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                    try {
                        Map<String,Object> map = (Map<String,Object>) ds.getValue();
                        myList.add(new Shelf_Item(map.get("isbn").toString(), map.get("title").toString(), map.get("author").toString(),map.get("imgurl").toString(), map.get("time").toString() )) ;
                    } catch(Exception e) {
                        e.printStackTrace();
                    }
                }
                Collections.sort(myList, new TimingS());
                mAdapter = new Shelf_ImageAdapter(myList, "t");
                mRecyclerView.setAdapter(mAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView mRecyclerView = (RecyclerView) view;

            mRecyclerView.setHasFixedSize(true);

            // use a linear layout manager
            mLayoutManager = new LinearLayoutManager(context);
            mRecyclerView.setLayoutManager(mLayoutManager);

            mAdapter = new Shelf_ImageAdapter(myList, "t");
            mRecyclerView.setAdapter(mAdapter);
        }

        return view;
    }

    class TimingS implements Comparator<Shelf_Item> {
        @Override
        public int compare(Shelf_Item o1, Shelf_Item o2) {
            return o2.getTime().compareTo(o1.getTime());
        }
    }
}
