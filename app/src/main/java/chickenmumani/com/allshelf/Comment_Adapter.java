package chickenmumani.com.allshelf;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;


public class Comment_Adapter extends RecyclerView.Adapter<Comment_Adapter.ViewHolder> {

    FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();;
    FirebaseUser user = mAuth.getCurrentUser();

    private final Context context;
    private List<Comment_Item> myList;
    public Comment_Adapter(List<Comment_Item> list, Context context)
    {
        this.myList = list;
        this.context = context;
    }

    @Override
    public Comment_Adapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_comment, parent, false);
        return new Comment_Adapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final Comment_Adapter.ViewHolder holder, int position) {
        final Comment_Item my = myList.get(position);
        holder.commentText.setText(my.getText());
        holder.commentDate.setText(my.getDate());
        holder.commentWriter.setText(my.getName());
        holder.commentLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (user.getUid().equals(my.getUuid())) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(context);     // 여기서 this는 Activity의 this
                    builder.setMessage("댓글을 삭제하시겠습니까?")
                            .setCancelable(false)
                            .setPositiveButton("예", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    mDatabase.getReference("Board_Item").child(my.getDate() + " " + my.getUuid()).child("comments").child(my.getDate() + " " + my.getUuid()).removeValue();
                                    Log.d("check", my.getDate() + " " + my.getUuid());
                                }
                            })

                            .setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    dialog.cancel();
                                }
                            });
                    AlertDialog dialog = builder.create();    // 알림창 객체 생성
                    dialog.show();    // 알림창 띄우기

                    return true;
                }
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return myList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public final View mView;
        public final TextView commentText;
        public final TextView commentDate;
        public final TextView commentWriter;
        public final LinearLayout commentLayout;


        public ViewHolder(View view) {
            super(view);
            mView = view;
            commentDate = view.findViewById(R.id.comment_date);
            commentText = view.findViewById(R.id.comment_text);
            commentWriter = view.findViewById(R.id.comment_writer);
            commentLayout = view.findViewById(R.id.comment_layout);
        }
    }
}