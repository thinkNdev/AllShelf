package chickenmumani.com.allshelf;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import chickenmumani.com.allshelf.R;

public class Report_Activity extends AppCompatActivity {

    DatabaseReference mDatabase;
    String num, post_type, uid;
    int why = 1;
    int blinded, dup = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
        mDatabase = FirebaseDatabase.getInstance().getReference("Report");
        Intent intent = getIntent();
        num = intent.getStringExtra("num");
        post_type = intent.getStringExtra("post_type");
        uid = intent.getStringExtra("uid");
        ((RadioGroup)findViewById(R.id.rep_group)).setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i){
                    case R.id.rep1:
                        why = 1;
                        break;
                    case R.id.rep2:
                        why = 2;
                        break;
                    case R.id.rep3:
                        why = 3;
                        break;
                    case R.id.rep4:
                        why = 4;
                        break;
                    case R.id.rep5:
                        why = 5;
                        break;

                }
            }
        });

        FirebaseDatabase.getInstance().getReference("Review").child("ReviewList")
                .child(num).child("Blinded").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                blinded = Integer.parseInt(dataSnapshot.getValue().toString());
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });
        FirebaseDatabase.getInstance().getReference("Report").child(uid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue() != null) dup = 1;
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });

        ((Button)findViewById(R.id.report_com)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference pushref = mDatabase.child(uid);
                pushref.child("num").setValue(num);
                pushref.child("post_type").setValue(post_type);
                pushref.child("text").setValue(((EditText)findViewById(R.id.rep_edit)).getText().toString());
                if(dup == 0) {
                    FirebaseDatabase.getInstance().getReference("Review").child("ReviewList")
                            .child(num).child("Blinded").setValue(blinded + 1);
                }

                Report_Activity.this.finish();

            }
        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //바깥레이어 클릭시 안닫히게
        if(event.getAction()==MotionEvent.ACTION_OUTSIDE){
            return false;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("result", "No");
        setResult(RESULT_OK, intent);

        //액티비티(팝업) 닫기
        finish();
    }

    public void reportcan(View view) {
        this.finish();
    }
}
