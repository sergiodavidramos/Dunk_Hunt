package com.example.duckhunt.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.duckhunt.R;
import com.example.duckhunt.common.Constantes;
import com.example.duckhunt.models.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class LoginActivity extends AppCompatActivity {
    EditText etNick;
    Button btnStart;
    String nick;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Instanciar la coneccion a firestore
        db = FirebaseFirestore.getInstance();

        etNick = findViewById(R.id.editTextNick);
        btnStart = findViewById(R.id.buttonStart);

        // Cambiar tipo de fuente
        Typeface typeface = Typeface.createFromAsset(getAssets(), "pixel.ttf");
        etNick.setTypeface(typeface);
        btnStart.setTypeface(typeface);

        //Eventos: evento Click
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nick = etNick.getText().toString();

                if(nick.isEmpty()){
                    etNick.setError("el nombre de usuario es obligatorio");
                } else if(nick.length()<3) {
                    etNick.setError("Debe tener almenos 3 caracteres");
                } else {
                    addNickAndStart();
                }
            }
        });
    }

    private void addNickAndStart() {
        db.collection("users").whereEqualTo("nick", nick)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if(queryDocumentSnapshots.size()>0){
                            etNick.setError("El nick no esta disponible");
                        }else {
                            addNickToFirestore();
                        }
                    }
                });
    }

    private void addNickToFirestore() {
        User nuevoUsuario = new User(nick,0);
        db.collection("users").add(nuevoUsuario)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        etNick.setText("");
                        Intent i = new Intent(LoginActivity.this, GameActivity.class);
                        i.putExtra(Constantes.EXTRA_NICK, nick);

                        i.putExtra(Constantes.EXTRA_ID, documentReference.getId());

                        startActivity(i);
                    }
                });
    }
}
