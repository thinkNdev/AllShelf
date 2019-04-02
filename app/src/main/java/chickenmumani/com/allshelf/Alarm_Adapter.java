package chickenmumani.com.allshelf;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class Alarm_Adapter extends RecyclerView.Adapter<Alarm_Adapter.ViewHolder> {
    private List<Alarm_Item> alarmList;
    private DatabaseReference mDatabase;
    private FragmentActivity classactivity;
    Thread userImageThread;
    String userImageUrls;
    public Alarm_Adapter(FragmentActivity classactivity, List<Alarm_Item> list) {
        this.alarmList = list;
        this.classactivity = classactivity;
    }

    @Override
    public Alarm_Adapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_alarm, parent, false);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);
        return new Alarm_Adapter.ViewHolder(v);
    }


    @Override
    public void onBindViewHolder(final Alarm_Adapter.ViewHolder holder, int position) {
        final Alarm_Item my = alarmList.get(position);
        holder.alarmDate.setText(my.getDate());
        holder.alarmText.setText(my.getText());

        mDatabase = FirebaseDatabase.getInstance().getReference("User_Info").child(my.getUid()).child("Profile_Image");
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userImageUrls = (String)dataSnapshot.getValue();
                try {
                    URL url = new URL(userImageUrls);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setDoInput(true);
                    conn.connect();

                    InputStream is = conn.getInputStream();
                    final Bitmap apro = BitmapFactory.decodeStream(is);
                    classactivity.runOnUiThread(new Runnable(){
                        @Override
                        public void run() {
                            holder.alarmImg.setImageBitmap(apro);
                            holder.alarmImg.setBackground(new ShapeDrawable(new OvalShape()));
                            holder.alarmImg.setClipToOutline(true);
                        }
                    });
                } catch (Exception e) { e.printStackTrace(); }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });

        holder.alarmLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(my.getType().equals("Trade") || my.getType().equals("Toron")) {
                    final Intent intentt = new Intent(holder.alarmView.getContext(),  Board_ContentActivity.class);
                    FirebaseDatabase.getInstance().getReference("Board_Item").
                            child(my.getType()).child(my.getBoardid()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            intentt.putExtra("id", dataSnapshot.getKey());
                            intentt.putExtra("uuid", dataSnapshot.child("uid").getValue().toString());
                            intentt.putExtra("name", dataSnapshot.child("name").getValue().toString());
                            intentt.putExtra("title", dataSnapshot.child("title").getValue().toString());
                            intentt.putExtra("date", dataSnapshot.child("date").getValue().toString());
                            intentt.putExtra("content", dataSnapshot.child("content").getValue().toString());
                            intentt.putExtra("bookname", dataSnapshot.child("bookname").getValue().toString());
                            intentt.putExtra("isbn", dataSnapshot.child("isbn").getValue().toString());
                            intentt.putExtra("imgurl", dataSnapshot.child("imgurl").getValue().toString());
                            if(my.getType().equals("Trade")) intentt.putExtra("tag", "교환");
                            else intentt.putExtra("tag", "토론");
                            ((Activity)(holder.alarmView.getContext())).startActivity(intentt);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {}
                    });
                } else if(my.getType().equals("Review")) {
                    final Intent intentt = new Intent(holder.alarmView.getContext(),  Review_OneActivity.class);
                    intentt.putExtra("num", my.getBoardid());
                    ((Activity)(holder.alarmView.getContext())).startActivity(intentt);
                }

            }
        });

    }

    @Override
    public int getItemCount() {
        return alarmList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public final View alarmView;
        public final TextView alarmText;
        public final TextView alarmDate;
        public final ImageView alarmImg;
        public final LinearLayout alarmLayout;

        public ViewHolder(View view) {
            super(view);
            alarmView = view;
            alarmText = view.findViewById(R.id.alarmview_text);
            alarmDate = view.findViewById(R.id.alarmview_date);
            alarmImg = view.findViewById(R.id.alarmview_img);
            alarmLayout = view.findViewById(R.id.alarmview_lay);
        }
    }
}