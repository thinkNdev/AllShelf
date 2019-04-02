package chickenmumani.com.allshelf;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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


public class Search_Adapter extends RecyclerView.Adapter<Search_Adapter.ViewHolder> {

    private List<Search_Item> myList;
    Bitmap bitmap;

    public Search_Adapter(List<Search_Item> list) {
        this.myList = list;
    }

    @Override

    public Search_Adapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.view_searched, parent, false);
        return new Search_Adapter.ViewHolder(v);
    }


    @Override
    public void onBindViewHolder(final Search_Adapter.ViewHolder holder, int position) {
        final Search_Item my = myList.get(position);
        holder.setit.setText(my.getTitle());
        holder.seaut.setText(my.getAuthor());
        holder.secom.setText(my.getCompany());
        holder.seisbn.setText(my.getIsbn());

        Thread mThread = new Thread() {
            @Override
            public void run() {
                try {
                    URL url = new URL(my.getCover());

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
            holder.seimg.setImageBitmap(bitmap);
        } catch (Exception e) { e.printStackTrace(); }

        holder.nolay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                Context context = v.getContext();
                Intent intent = new Intent(context, BookInfo_Activity.class);
                intent.putExtra("barcodeContents",my.getIsbn());
                context.startActivity(intent);
                ((Activity)context).finish();
            }
        });

    }

    @Override
    public int getItemCount() {
        return myList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public final View mView;
        public final TextView setit;
        public final TextView seaut;
        public final TextView secom;
        public final TextView seisbn;
        public final ImageView seimg;
        public final LinearLayout nolay;


        public ViewHolder(View view) {
            super(view);
            mView = view;
            setit = (TextView) view.findViewById(R.id.searchview_title);
            seaut = (TextView) view.findViewById(R.id.searchview_author);
            secom = (TextView) view.findViewById(R.id.searchview_company);
            seisbn = (TextView) view.findViewById(R.id.searchview_isbn);
            seimg = (ImageView) view.findViewById(R.id.searchview_img);
            nolay = (LinearLayout) view.findViewById(R.id.searchview_lay);

        }

    }

}