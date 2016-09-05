package com.gauldev.azat.batteryalarm;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    EditText _emailText;
    EditText _passwordText;
    Button _loginButton;
    ProgressDialog _loginDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        _emailText=(EditText)findViewById(R.id.input_email);
        _passwordText=(EditText)findViewById(R.id.input_password);
        _loginButton=(Button)findViewById(R.id.btn_login);
        _loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });
    }

    private void login() {
        _loginButton.setEnabled(false);
        if (!validate()){
            onLoginFailed(null);
        }
        _loginDialog = new ProgressDialog(LoginActivity.this);
        _loginDialog.setIndeterminate(true);
        _loginDialog.setMessage("Аутентификация...");
        _loginDialog.show();

        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        FirebaseAuth.getInstance().signInWithEmailAndPassword(email,password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                onLoginSuccess();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                onLoginFailed(e);
            }
        });
    }


    @Override
    public void onBackPressed() {
        // disable going back to the MainActivity
        moveTaskToBack(true);
    }

    public void onLoginSuccess() {
        if (_loginDialog!=null){
            _loginDialog.dismiss();
        }
        finish();
    }

    public void onLoginFailed(Exception e) {
        if (_loginDialog!=null){
            _loginDialog.dismiss();
        }
        Toast.makeText(getBaseContext(), "Не удалось авторизоваться", Toast.LENGTH_LONG).show();
        _loginButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        if (email.isEmpty() || !Patterns.PHONE.matcher(email).matches()) {
            _emailText.setError("введите вверный логин");
            valid = false;
        } else {
            _emailText.setError(null);
        }

        return valid;
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (_loginDialog!=null){
            _loginDialog.dismiss();
        }
    }
}