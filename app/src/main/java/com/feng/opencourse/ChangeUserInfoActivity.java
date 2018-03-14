package com.feng.opencourse;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.feng.opencourse.entity.UserBase;
import com.feng.opencourse.entity.UserData;

public class ChangeUserInfoActivity extends AppCompatActivity {

    private UserData userData;
    private UserBase userBase;

    private ImageView ivAvatar;
    private EditText etNickname;
    private EditText etName;
    private EditText etPhone;
    private EditText etEmail;
    private EditText etPassword;
    private Button btnChange;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_user_info);

        ivAvatar = (ImageView) findViewById(R.id.iv_change_user_info_avatar);
        etNickname = (EditText) findViewById(R.id.et_change_user_info_nickname);
        etName = (EditText) findViewById(R.id.et_change_user_info_realname);
        etPhone = (EditText) findViewById(R.id.et_change_user_info_phone);
        etEmail = (EditText) findViewById(R.id.et_change_user_info_email);
        etPassword = (EditText) findViewById(R.id.et_change_user_info_password);
        btnChange = (Button) findViewById(R.id.btn_change_user_info);

        Bundle bundle=this.getIntent().getExtras();
        userBase = (UserBase) bundle.getSerializable("userBase");
        userData = (UserData) bundle.getSerializable("userData");
        etNickname.setText(userData.getNickname());
        etName.setText(userBase.getUserName());
        etPhone.setText(userBase.getPhone());
        etEmail.setText(userBase.getEmail());
        etPassword.setText(userBase.getPassword());


    }
}
