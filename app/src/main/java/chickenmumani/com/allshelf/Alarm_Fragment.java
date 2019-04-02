package chickenmumani.com.allshelf;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import chickenmumani.com.allshelf.R;

public class Alarm_Fragment extends Fragment {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ArrayList<Alarm_Item> alramList;
    private DatabaseReference alramDatabase;

    public Alarm_Fragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_alarm, container, false);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.alarm_recyclerview);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
        mRecyclerView.setHasFixedSize(true);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();;
        FirebaseUser user = mAuth.getCurrentUser();
        alramList = new ArrayList<Alarm_Item>();
        alramDatabase = FirebaseDatabase.getInstance().getReference("Alarm_Item").child(user.getUid());
        alramDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                alramList.clear();
                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                    try{
                        String[] txtArr = ds.getKey().split(" ") ;
                        alramList.add(new Alarm_Item(ds.child("type").getValue().toString(), ds.child("boardid").getValue().toString(),
                                txtArr[2], ds.child("date").getValue().toString(), ds.child("text").getValue().toString()));
                    }
                    catch(NullPointerException e){
                        e.printStackTrace();
                    }
                }

                mAdapter = new Alarm_Adapter(getActivity(), alramList);
                mRecyclerView.setAdapter(mAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        //R.drawable.ic_close_black_24dp R.drawable.ic_info_black_24dp


        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView mRecyclerView = (RecyclerView) view;

            mRecyclerView.setHasFixedSize(true);

            // use a linear layout manager
            mLayoutManager = new LinearLayoutManager(context);
            mRecyclerView.setLayoutManager(mLayoutManager);

            // specify an adapter (see also next example)
            mAdapter = new Alarm_Adapter(getActivity(), alramList);
            mRecyclerView.setAdapter(mAdapter);
        }

        return view;
    }

    public void onResume(){
        super.onResume();
        ((Navi_Activity) getActivity())
                .setActionBarTitle("알림");
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

}
