package chickenmumani.com.allshelf;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import chickenmumani.com.allshelf.R;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String TAG = "GoogleActivity";
    private static final int RC_SIGN_IN = 9001;
    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);

        findViewById(R.id.sign_in_button).setOnClickListener(this);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        mAuth = FirebaseAuth.getInstance();

        progressBar = (ProgressBar) findViewById(R.id.mainProgressBar);
        progressBar.setVisibility(View.INVISIBLE);

        /*


        Button btn_go1 = (Button) findViewById(R.id.mainButton1);
        btn_go1.setOnClickListener(
                new Button.OnClickListener() {
                    public void onClick(View v) {
                        Intent intent = new Intent(v.getContext(), Reviewview_Activity.class);
                        startActivity(intent);
                    }
                }
        );

        Button btn_go8 = (Button) findViewById(R.id.mainButton8);
        btn_go8.setOnClickListener(
                new Button.OnClickListener() {
                    public void onClick(View v) {
                        Intent intent = new Intent(v.getContext(), Navi_Activity.class);
                        startActivity(intent);
                    }
                }
        );



        Button btn_go2 = (Button) findViewById(R.id.mainButton2);
        btn_go2.setOnClickListener(
                new Button.OnClickListener() {
                    public void onClick(View v) {
                        Intent intent = new Intent(v.getContext(), AlarmActivity.class);
                        startActivity(intent);
                    }
                }
        );

        Button btn_go3 = (Button) findViewById(R.id.mainButton3);
        btn_go3.setOnClickListener(
                new Button.OnClickListener() {
                    public void onClick(View v) {
                        Intent intent = new Intent(v.getContext(), Logout_Activity.class);
                        startActivityForResult(intent, 1);
                    }
                }
        );

        Button btn_go4 = (Button) findViewById(R.id.mainButton4);
        btn_go4.setOnClickListener(
                new Button.OnClickListener() {
                    public void onClick(View v) {
                        Intent intent = new Intent(v.getContext(), SettingsActivity.class);
                        startActivity(intent);
                    }
                }
        );

        Button btn_go5 = (Button) findViewById(R.id.mainButton5);
        btn_go5.setOnClickListener(
                new Button.OnClickListener() {
                    public void onClick(View v) {
                        Intent intent = new Intent(v.getContext(), Export_Activity2.class);
                        startActivity(intent);
                    }
                }
        );

        Button btn_go6 = (Button) findViewById(R.id.mainButton6);
        btn_go6.setOnClickListener(
                new Button.OnClickListener() {
                    public void onClick(View v) {
                        Intent intent = new Intent(v.getContext(), FriendManageActivity.class);
                        startActivity(intent);
                    }
                }
        );

        Button btn_go7 = (Button) findViewById(R.id.mainButton7);
        btn_go7.setOnClickListener(
                new Button.OnClickListener() {
                    public void onClick(View v) {
                        Intent intent = new Intent(v.getContext(), Message_PopActivity.class);
                        startActivityForResult(intent, 2);
                    }
                }
        ); */



    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.sign_in_button) {
            progressBar.setVisibility(View.VISIBLE);
            signIn();
            progressBar.setVisibility(View.INVISIBLE);
        }
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        progressBar.setVisibility(View.INVISIBLE);
        Log.w(TAG, "start signin");
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            Log.d("w",user.getUid() + "\t" + user.getEmail() + "\t" + user.getProviderId() + user.getPhotoUrl());
            Intent nextActivityIntent = new Intent(MainActivity.this, Navi_Activity.class);
            startActivity(nextActivityIntent);
            finish();
        } else {
            findViewById(R.id.sign_in_button).setVisibility(View.VISIBLE);
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Snackbar.make(findViewById(R.id.layout_main), "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            progressBar.setVisibility(View.VISIBLE);
            try {
                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                Log.w(TAG,"intent data" + data.toString());
                // Google Sign In failed, update UI appropriately
                progressBar.setVisibility(View.INVISIBLE);
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);     // 여기서 this는 Activity의 this
                builder .setMessage("정보를 불러오는 중 문제가 발생했습니다. 다시 시도하세요.")
                        .setCancelable(false)
                        .setPositiveButton("닫기", null);

                AlertDialog dialog = builder.create();    // 알림창 객체 생성
                dialog.show();
                Log.w(TAG, "Google sign in failed", e);
                updateUI(null);
            }
        }

        if(requestCode==1){
            if(resultCode==RESULT_OK){
                //데이터 받기
                //String result = data.getStringExtra("result");
                //txtResult.setText("1 " + result);
            }
        }

        if(requestCode==2){
            if(resultCode==RESULT_OK){
                //데이터 받기
                //String result = data.getStringExtra("result");
                //txtResult.setText("2 " + result);
            }
        }
    }
}
