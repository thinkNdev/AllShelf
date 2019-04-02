package chickenmumani.com.allshelf;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BlurMaskFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

public class Shelf_ImageFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    private SharedPreferences appData;
    private Animation fab_open, fab_close;
    private Boolean isFabOpen = false;
    private FloatingActionButton fab, fab1, fab2;
    private TextView ftxt1, ftxt2;
    private EditText searchText;
    private ImageButton searchButton;
    private String sorte;
    private Button sortb;
    private String it;

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_shelf_image,container,false);

        sortb =  (Button) view.findViewById(R.id.desk_sortbuttoni);
        appData = getActivity().getSharedPreferences("appData", MODE_PRIVATE);
        load();

        mRecyclerView = (RecyclerView) view.findViewById(R.id.book_shelfimagerecyclerview);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
        mRecyclerView.setHasFixedSize(true);

        searchText = (EditText)view.findViewById(R.id.desk_searchedit);
        searchText.setHint("찾으시는 책 제목을 입력해주세요.");
        searchButton = (ImageButton)view.findViewById(R.id.desk_searchbutton);

        searchText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchText.setText("");
                searchText.setTextColor(Color.BLACK);
            }
        });
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(searchText == null || searchText.getTextSize() < 1 || searchText.getText().toString().equalsIgnoreCase("")){
                    searchText.setText("책 제목이 입력되지 않았습니다.");
                    searchText.setTextColor(Color.GRAY);
                    return;
                }
                else{
                    String bookname = searchText.getText().toString();
                    if(bookname.equalsIgnoreCase("책 제목이 입력되지 않았습니다.")){
                        return;
                    }
                    else{
                        Intent intent = new Intent(getActivity(), MySearch_Activity.class);
                        intent.putExtra("search", bookname);
                        startActivity(intent);
                        searchText.setText("");
                        return;
                    }
                }
            }
        });

        if(it == "i") mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        else mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 1));

        final ArrayList<Shelf_Item> myList = new ArrayList<Shelf_Item>();

        mAuth = FirebaseAuth.getInstance();
        final FirebaseUser user = mAuth.getCurrentUser();

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference();
        mDatabase = FirebaseDatabase.getInstance().getReference("User_Book").child(user.getUid());

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //circle_bar.setVisibility(View.GONE);
                //http://www.aladin.co.kr/ttb/api/ItemLookUp.aspx?ttbkey=ttbybson20100846003&itemIdType=ISBN13&ItemId=9788968483318&output=js&Version=20131101&OptResult=ebookList,usedList,reviewList
                myList.clear();
                int check = 1;
                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                    try {
                        Map<String,Object> map = (Map<String,Object>) ds.getValue();
                        myList.add(new Shelf_Item(map.get("isbn").toString(), map.get("title").toString(), map.get("author").toString()
                                ,map.get("imgurl").toString(), map.get("time").toString() )) ;
                    } catch(Exception e) {
                        check = 0;
                    }
                }

                if(check == 1) {
                    // specify an adapter (see also next example)
                    if(sortb.getText() == "최신등록순") {
                        Collections.sort(myList,new TimingS());
                        mAdapter = new Shelf_ImageAdapter(myList, it);
                        mRecyclerView.setAdapter(mAdapter);
                    } else if(sortb.getText() == "도서명순") {
                        Collections.sort(myList,new TitlingS());
                        mAdapter = new Shelf_ImageAdapter(myList, it);
                        mRecyclerView.setAdapter(mAdapter);
                    } else if(sortb.getText() == "저자명순") {
                        Collections.sort(myList,new AuthoringS());
                        mAdapter = new Shelf_ImageAdapter(myList, it);
                        mRecyclerView.setAdapter(mAdapter);
                    }

                    mAdapter = new Shelf_ImageAdapter(myList, it);
                    mRecyclerView.setAdapter(mAdapter);
                }

                //circle_bar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView mRecyclerView = (RecyclerView) view;

            mRecyclerView.setHasFixedSize(true);

            // use a linear layout manager
            mLayoutManager = new LinearLayoutManager(context);
            mRecyclerView.setLayoutManager(mLayoutManager);
        }

        fab_open = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.fab_close);

        fab = (FloatingActionButton) getActivity().findViewById(R.id.shelf_addbookfab);
        fab1 = (FloatingActionButton) getActivity().findViewById(R.id.shelf_addbookfabbarcode);
        fab2 = (FloatingActionButton) getActivity().findViewById(R.id.shelf_addbookfabsearch);

        ftxt1 = (TextView) getActivity().findViewById(R.id.shelf_lintxt1);
        ftxt2 = (TextView) getActivity().findViewById(R.id.shelf_lintxt2);

        final View shfab = (View) getActivity().findViewById(R.id.shelfimage_fabimgview);
        FrameLayout shfra = (FrameLayout) getActivity().findViewById(R.id.shelf_framelay);
        shfab.bringToFront();
        shfra.bringToFront();

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(shfab.getVisibility() == View.VISIBLE)
                    shfab.setVisibility(View.INVISIBLE);
                else shfab.setVisibility(View.VISIBLE);
                anim();
            }
        });
        fab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shfab.setVisibility(View.INVISIBLE);
                getCameraPermission();
                anim();
            }
        });
        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shfab.setVisibility(View.INVISIBLE);
                Intent intent = new Intent(getActivity(), Search_Activity.class);
                startActivity(intent);
                anim();
            }
        });
        shfab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shfab.setVisibility(View.INVISIBLE);
                anim();
            }
        });

        sortb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(sortb.getText() == "최신등록순") {
                    Collections.sort(myList,new TitlingS());
                    mAdapter = new Shelf_ImageAdapter(myList, it);
                    mRecyclerView.setAdapter(mAdapter);
                    sortb.setText("도서명순");
                } else if(sortb.getText() == "도서명순") {
                    Collections.sort(myList,new AuthoringS());
                    mAdapter = new Shelf_ImageAdapter(myList, it);
                    mRecyclerView.setAdapter(mAdapter);
                    sortb.setText("저자명순");
                } else if(sortb.getText() == "저자명순") {
                    Collections.sort(myList,new TimingS());
                    mAdapter = new Shelf_ImageAdapter(myList, it);
                    mRecyclerView.setAdapter(mAdapter);
                    sortb.setText("최신등록순");
                }

            }
        });

        ImageButton menub = (ImageButton) view.findViewById(R.id.desk_menubutton);
        menub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(it.equals("i")) {
                    mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 1));
                    it = "t";
                    mAdapter = new Shelf_ImageAdapter(myList, it);
                    mRecyclerView.setAdapter(mAdapter);
                } else {
                    mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
                    it = "i";
                    mAdapter = new Shelf_ImageAdapter(myList, it);
                    mRecyclerView.setAdapter(mAdapter);
                }
            }
        });

        ImageButton timel = (ImageButton) view.findViewById(R.id.desk_timelinebutton);
        timel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), Shelf_TimelineActivity.class);
                intent.putExtra("uid", user.getUid());
                intent.putExtra("uname", user.getDisplayName());
                startActivity(intent);
            }
        });

        return view;
    }

    public void onResume(){
        super.onResume();
        ((Navi_Activity) getActivity())
                .setActionBarTitle("서재");

    }

    private void getCameraPermission() {
        int permissionCheck = ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.CAMERA);
        if (permissionCheck == PackageManager.PERMISSION_DENIED) {
            //권한없음
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA},0);
        } else {
            //권한있음
            Intent intent = new Intent(getActivity(), Shelf_BarcodeScanActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public void anim() {

        if (isFabOpen) {
            fab1.startAnimation(fab_close);
            fab2.startAnimation(fab_close);
            ftxt1.startAnimation(fab_close);
            ftxt2.startAnimation(fab_close);
            fab1.setClickable(false);
            fab2.setClickable(false);
            isFabOpen = false;
        } else {
            fab1.startAnimation(fab_open);
            fab2.startAnimation(fab_open);
            ftxt1.startAnimation(fab_open);
            ftxt2.startAnimation(fab_open);
            fab1.setClickable(true);
            fab2.setClickable(true);
            isFabOpen = true;
        }
    }

    private void save() {
        SharedPreferences.Editor editor = appData.edit();
        editor.putString("sorted", sortb.getText().toString().trim());
        editor.putString("it",it);
        editor.apply();
    }

    private void load() {
        sorte = appData.getString("sorted", "최신등록순");
        sortb.setText(sorte);
        it = appData.getString("it","i");
    }

    class TimingS implements Comparator<Shelf_Item> {
        @Override
        public int compare(Shelf_Item o1, Shelf_Item o2) {
            return o2.getTime().compareTo(o1.getTime());
        }

    }

    class TitlingS implements Comparator<Shelf_Item> {
        @Override
        public int compare(Shelf_Item o1, Shelf_Item o2) {
            return o1.getTitle().compareTo(o2.getTitle());
        }

    }

    class AuthoringS implements Comparator<Shelf_Item> {
        @Override
        public int compare(Shelf_Item o1, Shelf_Item o2) {
            return o1.getAuthor().compareTo(o2.getAuthor());
        }

    }
}