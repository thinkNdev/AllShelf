package chickenmumani.com.allshelf;

import android.content.Context;
import android.content.Intent;
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


public class Notice_Adapter extends RecyclerView.Adapter<Notice_Adapter.ViewHolder> {

    private List<Notice_Item> myList;

    public Notice_Adapter(List<Notice_Item> list) {
        this.myList = list;
    }

    @Override
    public Notice_Adapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.view_notice, parent, false);
        return new Notice_Adapter.ViewHolder(v);
    }


    @Override
    public void onBindViewHolder(final Notice_Adapter.ViewHolder holder, int position) {
        final Notice_Item my = myList.get(position);
        holder.notit.setText(my.getTitle());
        holder.nodate.setText(my.getDate());

        holder.nolay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                Context context = v.getContext();
                Intent intent = new Intent(context, Notice_ContentActivity.class);
                intent.putExtra("id",my.getId());
                intent.putExtra("title",my.getTitle());
                intent.putExtra("date",my.getDate());
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
        public final TextView notit;
        public final TextView nodate;
        public final LinearLayout nolay;


        public ViewHolder(View view) {
            super(view);
            mView = view;
            notit = (TextView) view.findViewById(R.id.noticeview_title);
            nodate = (TextView) view.findViewById(R.id.noticeview_date);
            nolay = (LinearLayout) view.findViewById(R.id.noticeview_lay);

        }

    }

}