package chickenmumani.com.allshelf;

import android.content.Context;
import android.content.Intent;
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

public class Board_Adapter extends RecyclerView.Adapter<Board_Adapter.ViewHolder> {
    private List<Board_Item> myList;
    private String tag;
    private Bitmap bitmap;

    public Board_Adapter(List<Board_Item> list, String tag) {
        this.myList = list;
        this.tag = tag;
    }

    @Override
    public Board_Adapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_board, parent, false);
        return new Board_Adapter.ViewHolder(v);
    }


    @Override
    public void onBindViewHolder(final Board_Adapter.ViewHolder holder, int position) {
        final Board_Item my = myList.get(position);
        ImageView imgView = holder.boardBookImg;
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
            imgView.setImageBitmap(bitmap);
        } catch (Exception e) { e.printStackTrace(); }

        holder.boardTitle.setText(my.getTitle());
        holder.boardDate.setText(my.getDate());
        holder.boardUser.setText(my.getName());
        holder.boardBookName.setText(my.getBookname());

        holder.boardLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                Context context = v.getContext();
                final Intent intent = new Intent(context, Board_ContentActivity.class);
                intent.putExtra("id", my.getDbid());
                intent.putExtra("uuid", my.getUuid());
                intent.putExtra("name", my.getName());
                intent.putExtra("title", my.getTitle());
                intent.putExtra("date", my.getDate());
                intent.putExtra("content", my.getContent());
                intent.putExtra("bookname", my.getBookname());
                intent.putExtra("isbn", my.getIsbn());
                intent.putExtra("imgurl", my.getImgurl());
                intent.putExtra("tag", tag);
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
        public final TextView boardTitle;
        public final TextView boardDate;
        public final TextView boardUser;
        public final TextView boardBookName;
        public final ImageView boardBookImg;
        public final LinearLayout boardLayout;


        public ViewHolder(View view) {
            super(view);
            mView = view;
            boardTitle = (TextView) view.findViewById(R.id.boardview_title);
            boardDate = (TextView) view.findViewById(R.id.boardview_date);
            boardUser = (TextView) view.findViewById(R.id.boardview_user);
            boardBookName = (TextView) view.findViewById(R.id.boardview_book);
            boardBookImg = (ImageView) view.findViewById(R.id.boardview_image);
            boardLayout = (LinearLayout) view.findViewById(R.id.boardview_lay);
        }
    }
}