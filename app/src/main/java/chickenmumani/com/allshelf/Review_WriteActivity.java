package chickenmumani.com.allshelf;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
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
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import chickenmumani.com.allshelf.R;

public class Review_WriteActivity extends AppCompatActivity {

    private DatabaseReference mDatabase;
    String bookname, isbn, uid, uname, proimg;
    final int RESULT_LOAD_IMG = 5;
    final int CROP_FROM_CAMERA = 6;
    FirebaseStorage storage;
    StorageReference storageRef;
    RatingBar ratingbar;
    Uri selectedImage;
    Bitmap photo;
    int reviewcount, cancomment = 1, openrange = 1;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_write);
        setTitle("리뷰 쓰기");
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        progressBar = (ProgressBar)findViewById(R.id.review_write_progressbar);
        progressBar.setVisibility(View.INVISIBLE);
        Intent intent = getIntent();
        bookname = intent.getStringExtra("bookname");
        isbn = intent.getStringExtra("isbn");
        uid = intent.getStringExtra("uid");
        uname = intent.getStringExtra("uname");
        proimg = intent.getStringExtra("cover");
        mDatabase = FirebaseDatabase.getInstance().getReference();
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        ratingbar =(RatingBar)findViewById(R.id.review_ratingbar);

        Button imgbut = (Button) findViewById(R.id.writere_editbutton);
        imgbut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, RESULT_LOAD_IMG);
            }
        });

        RadioGroup group1=(RadioGroup)findViewById(R.id.writere_radopen);
        group1.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i){
                    case R.id.writere_radopenall:
                        cancomment = 1;
                        break;
                    case R.id.writere_radopenno:
                        cancomment = 0;
                        break;
                }
            }
        });

        RadioGroup group2=(RadioGroup)findViewById(R.id.writere_radcom);
        group2.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i){
                    case R.id.writere_radcomyes:
                        openrange = 1;
                        break;
                    case R.id.writere_radcomno:
                        openrange = 0;
                        break;
                }
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
                if(((EditText)findViewById(R.id.wredit)).getText().toString().length() < 10) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder .setMessage("리뷰가 10자 미만입니다. 조금 더 성의있게 작성해주세요.")
                            .setCancelable(false)
                            .setPositiveButton("확인", null);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                    return true;
                } else if(((TextView)findViewById(R.id.writere_title)).getText().toString().equals("사진을 추가해주세요")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder .setMessage("리뷰에 사용할 사진을 업로드해주세요.")
                            .setCancelable(false)
                            .setPositiveButton("확인", null);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                    return true;
                }

                progressBar.setVisibility(View.VISIBLE);

                StorageReference ImagesRef = storageRef.child("Review_Image/"+isbn +"_"+ uid +".jpg");
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                photo.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] data = baos.toByteArray();
                UploadTask uploadTask = ImagesRef.putBytes(data);

                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        progressBar.setVisibility(View.INVISIBLE);
                        AlertDialog.Builder builder = new AlertDialog.Builder(Review_WriteActivity.this);
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

                        mDatabase.child("Review").child("ReviewList").child("Counter").
                                child("GetCount").runTransaction(new Transaction.Handler() {
                            @Override
                            public Transaction.Result doTransaction(MutableData mutableData) {
                                if (mutableData.getValue() == null) {
                                    return Transaction.success(mutableData);
                                }
                                reviewcount = (int)(long)mutableData.getValue();
                                reviewcount++;
                                mutableData.setValue(reviewcount);
                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.KOREA);
                                Date date = new Date(System.currentTimeMillis());
                                String getTime = sdf.format(date);
                                DatabaseReference tdata = mDatabase.child("Review").child("ReviewList").child(String.valueOf(reviewcount));
                                tdata.child("Blinded").setValue(0);
                                tdata.child("Book").setValue(bookname);
                                tdata.child("CanComment").setValue(cancomment);
                                tdata.child("Good").child("Count").setValue(0);
                                tdata.child("ISBN").setValue(isbn);
                                tdata.child("Image").setValue("Review_Image/"+isbn +"_"+ uid +".jpg");
                                tdata.child("OpenRange").setValue(openrange);
                                tdata.child("Rate").setValue(ratingbar.getNumStars());
                                tdata.child("Text").setValue(((EditText)findViewById(R.id.wredit)).getText().toString());
                                tdata.child("UserInfo").child("name").setValue(uname);
                                tdata.child("UserInfo").child("uid").setValue(uid);
                                tdata.child("UserInfo").child("proimg").setValue(proimg);
                                tdata.child("Time").setValue(getTime);

                                mDatabase.child("Review").child("Book").child(isbn).child(uid).setValue(reviewcount);
                                mDatabase.child("Review").child("User").child(uid).child(isbn).setValue(reviewcount);

                                mDatabase.child("User_Info").child(uid).child("Profile_Image").setValue(proimg);


                                Intent intent = new Intent(Review_WriteActivity.this, Review_OneActivity.class);
                                intent.putExtra("num", String.valueOf(reviewcount));
                                startActivity(intent);
                                Review_WriteActivity.this.finish();
                                return Transaction.success(mutableData);
                            }

                            @Override
                            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
                                Log.d("w", "postTransaction:onComplete:" + databaseError);
                            }
                        });
                    }
                });
                progressBar.setVisibility(View.INVISIBLE);
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
        if(resultCode == Activity.RESULT_OK)
            switch (requestCode){
                case RESULT_LOAD_IMG:
                {
                    selectedImage = data.getData();
                    ((TextView)findViewById(R.id.writere_title)).setText(getFileName(selectedImage));
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
