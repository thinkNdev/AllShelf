package chickenmumani.com.allshelf;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;

import chickenmumani.com.allshelf.R;

public class Notice_ContentActivity extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private StorageReference mStorageRef;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice_content);
        setTitle("공지사항");
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        progressBar = (ProgressBar)findViewById(R.id.noticeProgressBar);
        progressBar.setVisibility(View.VISIBLE);

        TextView title = (TextView) findViewById(R.id.noticeview_title);
        TextView date = (TextView) findViewById(R.id.noticeview_date);
        final TextView content = (TextView) findViewById(R.id.notcontent_content);

        Intent intent = new Intent(this.getIntent());

        title.setText(intent.getStringExtra("title"));
        date.setText(intent.getStringExtra("date"));
        mStorageRef = FirebaseStorage.getInstance().getReference();

        String storagePath = "Notice_Content/" + intent.getStringExtra("id") +".txt";
        StorageReference islandRef = mStorageRef.child(storagePath);

        final long ONE_MEGABYTE = 1024 * 1024;
        islandRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                String str2 = new String(bytes);
                content.setText(str2);
                progressBar.setVisibility(View.INVISIBLE);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.e("m","Storage Failed");
                progressBar.setVisibility(View.INVISIBLE);
                // Handle any errors
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
