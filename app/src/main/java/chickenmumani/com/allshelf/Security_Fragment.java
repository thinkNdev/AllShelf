package chickenmumani.com.allshelf;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import chickenmumani.com.allshelf.R;

public class Security_Fragment extends Fragment {

    private FirebaseAuth mAuth;
    Bitmap bitmap;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_security,container,false);

        mAuth = FirebaseAuth.getInstance();
        final FirebaseUser user = mAuth.getCurrentUser();

        TextView name = (TextView) v.findViewById(R.id.sec_name);
        TextView email = (TextView) v.findViewById(R.id.sec_email);
        ImageView img = (ImageView) v.findViewById(R.id.sec_image);

        NavigationView navigationView = (NavigationView) getActivity().findViewById(R.id.nav_view);
        View hView =  navigationView.getHeaderView(0);

        name.setText(((TextView)hView.findViewById(R.id.header_name)).getText());
        email.setText(((TextView)hView.findViewById(R.id.header_email)).getText());
        img.setImageDrawable(((ImageView)hView.findViewById(R.id.header_img)).getDrawable());
        img.setBackground(new ShapeDrawable(new OvalShape()));
        img.setClipToOutline(true);

        LinearLayout l = (LinearLayout) v.findViewById(R.id.sec_logout);
        l.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());     // 여기서 this는 Activity의 this
                builder .setMessage("로그아웃 하시겠습니까?")
                        .setCancelable(false)
                        .setPositiveButton("예", new DialogInterface.OnClickListener(){
                            public void onClick(DialogInterface dialog, int whichButton){
                                FirebaseAuth.getInstance().signOut();
                                Intent intent = new Intent(getActivity(), MainActivity.class);
                                startActivity(intent);
                                getActivity().finish();
                            }
                        })
                        .setNegativeButton("아니오", new DialogInterface.OnClickListener(){
                            public void onClick(DialogInterface dialog, int whichButton){
                                dialog.cancel();
                            }
                        });
                AlertDialog dialog = builder.create();    // 알림창 객체 생성
                dialog.show();    // 알림창 띄우기
            }
        });

        LinearLayout b = (LinearLayout) v.findViewById(R.id.lin_exportb);
        b.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                Intent intent = new Intent(getActivity(), Export_Activity.class);
                startActivity(intent);
            }
        });

        LinearLayout c = (LinearLayout) v.findViewById(R.id.lin_withb);
        c.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                Intent intent = new Intent(getActivity(), WithDrawal_Activity.class);
                startActivity(intent);
            }
        });

        LinearLayout d = (LinearLayout) v.findViewById(R.id.lin_versionb);
        d.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                Intent intent = new Intent(getActivity(), Version_Activity.class);
                startActivity(intent);
            }
        });

        return v;
    }

    public void onResume(){
        super.onResume();
        ((Navi_Activity) getActivity())
                .setActionBarTitle("개인/보안");

    }


    @Override
    public void onDetach() {
        super.onDetach();
    }
}