package e.user.afinal;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private EditText userName,userBranch,userMIS,userYear,userGender,userDOB;
    private Button UpdateAccountSettingsButton;
    private CircleImageView userProfImage;
    private String currentUserID;
    private ProgressDialog loadingBar;
    private StorageReference  UserProfileImageRef;

    private DatabaseReference SettingsuserRef;
    private FirebaseAuth mAuth;

    final static  int Gallery_Pic = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        SettingsuserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserID);
        loadingBar = new ProgressDialog(this);

        UserProfileImageRef = FirebaseStorage.getInstance().getReference().child("Profile Images");

        mToolbar = (Toolbar)findViewById(R.id.settings_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Account Settings");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        userName = (EditText)findViewById(R.id.settings_username);
        userMIS = (EditText)findViewById(R.id.settings_mis);
        userBranch = (EditText)findViewById(R.id.settings_branch);
        userYear = (EditText)findViewById(R.id.settings_year);
        userDOB = (EditText)findViewById(R.id.settings_dob);
        userGender = (EditText)findViewById(R.id.settings_gender);
        userProfImage = (CircleImageView)findViewById(R.id.settings_profile_image);

        UpdateAccountSettingsButton = (Button)findViewById(R.id.update_account_settings_button);


        SettingsuserRef.addValueEventListener(new ValueEventListener() {
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

                     Picasso.with(SettingsActivity.this).load(myProfileImage).placeholder(R.drawable.profile).into(userProfImage);
                     userName.setText(myuserName);
                     userMIS.setText(myMIS);
                     userBranch.setText(myBranch);
                     userGender.setText(myGender);
                     userYear.setText(myYear);
                     userDOB.setText(myDOB);
                 }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        UpdateAccountSettingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ValidateAccountInfo();
            }
        });
        userProfImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent =new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent,Gallery_Pic);
            }
        });
    }


    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == Gallery_Pic && resultCode ==RESULT_OK && data!=null){
            Uri ImageUri = data.getData();
            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1,1)
                    .start(this);

        }

        if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if(resultCode==RESULT_OK){
                loadingBar.setTitle("Profile Image");
                loadingBar.setMessage("Please wait for a while...");
                loadingBar.setCanceledOnTouchOutside(true);
                loadingBar.show();


                Uri resultUri = result.getUri();

                StorageReference filePath = UserProfileImageRef.child(currentUserID + ".jpg");
                filePath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if(task.isSuccessful()){
                            //SendUserToMainActivity();
                            Toast.makeText(SettingsActivity.this,"Profile Image saved",Toast.LENGTH_SHORT).show();
                            final String downloadUrl = task.getResult().getDownloadUrl().toString();
                            SettingsuserRef.child("profileimage").setValue(downloadUrl)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                Intent selfIntent = new Intent(SettingsActivity.this,SettingsActivity.class);
                                                startActivity(selfIntent);
                                                Toast.makeText(SettingsActivity.this,"Profile Image saved to Database",Toast.LENGTH_SHORT).show();
                                                loadingBar.dismiss();
                                            }
                                            else{
                                                String message = task.getException().getMessage();
                                                Toast.makeText(SettingsActivity.this,"Error Occured:"+ message,Toast.LENGTH_SHORT).show();
                                                loadingBar.dismiss();
                                            }
                                        }
                                    });
                            //loadingBar.dismiss();
                        }
                    }
                });
            }
            else{
                Toast.makeText(SettingsActivity.this,"Image can't be cropped.Try again",Toast.LENGTH_SHORT).show();
                loadingBar.dismiss();
            }
        }
    }

    private void ValidateAccountInfo() {
        String username = userName.getText().toString();
        String mis = userMIS.getText().toString();
        String branch = userBranch.getText().toString();
        String year = userYear.getText().toString();
        String gender = userGender.getText().toString();
        String dob = userDOB.getText().toString();

        if(TextUtils.isEmpty(username))
        {
            Toast.makeText(this,"Please write your Username...",Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(mis))
        {
            Toast.makeText(this,"Please write your MIS...",Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(branch))
        {
            Toast.makeText(this,"Please write your Branch...",Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(year))
        {
            Toast.makeText(this,"Please write your Academic Year...",Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(gender))
        {
            Toast.makeText(this,"Please write your Gender...",Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(dob))
        {
            Toast.makeText(this,"Please write your Date of Birth...",Toast.LENGTH_SHORT).show();
        }
        else
            {
                loadingBar.setTitle("Profile Image");
                loadingBar.setMessage("Please wait for a while...");
                loadingBar.setCanceledOnTouchOutside(true);
                loadingBar.show();

                UpdateAccountInforomation(username,mis,branch,year,gender,dob);
            }

    }

    private void UpdateAccountInforomation(String username, String mis, String branch, String year, String gender, String dob) {
        HashMap userMap = new HashMap();
           userMap.put("username",username);
            userMap.put("mis",mis);
            userMap.put("branch",branch);
            userMap.put("year",year);
            userMap.put("dob",dob);
            userMap.put("gender",gender);
        SettingsuserRef.updateChildren(userMap).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if(task.isSuccessful())
                {
                    SendUserToMainActivity();
                    Toast.makeText(SettingsActivity.this,"Account Information Updated Successfully",Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                }
                else
                    {
                        Toast.makeText(SettingsActivity.this,"Error Occured",Toast.LENGTH_SHORT).show();
                        loadingBar.dismiss();
                    }
            }
        });
    }
    private void SendUserToMainActivity() {
        Intent mainIntent = new Intent(SettingsActivity.this,MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }

}
