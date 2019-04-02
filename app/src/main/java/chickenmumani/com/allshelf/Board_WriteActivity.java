package chickenmumani.com.allshelf;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Board_WriteActivity extends AppCompatActivity {
    private DatabaseReference mDatabase;
    private FirebaseStorage storage;
    private StorageReference storageRef;
    private ProgressBar progressBar;
    private TextView content_textview;
    private TextView upload_book_textview;
    private ImageView upload_book_imagebutton;
    private Intent infoIntent;

    int openrange = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board_write);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Intent intent = getIntent();
        setTitle("게시물 작성");

        content_textview = (TextView)findViewById(R.id.board_edit);
        upload_book_textview = (TextView)findViewById(R.id.board_write_bookname_textview);
        upload_book_imagebutton = (ImageButton)findViewById(R.id.board_write_bookname_imagebutton);

        mDatabase = FirebaseDatabase.getInstance().getReference("Board_Item");
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        progressBar = (ProgressBar)findViewById(R.id.boardWriteProgressBar);
        progressBar.setVisibility(View.INVISIBLE);

        RadioGroup group=(RadioGroup)findViewById(R.id.board_class);
        group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i){
                    case R.id.board_classyes:
                        openrange = 1;
                        content_textview.setHint(R.string.board_trade_hint);
                        break;
                    case R.id.board_classno:
                        openrange = 0;
                        content_textview.setHint(R.string.board_toron_hint);
                        break;
                }
            }
        });

        upload_book_imagebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(Board_WriteActivity.this, Board_Search_Activity.class);
                startActivityForResult(intent1, 1102);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            case R.id.action_upload:
            {
                if(((TextView)findViewById(R.id.board_write_bookname_textview)).getText().toString() == null || ((TextView)findViewById(R.id.board_write_bookname_textview)).getText().toString().length() < 1 || ((TextView)findViewById(R.id.board_write_bookname_textview)).getText().toString().equalsIgnoreCase("")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder .setMessage("책이 등록되지 않았습니다. 등록해주세요.")
                            .setCancelable(false)
                            .setPositiveButton("확인", null);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                    return true;
                }
                else if(((EditText)findViewById(R.id.board_titleedit)).getText().toString().length() >= 20) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder .setMessage("제목이 너무 깁니다. 조금만 줄여주세요.")
                            .setCancelable(false)
                            .setPositiveButton("확인", null);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                    return true;
                }
                else if(((EditText)findViewById(R.id.board_edit)).getText().toString().length() < 10) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder .setMessage("내용이 10자 미만으로 확인되어 게시물 등록에 실패하였습니다.")
                            .setCancelable(false)
                            .setPositiveButton("확인", null);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                    return true;
                }
                else{
                    progressBar.setVisibility(View.VISIBLE);

                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.KOREA);
                    Date date = new Date();
                    String getTime = sdf.format(date);
                    FirebaseAuth mAuth = FirebaseAuth.getInstance();;
                    FirebaseUser user = mAuth.getCurrentUser();

                    DatabaseReference tdata;
                    if(openrange == 0){
                        tdata = mDatabase.child("Toron").child(date.getTime() + " " + user.getUid().toString());
                    }
                    else{
                        tdata = mDatabase.child("Trade").child(date.getTime() + " " + user.getUid().toString());
                    }

                    tdata.child("date").setValue(getTime);
                    tdata.child("name").setValue(user.getDisplayName());
                    tdata.child("title").setValue(((EditText)findViewById(R.id.board_titleedit)).getText().toString());
                    tdata.child("content").setValue(((EditText)findViewById(R.id.board_edit)).getText().toString());
                    tdata.child("uid").setValue(user.getUid().toString());
                    tdata.child("bookname").setValue(infoIntent.getStringExtra("title"));
                    tdata.child("isbn").setValue(infoIntent.getStringExtra("isbn"));
                    tdata.child("imgurl").setValue(infoIntent.getStringExtra("imgurl"));

                    progressBar.setVisibility(View.INVISIBLE);
                    Board_WriteActivity.this.finish();
                }
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.writere, menu);
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        infoIntent = data;
        upload_book_textview.setText(infoIntent.getStringExtra("title"));
    }
}
