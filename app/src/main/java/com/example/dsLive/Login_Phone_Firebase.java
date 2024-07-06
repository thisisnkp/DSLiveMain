package com.example.dsLive;

import static com.example.dsLive.activity.LoginActivityActivity.login_google;
import static com.example.dsLive.user.EditProfileActivity.name_to_be_enter_EditProfile;
import static com.example.dsLive.user.EditProfileActivity.username_to_be_enter_EditProfile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.dsLive.activity.LoginActivityActivity;
import com.example.dsLive.activity.MainActivity;
import com.example.dsLive.dilog.CustomDialogClass;
import com.example.dsLive.modelclass.UserRoot;
import com.example.dsLive.retrofit.Const;
import com.example.dsLive.retrofit.RetrofitBuilder;
import com.example.dsLive.user.EditProfileActivity;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class Login_Phone_Firebase extends AppCompatActivity {

    private EditText phoneEditText, otpEditText;
    private CustomDialogClass customDialogClass;
    private Button sendOtpButton, verifyOtpButton;
    private FirebaseAuth mAuth;
    private String verificationId;
    public static boolean login_firebase;
    public static String phone_number_username;
    private String androidId;
    private String token;


    protected SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_phone_firebase);
        login_google =false;
        login_firebase=true;

        try {
            if(!name_to_be_enter_EditProfile.isEmpty() || !username_to_be_enter_EditProfile.isEmpty())
            {
                name_to_be_enter_EditProfile = "";
                username_to_be_enter_EditProfile = "";
            }
        }catch (Exception e)
        {
            Log.e("svesv",e.toString());
        }


        FirebaseApp.initializeApp(this);
        phoneEditText = findViewById(R.id.phoneEditText);
        otpEditText = findViewById(R.id.otpEditText);
        sendOtpButton = findViewById(R.id.sendOtpButton);
        verifyOtpButton = findViewById(R.id.verifyOtpButton);

        androidId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        customDialogClass = new CustomDialogClass(this, R.style.customStyle);
        customDialogClass.setCancelable(false);
        sessionManager = new SessionManager(this);

        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                //Log.e(, "Fetching FCM registration token failed", task.getException());
                return;
            }
            token = task.getResult();


        });


        mAuth = FirebaseAuth.getInstance();
        //FirebaseAuth.getInstance().getFirebaseAuthSettings().forceRecaptchaFlowForTesting(true);
        sendOtpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phoneNumber = phoneEditText.getText().toString().trim();
                if (phoneNumber.length() != 10) {
                    phoneEditText.setError("Valid 10 digit phone number is required");
                    phoneEditText.requestFocus();
                    return;
                }
                sendVerificationCode("+91"+phoneNumber);
            }
        });

        verifyOtpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String code = otpEditText.getText().toString().trim();
                if (code.isEmpty() || code.length() < 6) {
                    otpEditText.setError("Enter valid code");
                    otpEditText.requestFocus();
                    return;
                }
                verifyCode(code);
            }
        });

    }


    private void sendVerificationCode(String phoneNumber)
    {
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(phoneNumber)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // Activity (for callback binding)
                        .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks =
            new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                @Override
                public void onVerificationCompleted(@NonNull PhoneAuthCredential credential) {
                    // This callback will be invoked in two situations:
                    // 1 - Instant verification. In some cases the phone number can be instantly
                    //     verified without needing to send or enter a verification code.
                    // 2 - Auto-retrieval. On some devices Google Play services can automatically
                    //     detect the incoming verification SMS and perform verification without
                    //     user action.
                    String code = credential.getSmsCode();
                    if (code != null) {
                        otpEditText.setText(code);
                        verifyCode(code);
                    }
                }

                @Override
                public void onVerificationFailed(@NonNull FirebaseException e) {
                    Toast.makeText(Login_Phone_Firebase.this, e.getMessage(), Toast.LENGTH_LONG).show();
                }

                @Override
                public void onCodeSent(@NonNull String s,
                                       @NonNull PhoneAuthProvider.ForceResendingToken token) {
                    super.onCodeSent(s, token);
                    verificationId = s;
                }
            };

    private void verifyCode(String code)
    {
        if(phoneEditText.getText().toString().trim().length() != 10)
        {
            Toast.makeText(this, "Please Enter Valid 10 Digit Phone Number", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d("___Login_Phone_Firebase___", "code " + code);
        Log.d("___Login_Phone_Firebase___", "verificationId "+ verificationId);

        if(Objects.equals(code, "") || code == null || Objects.equals(verificationId, "") || verificationId == null)
        {
            Toast.makeText(this, "Please First Click On Send OTP", Toast.LENGTH_SHORT).show();
            return;
        }
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        signInWithCredential(credential);
    }

    private void signInWithCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                       try{
                           if(Objects.equals(name_to_be_enter_EditProfile, "") || Objects.equals(username_to_be_enter_EditProfile, ""))
                           {
                               name_to_be_enter_EditProfile = phoneEditText.toString();
                               username_to_be_enter_EditProfile = phoneEditText.toString();
                           }
                       }catch (Exception e)
                       {
                           Log.e("ave", e.toString());
                       }

                        JsonObject jsonObject = new JsonObject();
                        jsonObject.addProperty("name", name_to_be_enter_EditProfile);
                        jsonObject.addProperty("gender", "");
                        jsonObject.addProperty("image", "");
                        jsonObject.addProperty("email", androidId);
                        jsonObject.addProperty("loginType", 2);
                        jsonObject.addProperty("username", androidId);
                        sendData(jsonObject);


                        // Redirect to another activity
                    } else {
                        // Sign in failed, display a message and update the UI
                        Toast.makeText(Login_Phone_Firebase.this, "Login Failed", Toast.LENGTH_SHORT).show();
                        Toast.makeText(Login_Phone_Firebase.this, "Try Google Login", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void sendData(JsonObject jsonObject) {
        customDialogClass.show();
        jsonObject.addProperty("age", 18);
        jsonObject.addProperty("country", sessionManager.getStringValue(Const.COUNTRY));
        jsonObject.addProperty("ip", sessionManager.getStringValue(Const.IPADDRESS));
        jsonObject.addProperty("identity", androidId);
        jsonObject.addProperty("fcmToken", token);

        Call<UserRoot> call = RetrofitBuilder.create().createUser(jsonObject);
        call.enqueue(new Callback<UserRoot>() {
            @Override
            public void onResponse(Call<UserRoot> call, Response<UserRoot> response) {
                if (response.code() == 200) {

                    try {
                        assert response.body() != null;
//                        Log.v("___LOGIN_ACTIVITY___", response.body().toString());
                        UserRoot user = response.body();
                        String comeplete_json_response = new Gson().toJson(user);
                        Log.v("___LOGIN_PHONE_ACTIVITY___", comeplete_json_response);
                        Log.i("___LOGIN_PHONE_ACTIVITY___", response.body().getUser().getId());
                        Log.i("___LOGIN_PHONE_ACTIVITY___", response.body().getUser().getIdentity());
                        Log.i("___LOGIN_PHONE_ACTIVITY___", response.body().getUser().getUniqueId());

                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }

                    if (response.body().isStatus() && response.body().getUser() != null) {
                        sessionManager.saveUser(response.body().getUser());
                        //Log.d(TAG, "onResponse: quick login " + response.body().getUser());
                        Toast.makeText(Login_Phone_Firebase.this, "Login Successful", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(Login_Phone_Firebase.this, EditProfileActivity.class);
                        startActivity(intent);
                        customDialogClass.dismiss();
                    }
                }
            }

            @Override
            public void onFailure(Call<UserRoot> call, Throwable t) {

                t.printStackTrace();
            }
        });
    }

}