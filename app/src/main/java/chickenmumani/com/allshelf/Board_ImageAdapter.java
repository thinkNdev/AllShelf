package chickenmumani.com.allshelf;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

public class Board_ImageAdapter extends RecyclerView.Adapter<Board_ImageAdapter.ViewHolder> {

    private List<Shelf_Item> myList;
    private OnItemClick mCallback;
    Bitmap bitmap;
    String it;

    public Board_ImageAdapter(List<Shelf_Item> list, String it, OnItemClick listener) {
        this.myList = list;
        this.it = it;
        this.mCallback = listener;
    }

    @Override

    public Board_ImageAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;
        if(it.equals("i"))
            v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.view_shelf_image, parent, false);
        else
            v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.view_shelf_text, parent, false);
        return new Board_ImageAdapter.ViewHolder(v);
    }


    @Override
    public void onBindViewHolder(final Board_ImageAdapter.ViewHolder holder, int position) {
        final Shelf_Item my = myList.get(position);
        ImageView img = holder.noimg;

        Thread mThread = new Thread() {
            @Override
            public void run() {
                try {
                    URL url = new URL(my.getImgurl());

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
            img.setImageBitmap(bitmap);
        } catch (Exception e) { e.printStackTrace(); }

        if(it.equals("t")) {
            holder.notit.setText(my.getTitle());
            holder.noaut.setText(my.getAuthor());
            holder.noisbn.setText(my.getIsbn());

        }

        holder.nolay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                mCallback.onClick(my);
            }
        });
    }

    @Override
    public int getItemCount() {
        return myList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public View mView;
        public ImageView noimg;
        public LinearLayout nolay;
        public TextView notit;
        public TextView noaut;
        public TextView noisbn;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            if(it == "i") {
                noimg = (ImageView) view.findViewById(R.id.shelfview_img);
                nolay = (LinearLayout) view.findViewById(R.id.shelfview_ilay);
            } else {
                noimg = (ImageView) view.findViewById(R.id.shelfview_timg);
                nolay = (LinearLayout) view.findViewById(R.id.shelfview_tlay);
                notit = (TextView) view.findViewById(R.id.shelfview_ttitle);
                noaut = (TextView) view.findViewById(R.id.shelfview_tauthor);
                noisbn = (TextView) view.findViewById(R.id.shelfview_tisbn);
            }
        }
    }
}