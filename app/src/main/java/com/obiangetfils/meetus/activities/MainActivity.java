package com.obiangetfils.meetus.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.obiangetfils.meetus.R;
import com.obiangetfils.meetus.activities.sign.SignInActivity;
import com.obiangetfils.meetus.adapters.UserAdapter;
import com.obiangetfils.meetus.listeners.UsersListener;
import com.obiangetfils.meetus.models.User;
import com.obiangetfils.meetus.utilities.Constants;
import com.obiangetfils.meetus.utilities.PreferenceManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity implements UsersListener {

    private PreferenceManager preferenceManager;
    private TextView textTitle, textSignOut;
    private RecyclerView usersRecyclerView;
    private List<User> users;
    private UserAdapter userAdapter;

    private TextView textErrorMessage;
    private SwipeRefreshLayout swiperefreshlayout;
    private String type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        preferenceManager = new PreferenceManager(getApplicationContext());
        textTitle = (TextView) findViewById(R.id.textTitle);
        textSignOut = (TextView) findViewById(R.id.textSignOut);
        usersRecyclerView = (RecyclerView) findViewById(R.id.usersRecyclerView);
        textErrorMessage = (TextView) findViewById(R.id.textErrorMessage);
        swiperefreshlayout = (SwipeRefreshLayout) findViewById(R.id.swiperefreshlayout);


        textTitle.setText(String.format(
                "%s %s",
                preferenceManager.getString(Constants.KEY_FIRST_NAME),
                preferenceManager.getString(Constants.KEY_LAST_NAME)
        ));

        textSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
            }
        });

        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
            @Override
            public void onComplete(@NonNull Task<InstanceIdResult> task) {
                if (task.isSuccessful() && task.getResult() != null) {
                    sendFCMTokenToDatabase(task.getResult().getToken());
                }
            }
        });

        users = new ArrayList<>();
        userAdapter = new UserAdapter(users, this);
        usersRecyclerView.setAdapter(userAdapter);

        swiperefreshlayout.setOnRefreshListener(this::getUsers);
        getUsers();
    }

    private void getUsers() {

        swiperefreshlayout.setRefreshing(true);
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        database.collection(Constants.KEY_COLLECTIONS_USERS)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        swiperefreshlayout.setRefreshing(false);
                        String myUsersId = preferenceManager.getString(Constants.KEY_USER_ID);
                        if (task.isSuccessful() && task.getResult() != null) {
                            users.clear();
                            for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                if (myUsersId.equals(documentSnapshot.getId())) {
                                    continue;
                                }
                                User user = new User();
                                user.firstName = documentSnapshot.getString(Constants.KEY_FIRST_NAME);
                                user.lastName = documentSnapshot.getString(Constants.KEY_LAST_NAME);
                                user.email = documentSnapshot.getString(Constants.KEY_EMAIL);
                                user.token = documentSnapshot.getString(Constants.KEY_FCM_TOKEN);
                                users.add(user);
                            }

                            if (users.size() > 0) {
                                userAdapter.notifyDataSetChanged();
                            } else {
                                textErrorMessage.setText(String.format("%s", "No Users Available"));
                                textErrorMessage.setVisibility(View.VISIBLE);
                            }

                        } else {
                            textErrorMessage.setText(String.format("%s", "No Users Available"));
                            textErrorMessage.setVisibility(View.VISIBLE);
                        }
                    }
                });

    }

    private void sendFCMTokenToDatabase(String token) {

        FirebaseFirestore database = FirebaseFirestore.getInstance();
        DocumentReference documentReference = database.collection(Constants.KEY_COLLECTIONS_USERS).document(
                preferenceManager.getString(Constants.KEY_USER_ID)
        );
        documentReference.update(Constants.KEY_FCM_TOKEN, token)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(MainActivity.this, "Token update successfully", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainActivity.this, "Unable to send Token: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void signOut() {
        Toast.makeText(this, "Sign Out ...", Toast.LENGTH_SHORT).show();
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        DocumentReference documentReference = database.collection(Constants.KEY_COLLECTIONS_USERS).document(
                preferenceManager.getString(Constants.KEY_USER_ID)
        );

        HashMap<String, Object> updates = new HashMap<>();
        updates.put(Constants.KEY_FCM_TOKEN, FieldValue.delete());
        documentReference.update(updates)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        preferenceManager.clearPreferences();
                        startActivity(new Intent(getApplicationContext(), SignInActivity.class));
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainActivity.this, "Unable To Sign Out!", Toast.LENGTH_SHORT).show();
                    }
                });

    }

    @Override
    public void initiateVideoMeeting(User user) {

        type = "video";
        if (user.token == null || user.token.trim().isEmpty()) {
            Toast.makeText(
                    this,
                    user.firstName + " " + user.lastName + " is not available for meeting",
                    Toast.LENGTH_SHORT).show();
        } else {
            startCall(user, type);
        }
    }

    @Override
    public void initiateAudioMeeting(User user) {
        type = "audio";
        if (user.token == null || user.token.trim().isEmpty()) {
            Toast.makeText(
                    this,
                    user.firstName + " " + user.lastName + " is not available for meeting",
                    Toast.LENGTH_SHORT).show();
        } else {
            startCall(user, type);
        }

    }

    private void startCall(User user, String type) {
        Intent intent = new Intent(getApplicationContext(), OutcomingInvitationActivity.class);
        intent.putExtra("user", user);
        intent.putExtra("type", type);
        startActivity(intent);
    }

}
