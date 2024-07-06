package com.example.dsLive.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;

import com.example.dsLive.FaceBookLoginManager;
import com.example.dsLive.GoogleLoginManager;
import com.example.dsLive.Login_Phone_Firebase;
import com.example.dsLive.R;
import com.example.dsLive.databinding.ActivityLoginActivityBinding;
import com.example.dsLive.dilog.CustomDialogClass;
import com.example.dsLive.modelclass.UserRoot;
import com.example.dsLive.retrofit.Const;
import com.example.dsLive.retrofit.RetrofitBuilder;
import com.example.dsLive.user.EditProfileActivity;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivityActivity extends BaseActivity {

    private static final int RC_SIGN_IN = 100;
    private static final String TAG = "loginacttttttttttt";
    GoogleLoginManager googleLoginManager;
    ActivityLoginActivityBinding binding;
    FaceBookLoginManager faceBookLoginManager;
    private CustomDialogClass customDialogClass;
    private String androidId;
    private String token;
    public static boolean login_google = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login_activity);
        getWindow().setStatusBarColor(Color.parseColor("#130B1E"));

        androidId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        customDialogClass = new CustomDialogClass(this, R.style.customStyle);
        customDialogClass.setCancelable(false);

        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.e(TAG, "Fetching FCM registration token failed", task.getException());
                return;
            }
            token = task.getResult();

            initMain();

        });



    }

    private void initMain() {
        Log.d(TAG, "On Init Called ");

        googleLoginManager = new GoogleLoginManager(this, new GoogleLoginManager.OnGoogleLoginListner() {
            @Override
            public void onLoginSuccess(GoogleLoginManager.GoogleUser googleUser) {
                Toast.makeText(LoginActivityActivity.this, "Automatically Login In To Google", Toast.LENGTH_SHORT).show();
                Log.w(TAG, "On Login Success");
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("name", googleUser.getName());
                // jsonObject.addProperty("gender", "");
                if (googleUser.getImage() != null && !googleUser.getImage().isEmpty()) {
                    jsonObject.addProperty("image", googleUser.getImage());
                }
                jsonObject.addProperty("email", googleUser.getEmail());
                jsonObject.addProperty("loginType", 0);
                //  jsonObject.addProperty("username", "");
                sendData(jsonObject);
            }

            @Override
            public void onFailure(String err) {
                Toast.makeText(LoginActivityActivity.this, "Unable To Automatically Login To Google ", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "onFailure: " + err);
            }
        });

        binding.btnGoogleLogin.setOnClickListener(view -> {
            onClickGoogle();
        });

        binding.btnphoneLogin.setOnClickListener(view -> {
            //onClickQuick();
            signin_through_firebase_phone();
        });

        binding.quickLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickQuick();
            }
        });

    }
    private void signin_through_firebase_phone()
    {

        Intent intent = new Intent(LoginActivityActivity.this, Login_Phone_Firebase.class);
        startActivity(intent);

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
                        Log.v("___LOGIN_ACTIVITY___", comeplete_json_response);
                        Log.i("___LOGIN_ACTIVITY___", response.body().getUser().getId());
                        Log.i("___LOGIN_ACTIVITY___", response.body().getUser().getIdentity());
                        Log.i("___LOGIN_ACTIVITY___", response.body().getUser().getUniqueId());

                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }

                    if (response.body().isStatus() && response.body().getUser() != null) {
                        sessionManager.saveUser(response.body().getUser());
                        Log.d(TAG, "onResponse: quick login " + response.body().getUser());
                        checkData();
                    }
                }
            }

            @Override
            public void onFailure(Call<UserRoot> call, Throwable t) {

                t.printStackTrace();
            }
        });
    }

    private void checkData() {
        UserRoot.User user = sessionManager.getUser();
        if (user.getUsername().isEmpty() || user.getGender().isEmpty()) {
            customDialogClass.dismiss();
            startActivity(new Intent(this, EditProfileActivity.class));
        } else {
            customDialogClass.dismiss();
            sessionManager.saveBooleanValue(Const.ISLOGIN, true);
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
    }


    public void onClickGoogle() {
        googleLoginManager.onLogin();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: ");
        if (requestCode == RC_SIGN_IN)
        {
            if (resultCode == Activity.RESULT_OK) {
                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                googleLoginManager.handleSignInResult(task);
                Log.i(TAG,"Activity Google Login Is Successful");
                login_google = true;
            } else
            {
                Log.w(TAG, "failed, user denied OR no network OR jks SHA1 not configure yet at play console android project");
            }
        } else
        {
            Log.w(TAG,"Calling Result For Facebook");
            faceBookLoginManager.callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void onClickFacebook(View view) {

    }


    public void onClickQuick() {
        Log.d(TAG, "onClickQuick: quick login clicked");
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("name", "");
        jsonObject.addProperty("gender", "");
        jsonObject.addProperty("image", "");
        jsonObject.addProperty("email", androidId);
        jsonObject.addProperty("loginType", 2);
        jsonObject.addProperty("username", androidId);
        sendData(jsonObject);
    }

    public void onClickPrivacy(View view) {
        WebActivity.open(this, "Privacy Policy", sessionManager.getSetting().getPrivacyPolicyLink());
    }

    public enum LoginType {
        google, facebook, quick, mobile;
    }

    private void FB_LOGIN()
    {
        try {
            faceBookLoginManager = new FaceBookLoginManager(this, facebookObject -> {
                Log.d(TAG, "onLoginSuccess: facebook  " + facebookObject.toString());
                JsonObject jsonObject = new JsonObject();
                try {
                    jsonObject.addProperty("email", facebookObject.getString("email"));
                    jsonObject.addProperty("name", facebookObject.getString("name"));

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                try {
                    jsonObject.addProperty("image", "https://graph.facebook.com/" + facebookObject.getString("id") + "/picture?type=large");
                } catch (JSONException e) {
                    jsonObject.addProperty("image", "");
                    e.printStackTrace();
                }

                jsonObject.addProperty("loginType", 0);
                sendData(jsonObject);
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        Dialog dialog = new Dialog(LoginActivityActivity.this);
        dialog.setContentView(R.layout.exitdialog);
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        TextView yes = dialog.findViewById(R.id.tv_exit);
        TextView no = dialog.findViewById(R.id.tvno);

        yes.setOnClickListener(v -> {
            super.onBackPressed();
            dialog.dismiss();
            finishAffinity();
        });

        no.setOnClickListener(v -> {
            dialog.dismiss();

        });

        dialog.show();

    }

}