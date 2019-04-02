package chickenmumani.com.allshelf;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import chickenmumani.com.allshelf.R;


public class Message_Adapter extends RecyclerView.Adapter<Message_Adapter.ViewHolder> {

    private List<Message_Item> myList;

    public Message_Adapter(List<Message_Item> list) {
        this.myList = list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.view_alarm, parent, false);
        return new ViewHolder(v);
    }


    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final Message_Item my = myList.get(position);
        holder.alimg.setImageDrawable(my.getIconDrawable());
        holder.almsg.setText(my.getMessage_msg());
        holder.aldate.setText(my.getMessage_date());

        holder.allay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                Context context = v.getContext();
                Intent intent = new Intent(context, Notice_ContentActivity.class);
                intent.putExtra("id",my.getId());
                intent.putExtra("title",my.getMessage_msg());
                intent.putExtra("date",my.getMessage_date());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return myList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public final View mView;
        public final ImageView alimg;
        public final TextView almsg;
        public final TextView aldate;
        public final LinearLayout allay;


        public ViewHolder(View view) {
            super(view);
            mView = view;
            alimg = (ImageView) view.findViewById(R.id.alarmview_img);
            almsg = (TextView) view.findViewById(R.id.alarmview_text);
            aldate = (TextView) view.findViewById(R.id.alarmview_date);
            allay = (LinearLayout) view.findViewById(R.id.alarmview_lay);

        }

    }

}
