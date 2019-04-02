package chickenmumani.com.allshelf;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.support.annotation.NonNull;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;


public class Post_Adapter extends RecyclerView.Adapter<Post_Adapter.ViewHolder> {

    private List<Post_Item> myList;
    Bitmap bitmap;
    Drawable upro;
    boolean is_uid_isbn;
    String uid, uname, isbn, urls, bookname;
    FirebaseStorage storage;
    StorageReference storageRef;
    private DatabaseReference mDatabase;
    public View mView;
    FirebaseUser user;

    public Post_Adapter(List<Post_Item> list, String uid, String uname, Drawable upro) {
        this.myList = list;
        this.uid = uid;
        this.uname = uname;
        this.upro = upro;
        is_uid_isbn = FALSE;
        user = FirebaseAuth.getInstance().getCurrentUser();
    }

    public Post_Adapter(List<Post_Item> list, String isbn, String bookname) {
        this.myList = list;
        this.isbn = isbn;
        this.bookname = bookname;
        is_uid_isbn = TRUE;
        user = FirebaseAuth.getInstance().getCurrentUser();
    }

    @Override

    public Post_Adapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;
        v = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_post, parent, false);
        return new Post_Adapter.ViewHolder(v);
    }


    @Override
    public void onBindViewHolder(final Post_Adapter.ViewHolder holder, int position) {
        final Post_Item my = myList.get(position);
        final ImageView popro = holder.popro;
        final ImageView poimg = holder.poimg;
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();

        StorageReference islandRef = storageRef.child(my.getPostimg());
        final long ONE_MEGABYTE = 1024 * 1024;
        islandRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                poimg.setImageBitmap(BitmapFactory.decodeByteArray(bytes, 0, bytes.length));
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
            }
        });

        if(is_uid_isbn) {
            mDatabase = FirebaseDatabase.getInstance().getReference("User_Info")
                    .child(my.getUid()).child("Profile_Image");
            ValueEventListener postListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    urls = (String)dataSnapshot.getValue();
                    Thread mThread = new Thread() {
                        @Override
                        public void run() {
                            try {
                                URL url = new URL(urls);

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
                        popro.setImageBitmap(bitmap);
                    } catch (Exception e) { e.printStackTrace(); }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            };
            mDatabase.addListenerForSingleValueEvent(postListener);

        } else {
            popro.setImageDrawable(upro);
        }

        popro.setBackground(new ShapeDrawable(new OvalShape()));
        popro.setClipToOutline(true);

        if(my.getIsfav()) holder.poisfav.setImageResource(R.drawable.ic_favorite_orange_24dp);
        else holder.poisfav.setImageResource(R.drawable.ic_favorite_gray_24dp);

        holder.poname.setText(my.getUname());
        holder.podate.setText(my.getDate());
        holder.pofavcount.setText(String.valueOf(my.getFavcount()));
        holder.porevtext.setText(my.getPosttext());
        holder.poratingbar.setRating(my.getStar());
        holder.pobook.setText(my.getBook());

        holder.pobutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                PopupMenu popup = new PopupMenu(holder.pobutton.getContext(), holder.pobutton);
                if(user.getUid().equals(uid)) popup.inflate(R.menu.post_menu2);
                else popup.inflate(R.menu.post_menu1);
            }
        });

        holder.poname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), Shelf_TimelineActivity.class);
                intent.putExtra("uid", my.getUid());
                intent.putExtra("uname", my.getUname());
                view.getContext().startActivity(intent);
                if(user.getUid().equals(my.getUid()) && !is_uid_isbn) {
                    ((Activity)view.getContext()).finish();
                }
            }
        });

        holder.popro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), Shelf_TimelineActivity.class);
                intent.putExtra("uid", my.getUid());
                intent.putExtra("uname", my.getUname());
                view.getContext().startActivity(intent);
                if(!is_uid_isbn && user.getUid().equals(my.getUid())) {
                    ((Activity)view.getContext()).finish();
                }
            }
        });

        holder.pobook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), Shelf_ReviewlistActivity.class);
                intent.putExtra("uid",my.getUid());
                intent.putExtra("isbn",isbn);
                intent.putExtra("bookname",my.getBook());
                intent.putExtra("cover",my.getPostimg());
                view.getContext().startActivity(intent);
                if(is_uid_isbn && isbn.equals(my.getIsbn()) ) {
                    ((Activity)view.getContext()).finish();
                }
            }
        });

        holder.poisfav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!my.getIsfav()) {
                    FirebaseDatabase.getInstance().getReference("Review").child("ReviewList").
                            child(my.getKey()).child("Good").child(user.getUid()).setValue(user.getDisplayName());
                    holder.poisfav.setImageResource(R.drawable.ic_favorite_orange_24dp);
                }
            }
        });

        holder.pobutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                Context wrapper = new ContextThemeWrapper(v.getContext().getApplicationContext(), R.style.MyPopupMenu);
                PopupMenu popup = new PopupMenu(wrapper, v);
                if(my.getUid().equals(user.getUid())) ((Activity)v.getContext()).getMenuInflater().inflate(R.menu.post_menu2, popup.getMenu());
                else ((Activity)v.getContext()).getMenuInflater().inflate(R.menu.post_menu1, popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if(item.getItemId() == R.id.action_edit) {
                            Intent intent = new Intent(mView.getContext(), Review_EditActivity.class);
                            intent.putExtra("num", my.getKey());
                            ((Activity)mView.getContext()).startActivity(intent);
                        } else if(item.getItemId() == R.id.action_report) {
                            Intent intent = new Intent(mView.getContext(), Report_Activity.class);
                            intent.putExtra("num", my.getKey());
                            intent.putExtra("post_type", "review");
                            intent.putExtra("uid", user.getUid());
                            ((Activity)mView.getContext()).startActivity(intent);
                        }
                        return false;
                    }
                } );
                popup.show();
            }
        });

        holder.polin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), Review_OneActivity.class);
                intent.putExtra("num", my.getKey());
                view.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() { return myList.size(); }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView popro;
        public TextView poname;
        public TextView pobook;
        public RatingBar poratingbar;
        public TextView podate;
        public TextView pofavcount;
        public ImageButton poisfav;
        public ImageView poimg;
        public TextView porevtext;
        public ImageView pobutton;
        public LinearLayout polin;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            popro = (ImageView)view.findViewById(R.id.post_img);
            poname = (TextView)view.findViewById(R.id.post_name);
            pobook = (TextView)view.findViewById(R.id.post_book);
            poratingbar = (RatingBar)view.findViewById(R.id.post_ratingbar);
            podate = (TextView)view.findViewById(R.id.post_date);
            pofavcount = (TextView)view.findViewById(R.id.post_favcount);
            poisfav = (ImageButton) view.findViewById(R.id.post_isfav);
            poimg = (ImageView) view.findViewById(R.id.post_revimg);
            porevtext = (TextView) view.findViewById(R.id.post_revtext);
            pobutton = (ImageButton) view.findViewById(R.id.post_button);
            polin = (LinearLayout) view.findViewById(R.id.post_lay);
        }

    }

}