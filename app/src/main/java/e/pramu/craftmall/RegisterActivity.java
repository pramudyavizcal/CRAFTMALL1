package e.pramu.craftmall;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {

    private EditText edtUsername,edtEmail,edtPassword;
    private Button btnRegister;
    private FirebaseAuth auth;
    ImageView iv;
    ProgressDialog pg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);
        initView();
        registerUser();
        toLogin();

    }
    private void registerUser() {
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //menampung imputan user
                String usernameUser = edtUsername.getText().toString().trim();
                String emailUser = edtEmail.getText().toString().trim();
                String passwordUser = edtPassword.getText().toString().trim();

                //validasi email dan password
                // jika email kosong
                if (emailUser.isEmpty()) {
                    edtEmail.setError("Email tidak boleh kosong");
                }
                if (usernameUser.isEmpty()) {
                    edtUsername.setError("Username harus diisi");
                }
                // jika email not valid
                else if (!Patterns.EMAIL_ADDRESS.matcher(emailUser).matches()) {
                    edtEmail.setError("Email tidak valid");
                }
                // jika password kosong
                else if (passwordUser.isEmpty()) {
                    edtPassword.setError("Password tidak boleh kosong");
                }
                //jika password kurang dari 6 karakter
                else if (passwordUser.length() < 6) {
                    edtPassword.setError("Password minimal terdiri dari 6 karakter");
                } else {
                    //create user dengan firebase auth
                    auth.createUserWithEmailAndPassword(emailUser, passwordUser)
                            .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    //jika gagal register do something
                                    if (!task.isSuccessful()) {
                                        Toast.makeText(RegisterActivity.this,
                                                "Register gagal karena " + task.getException().getMessage(),
                                                Toast.LENGTH_LONG).show();
                                    } else {
                                        //jika sukses akan menuju ke login activity
                                        startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                                        finish();
                                    }
                                }
                            });
                }
                pg = new ProgressDialog(RegisterActivity.this);
                pg.setMessage("Loading...");
                pg.setTitle("Harap Tunggu");
                pg.setProgressStyle(pg.STYLE_SPINNER);
                pg.show();
                pg.setCancelable(false);
                new Thread(new Runnable() {
                    public void run() {
                        try {
                            Thread.sleep(3000);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        pg.dismiss();
                    }
                }).start();
            }
        });
        }

    private void initView() {
        edtUsername = findViewById(R.id.edt_username_register);
        edtEmail = findViewById(R.id.edt_email_register);
        edtPassword = findViewById(R.id.edt_password_register);
        btnRegister = findViewById(R.id.btn_sign_up);
        iv = findViewById(R.id.imageViewRegister);
        auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser()!=null){
            startActivity(new Intent(RegisterActivity.this, homeActivity.class));
            finish();
        }
    }

    private void toLogin(){
       TextView textView = (TextView) findViewById(R.id.textUntukLogin);
        textView.setText("Sudah memiliki akun? Login disini");
// Membuat span dengan tampilan berbeda dan dapat diklik
        new PatternEditableBuilder().
                addPattern(Pattern.compile("Login"), Color.BLUE,
                        new PatternEditableBuilder.SpannableClickedListener() {
                            @Override
                            public void onSpanClicked(String text) {
                                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                            }
                        }).into(textView);
    }




    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are You Sure Want to Exit?")
                .setCancelable(true)//tidak bisa tekan tombol back
                //jika pilih yess
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        moveTaskToBack(true);
                        finish();
                    }
                })
                //jika pilih no
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                }).show();
    }
    }
