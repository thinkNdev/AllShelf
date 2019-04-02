package chickenmumani.com.allshelf;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.OpenableColumns;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Review_EditActivity extends AppCompatActivity {

    private DatabaseReference mDatabase;
    String isbn, uid, uname, proimg, num;
    final int RESULT_LOAD_IMG = 5;
    final int CROP_FROM_CAMERA = 6;
    FirebaseStorage storage;
    StorageReference storageRef;
    RatingBar ratingbar;
    Uri selectedImage;
    FirebaseUser user;
    Bitmap photo;
    int reviewcount, cancomment, openrange;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_edit);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("리뷰 수정");
        num = getIntent().getStringExtra("num");
        user = FirebaseAuth.getInstance().getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference("Review")
                .child("ReviewList").child(num);
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                isbn = dataSnapshot.child("ISBN").getValue().toString();
                ((EditText)findViewById(R.id.ededit)).setText(dataSnapshot.child("Text").getValue().toString());
                ((RatingBar)findViewById(R.id.ed_ratingbar)).setNumStars(Integer.parseInt(dataSnapshot.child("Rate").getValue().toString()));
                if(dataSnapshot.child("OpenRange").getValue().toString().equals("1")) {
                    ((RadioGroup)findViewById(R.id.ed_radopen)).check(R.id.ed_radopenall);
                    openrange = 1;
                } else {
                    ((RadioGroup)findViewById(R.id.ed_radopen)).check(R.id.ed_radopenno);
                    openrange = 0;
                }

                if(dataSnapshot.child("CanComment").getValue() == null ||
                        dataSnapshot.child("CanComment").getValue().toString().equals("1")) {
                    ((RadioGroup)findViewById(R.id.ed_radcom)).check(R.id.ed_radcomyes);
                    cancomment = 1;
                } else {
                    ((RadioGroup)findViewById(R.id.ed_radcom)).check(R.id.ed_radcomno);
                    cancomment = 0;
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });

        Button imgbut = (Button) findViewById(R.id.editere_editbutton);
        imgbut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, RESULT_LOAD_IMG);
            }
        });

        RadioGroup group1=(RadioGroup)findViewById(R.id.ed_radopen);
        group1.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i){
                    case R.id.ed_radopenall:
                        cancomment = 1;
                        break;
                    case R.id.ed_radopenno:
                        cancomment = 0;
                        break;
                }
            }
        });

        RadioGroup group2=(RadioGroup)findViewById(R.id.ed_radcom);
        group2.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i){
                    case R.id.ed_radcomyes:
                        openrange = 1;
                        break;
                    case R.id.ed_radcomno:
                        openrange = 0;
                        break;
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.writere, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            case R.id.action_upload: {
                if(((EditText)findViewById(R.id.ededit)).getText().toString().length() < 10) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage("리뷰가 10자 미만입니다. 조금 더 성의있게 작성해주세요.")
                            .setCancelable(false)
                            .setPositiveButton("확인", null);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                    return true;
                }
                mDatabase.child("Text").setValue(((EditText)findViewById(R.id.ededit)).getText().toString());
                mDatabase.child("Comment").child("CanComment").setValue(cancomment);
                mDatabase.child("OpenRange").setValue(openrange);
                mDatabase.child("Rate").setValue(((RatingBar)findViewById(R.id.ed_ratingbar)).getNumStars());

                if(photo != null) {
                    final ProgressDialog dialogr = ProgressDialog.show(Review_EditActivity.this, "",
                            "Loading... Please wait");

                    StorageReference ImagesRef = storageRef.child("Review_Image/"+isbn +"_"+ user.getUid() +".jpg");
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    photo.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    byte[] data = baos.toByteArray();
                    UploadTask uploadTask = ImagesRef.putBytes(data);
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            dialogr.dismiss();
                            AlertDialog.Builder builder = new AlertDialog.Builder(Review_EditActivity.this);
                            builder .setMessage("이미지 업로드에 실패했습니다. 네트워크 연결을 확인해주세요.")
                                    .setCancelable(false)
                                    .setPositiveButton("확인", null);
                            AlertDialog dialog = builder.create();
                            dialog.show();
                            // Handle unsuccessful uploads
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            dialogr.dismiss();
                            Intent intent = new Intent(Review_EditActivity.this, Review_OneActivity.class);
                            intent.putExtra("num", num);
                            startActivity(intent);
                            Review_EditActivity.this.finish();
                        }
                    });
                } else {
                    Intent intent = new Intent(Review_EditActivity.this, Review_OneActivity.class);
                    intent.putExtra("num", num);
                    startActivity(intent);
                    Review_EditActivity.this.finish();
                }
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == Activity.RESULT_OK)
            switch (requestCode){
                case RESULT_LOAD_IMG:
                {
                    selectedImage = data.getData();
                    ((TextView)findViewById(R.id.editere_title)).setText(getFileName(selectedImage));
                    Intent intent = new Intent("com.android.camera.action.CROP");
                    intent.setDataAndType(selectedImage, "image/*");

                    intent.putExtra("outputX", 500);
                    intent.putExtra("outputY", 500);
                    intent.putExtra("aspectX", 1);
                    intent.putExtra("aspectY", 1);
                    intent.putExtra("scale", true);
                    intent.putExtra("return-data", true);
                    startActivityForResult(intent, CROP_FROM_CAMERA);

                    break;
                }
                case CROP_FROM_CAMERA:
                {
                    try {
                        final Bundle extras = data.getExtras();
                        photo = extras.getParcelable("data");
                        File f = new File(selectedImage.getPath());
                        if(f.exists())
                        {
                            f.delete();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                }

            }
    }

    public String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }
}
