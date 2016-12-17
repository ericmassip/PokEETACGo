package edu.upc.eetac.dsa.pokeetacgo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.loopj.android.http.TextHttpResponseHandler;

import cz.msebera.android.httpclient.Header;
import edu.upc.eetac.dsa.pokeetacgo.entity.User;
import edu.upc.eetac.dsa.pokeetacgo.entity.serviceLibraryResults.AuthenticationResult;
import edu.upc.eetac.dsa.pokeetacgo.serviceLibrary.PokEETACRestClient;

public class RegisterActivity extends AppCompatActivity {
    private static final String TAG = "REGISTER";
    EditText username;
    EditText password;
    EditText email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        username = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);
        email = (EditText) findViewById(R.id.email);
    }

    public void register(View view) {
        final User user = new User();
        user.setUsername(username.getText().toString());
        user.setPassword(password.getText().toString());
        user.setEmail(email.getText().toString());

        if (isEmpty(username) || isEmpty(password) || isEmpty(email)) {
            Toast.makeText(getApplicationContext(), "Fill in all the fields, please.", Toast.LENGTH_SHORT).show();
        } else {
            PokEETACRestClient.post(this, "/user/register", PokEETACRestClient.getObjectAsStringEntity(user), "application/json", new TextHttpResponseHandler() {
                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    Log.e(TAG, "Error registering from hello");
                    Toast.makeText(getApplicationContext(), "Error registering. Please try again!", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, String responseString) {
                    Log.i(TAG, "Success registering: " + responseString);
                    AuthenticationResult authenticationResult = new Gson().fromJson(responseString, AuthenticationResult.class);
                    Log.i(TAG, "Register is successful: " + authenticationResult.isSuccessful);
                    if (authenticationResult.isSuccessful) {
                        Toast.makeText(getApplicationContext(), "Welcome " + user.getUsername() + "!", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "Username or email already exists. Please try again!", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }

    private boolean isEmpty(EditText editText) {
        return editText.getText().toString().isEmpty();
    }
}
