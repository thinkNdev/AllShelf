package chickenmumani.com.allshelf;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;


public class Friend_ListAdapter extends RecyclerView.Adapter<Friend_ListAdapter.ViewHolder> {

    private List<Friend_ListItem> myList;
    public View mView;
    Bitmap bitmap;

    public Friend_ListAdapter(List<Friend_ListItem> list) {
        this.myList = list;
    }

    @Override

    public Friend_ListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.view_friendlist, parent, false);
        return new Friend_ListAdapter.ViewHolder(v);
    }


    @Override
    public void onBindViewHolder(final Friend_ListAdapter.ViewHolder holder, int position) {
        final Friend_ListItem my = myList.get(position);
        holder.fname.setText(my.getName());

        Thread mThread = new Thread() {
            @Override
            public void run() {
                try {
                    URL url = new URL(my.getImg());

                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setDoInput(true);
                    conn.connect();

                    InputStream is = conn.getInputStream();
                    bitmap = BitmapFactory.decodeStream(is);
                } catch (Exception e) { e.printStackTrace(); }
            }
        };
        mThread.start();
        try {
            mThread.join();
            holder.fimg.setImageBitmap(bitmap);
            holder.fimg.setBackground(new ShapeDrawable(new OvalShape()));
            holder.fimg.setClipToOutline(true);
        } catch (Exception e) { e.printStackTrace(); }

        holder.flay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                Intent intent = new Intent(mView.getContext(), Shelf_TimelineActivity.class);
                intent.putExtra("uid", my.getUid());
                intent.putExtra("uname", my.getName());
                mView.getContext().startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return myList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView fname;
        public final ImageView fimg;
        public final LinearLayout flay;


        public ViewHolder(View view) {
            super(view);
            mView = view;
            fname = (TextView) view.findViewById(R.id.flist_name);
            fimg = (ImageView) view.findViewById(R.id.flist_img);
            flay = (LinearLayout) view.findViewById(R.id.flist_lin);

        }

    }

}