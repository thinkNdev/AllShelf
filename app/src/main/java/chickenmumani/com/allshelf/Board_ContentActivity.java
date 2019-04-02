package chickenmumani.com.allshelf;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

public class Board_ContentActivity extends AppCompatActivity {
    private ProgressBar progressBar;

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private DatabaseReference mDatabase, alramDatabase;
    private ArrayList<Comment_Item> myList;
    private String iscategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board_content);
        setTitle("");
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        progressBar = (ProgressBar)findViewById(R.id.boardProgressBar);
        progressBar.setVisibility(View.VISIBLE);

        TextView title = (TextView) findViewById(R.id.board_title_textview);
        TextView date = (TextView) findViewById(R.id.board_date_textview);
        TextView user = (TextView) findViewById(R.id.board_user_textview);
        TextView content = (TextView) findViewById(R.id.board_content_textview);
        TextView tagText = (TextView) findViewById(R.id.board_output_tag_text);
        TextView bookname = (TextView) findViewById(R.id.board_bookname_textview);
        ImageView tagImage = (ImageView) findViewById(R.id.board_output_tag_image);

        final Intent intent = new Intent(this.getIntent());
        String writer_date = intent.getStringExtra("date");
        final String board_id = intent.getStringExtra("id");
        final String writer_uuid = intent.getStringExtra("uuid");

        title.setText(intent.getStringExtra("title"));
        date.setText(intent.getStringExtra("date"));
        user.setText(intent.getStringExtra("name"));
        content.setText(intent.getStringExtra("content"));
        bookname.setText(intent.getStringExtra("bookname"));
        tagText.setText(intent.getStringExtra("tag"));
        if(intent.getStringExtra("tag").equalsIgnoreCase("교환")){
            tagImage.setImageResource(R.drawable.ic_board_change);
            iscategory = "Trade";
        }
        else{
            tagImage.setImageResource(R.drawable.ic_board_toron);
            iscategory = "Toron";
        }

        progressBar.setVisibility(View.INVISIBLE);

        mRecyclerView = findViewById(R.id.comment_recyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        myList = new ArrayList<Comment_Item>();
        mDatabase = FirebaseDatabase.getInstance().getReference("Board_Item").child(iscategory).child(intent.getStringExtra("id")).child("comments");
        alramDatabase = FirebaseDatabase.getInstance().getReference("Alarm_Item").child(writer_uuid);

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                myList.clear();
                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                    Map<String,Object> map = (Map<String,Object>) ds.getValue();
                    try{
                        myList.add(new Comment_Item(map.get("date").toString(), map.get("name").toString(), map.get("uuid").toString(), map.get("text").toString()));
                    }
                    catch(Exception e){}
                }
                Collections.sort(myList, new TimingN2C());
                mAdapter = new Comment_Adapter(myList, Board_ContentActivity.this);
                mRecyclerView.setAdapter(mAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });

        Button commentRegister = findViewById(R.id.comment_register);

        commentRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.KOREA);
                Date date = new Date();
                String getTime = sdf.format(date);
                FirebaseAuth mAuth = FirebaseAuth.getInstance();;
                FirebaseUser user = mAuth.getCurrentUser();

                Context context = view.getContext();
                final Intent intent = new Intent(context, Comment_Adapter.class);
                intent.putExtra("location", getTime + " " + user.getUid().toString());
                DatabaseReference tdata = mDatabase.child(getTime + " " + user.getUid().toString());
                tdata.child("date").setValue(getTime.toString());
                tdata.child("name").setValue(user.getDisplayName());
                tdata.child("text").setValue(((EditText)findViewById(R.id.editText_comment)).getText().toString());
                tdata.child("uuid").setValue(user.getUid().toString());
                if(!user.getUid().equals(writer_uuid)) {
                    DatabaseReference adata = alramDatabase.child(getTime + " " + user.getUid().toString());
                    adata.child("type").setValue(iscategory);
                    adata.child("boardid").setValue(board_id);
                    adata.child("date").setValue(getTime);
                    adata.child("text").setValue(user.getDisplayName().toString()+"님이 내 게시물에 댓글을 등록하셨습니다");
                    ((EditText)findViewById(R.id.editText_comment)).setText("");
                }
                progressBar.setVisibility(View.INVISIBLE);
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
}