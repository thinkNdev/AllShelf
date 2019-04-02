package chickenmumani.com.allshelf;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // 포그라운드에서만 작동함
        Log.d("Firebase", "From: " + remoteMessage.getFrom());
        Log.d("Firebase", "Notification Message Body: " + remoteMessage.getNotification().getBody());
    }

}
