package e.user.afinal;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    private TextView userName,userBranch,userMIS,userYear,userGender,userDOB;
    private CircleImageView userProfileImage;
    private DatabaseReference profileUserRef;
    private FirebaseAuth mAuth;
    private String currentUserID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        profileUserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserID);

        userName = (TextView) findViewById(R.id.my_username);
        userMIS = (TextView) findViewById(R.id.my_mis);
        userBranch = (TextView) findViewById(R.id.my_branch);
        userYear = (TextView) findViewById(R.id.my_year);
        userDOB = (TextView) findViewById(R.id.my_dob);
        userGender = (TextView) findViewById(R.id.my_gender);
        userProfileImage = (CircleImageView)findViewById(R.id.my_profile_pic);

        profileUserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    String myProfileImage = dataSnapshot.child("profileimage").getValue().toString();
                    String myuserName = dataSnapshot.child("username").getValue().toString();
                    String myMIS = dataSnapshot.child("mis").getValue().toString();
                    String myBranch = dataSnapshot.child("branch").getValue().toString();
                    String myGender = dataSnapshot.child("gender").getValue().toString();
                    String myYear = dataSnapshot.child("year").getValue().toString();
                    String myDOB = dataSnapshot.child("dob").getValue().toString();

                    Picasso.with(ProfileActivity.this).load(myProfileImage).placeholder(R.drawable.profile).into(userProfileImage);
                    userName.setText("@" + myuserName);
                    userMIS.setText("MIS:"+ myMIS);
                    userBranch.setText("Branch:"+ myBranch);
                    userGender.setText("Gender:" + myGender);
                    userYear.setText("Year:" + myYear);
                    userDOB.setText("DOB:" + myDOB);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
