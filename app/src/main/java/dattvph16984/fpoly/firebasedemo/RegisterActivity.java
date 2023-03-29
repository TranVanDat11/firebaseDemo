package dattvph16984.fpoly.firebasedemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegisterActivity extends AppCompatActivity {
        Button btnRegister;
        EditText edNameRgt,edPasswordRgt,edPasswordRes;
        String TAG = "zzzzzzz";
        private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        init();
        initListener();

    }
    private void init() {
        progressDialog = new ProgressDialog(this);
        btnRegister= findViewById(R.id.btn_registerRgt);
        edNameRgt= findViewById(R.id.ed_name_rgt);
        edPasswordRgt= findViewById(R.id.ed_pass_rgt);
    }
    private void initListener(){
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickRegister();
            }
        });
    }
    private void onClickRegister(){
        String emailRgt = edNameRgt.getText().toString().trim();
        String passRgt = edPasswordRgt.getText().toString().trim();
         FirebaseAuth fAuth = FirebaseAuth.getInstance();
         progressDialog.show();
        fAuth.createUserWithEmailAndPassword(emailRgt, passRgt)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressDialog.dismiss();
                        if (task.isSuccessful()) {
                            Toast.makeText(RegisterActivity.this, "Đăng ký thành công", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(RegisterActivity.this,LoginActivity.class);
                            startActivity(intent);
                            finishAffinity();
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(RegisterActivity.this, "Tài khoản đã tồn tại.",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
            private void updateUI(FirebaseUser user) {
                // hàm này dùng để viết cái gì tương tác với giao diện thì viết.
                if (user != null){
                    Toast.makeText(RegisterActivity.this, "Tài khoản và mật khẩu đã tồn tại", Toast.LENGTH_SHORT).show();
                }else if(edPasswordRgt.getText().toString().trim() == edPasswordRes.getText().toString().trim()){
                    Toast.makeText(RegisterActivity.this, "Mật khẩu nhập lại chưa đúng", Toast.LENGTH_SHORT).show();
                }else if(edNameRgt.getText().toString().trim().isEmpty() &&edPasswordRgt.getText().toString().trim().isEmpty()){
                    Toast.makeText(RegisterActivity.this, "Tên tài khoản và mật khẩu k được để trống!", Toast.LENGTH_SHORT).show();
                }
            }
}