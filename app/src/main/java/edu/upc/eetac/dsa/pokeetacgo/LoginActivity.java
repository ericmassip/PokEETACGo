package edu.upc.eetac.dsa.pokeetacgo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.loopj.android.http.*;

import cz.msebera.android.httpclient.Header;
import edu.upc.eetac.dsa.pokeetacgo.entity.User;
import edu.upc.eetac.dsa.pokeetacgo.entity.serviceLibraryResults.AuthenticationResult;
import edu.upc.eetac.dsa.pokeetacgo.serviceLibrary.PokEETACRestClient;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG="LOGIN";
    PokEETACGo pokEETACGo;
    TextView username;
    TextView password;
    Button registerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        pokEETACGo = PokEETACGo.getInstance();

        username = (TextView) findViewById(R.id.username);
        password = (TextView) findViewById(R.id.password);

        registerButton = (Button) findViewById(R.id.registerButton);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent register = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(register);
            }
        });
    }

    public void login(View view) {
        final User user = new User();
        user.setUsername(username.getText().toString());
        user.setPassword(password.getText().toString());

        PokEETACRestClient.post(this, "/user/login", PokEETACRestClient.getObjectAsStringEntity(user), "application/json", new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.e(TAG, "Error logging in from hello");
                Toast.makeText(getApplicationContext(), "Error logging in. Please try again!", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                Log.i(TAG, "Success logging in: " + responseString);
                AuthenticationResult authenticationResult = new Gson().fromJson(responseString, AuthenticationResult.class);
                Log.i(TAG, "Login is successful: " + authenticationResult.isSuccessful);
                if (authenticationResult.isSuccessful) {
                    pokEETACGo.setCurrentUserId(authenticationResult.userId);
                    Toast.makeText(getApplicationContext(), "Welcome " + user.getUsername() + "!", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Username or password incorrect. Please try again!", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
