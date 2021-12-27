package com.example.topics.Utilidades;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.topics.Modelo.Post;
import com.example.topics.Modelo.User;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Utilitarios extends AppCompatActivity {

    private Api api;

    private PreferenceManager preferenceManager;

    public Utilitarios(Context context) {
        this.preferenceManager = new PreferenceManager(context);
        this.api = new Api();
    }

    public Utilitarios() {
    }

    public static final String VALID_EMAIL_ADDRESS_REGEX = "^(.+)@(.+)$";

    public static final String VALID_DATE = "^(\\d\\d)\\s/\\s(\\d\\d)\\s/\\s(\\d\\d\\d\\d)$";

    public static boolean validate(String emailStr) {
        return emailStr.matches(VALID_EMAIL_ADDRESS_REGEX);
    }

    public String obtenerDato(String key){
        SharedPreferences preferences = getSharedPreferences("MisPreferencias", Context.MODE_PRIVATE);
        return preferences.getString(key,"");
    }



    public static boolean validateDate(String date){
        return date.matches(VALID_DATE);
    }

    public boolean isVacioEditableEmail(Editable editText, String campo, TextView textView){
        textView.setText("");
        if (editText.toString().equals("") || editText.toString() == null || editText.length() <= 2){
            textView.setText("El formato del email es incorrecto");
            textView.setVisibility(View.VISIBLE);
            textView.setTextColor(Color.RED);
            return false;
        }

        return true;
    }
    public boolean isVacioEditable(Editable editText, String campo, TextView textView){
        textView.setText("");
        if (editText.toString().equals("") || editText.toString() == null || editText.length() < 1){
            textView.setText("El campo "+campo+" está vacio");
            textView.setVisibility(View.VISIBLE);
            textView.setTextColor(Color.RED);
            return false;
        }

        return true;
    }
    public boolean isVacioEditText(EditText editText, TextView textView){
        textView.setText("");
        if (editText.getText().equals("") || editText.getText() == null || editText.getText().length() < 1 ){
            textView.setText("El campo "+editText.getHint().toString()+" está vacio");
            textView.setVisibility(View.VISIBLE);
            textView.setTextColor(Color.RED);
            return false;
        }

        return true;
    }

    public boolean isVisible(EditText editText){
        return editText.getVisibility() == View.VISIBLE;
    }

    public   boolean checkString(String str, String passwd, int num, TextView textView) {
        StringBuilder error = new StringBuilder();
        char ch;
        boolean capitalFlag = false;
        boolean lowerCaseFlag = false;
        boolean numberFlag = false;
        boolean coinciden = true;
        for(int i=0;i < str.length();i++) {
            ch = str.charAt(i);
            if(Character.isDigit(ch)) {
                numberFlag = true;
            }
            if (Character.isUpperCase(ch)) {
                capitalFlag = true;
            }
            if (Character.isLowerCase(ch)) {
                lowerCaseFlag = true;
            }
        }
        if (num == 0){
            if (!passwd.equals(str)){
                error.append("Las contraseñas no coinciden.");
                coinciden = false;
            }
        }
        if(numberFlag && capitalFlag && lowerCaseFlag && coinciden) {
            return true;
        }else {
            if (!numberFlag){
                error.append("Faltaría un dígito (0-9)");
            }
            if (!capitalFlag){
                error.append("Faltaría una mayúscula");
            }
            if (!lowerCaseFlag){
                error.append("Faltaría una minúscula");
            }
            if (!coinciden){
                textView.setVisibility(View.VISIBLE);
                textView.setTextColor(Color.RED);
                textView.setText("Las contraseñas no coinciden");
                error.setLength(0);
                return false;
            }
        }
        if (num == 1){
            textView.setTextColor(Color.RED);
            textView.setText(error);
            error.setLength(0);
            textView.setVisibility(View.VISIBLE);
        }
        return false;
    }

    public boolean validarCampo(EditText editText, TextView textView, String passwd){

        String campo = editText.getHint().toString();
        String texto = editText.getText().toString();
        switch (campo){
            case "Name":{
                textView.setText("");
                if (texto.length() > 20 || texto.length() < 5 ){
                    textView.setText("El campo nombre debe tener entre 5 y 20 caracteres");
                    textView.setVisibility(View.VISIBLE);
                    textView.setTextColor(Color.RED);
                    return false;
                }
                return true;
            }
            case "Password":{
                textView.setText("");
                if (!checkString(texto, texto,1,textView)){
                    return false;
                }
                if (texto.length() < 8){
                    textView.setTextColor(Color.RED);
                    textView.setText("Contraseña demasiado corta");
                    textView.setVisibility(View.VISIBLE);
                    return false;
                }
                return true;
            }
            case "Repeat Password":{
                textView.setText("");
                if (!checkString(texto, passwd,0,textView)){
                    return false;
                }

                return true;
            }
            case "Email":{
                textView.setText("");
                Log.d("EMAIL", editText.getText().toString());
                api.ifExistsEmailInUsersRegister(editText,textView);


            }
            case "Name User":{
                textView.setText("");


            }
        }
        return true;
    }
    public boolean comprobarDatos(EditText email, EditText password, TextView errorPassword, TextView errorName) {
        String emailText = email.getText().toString();
        String passwordText = password.getText().toString();
        if (emailText.isEmpty()) {
            Toast.makeText(this, "Campo Email vacío", Toast.LENGTH_LONG).show();
            errorName.setText("Campo Email vacío");
            errorName.setVisibility(View.VISIBLE);
            errorName.setTextColor(Color.RED);
            return false;
        } else if (passwordText.isEmpty()) {
            Toast.makeText(this, "Campo Contraseña vacío", Toast.LENGTH_LONG).show();
            errorPassword.setText("Campo Contraseña vacío");
            errorPassword.setVisibility(View.VISIBLE);
            errorPassword.setTextColor(Color.RED);
            return false;
        }
        return true;
    }

    public HashMap<String ,Object> conversionUsuarioToHashMapInhabilitados(User u){
        HashMap<String, Object> usuario = new HashMap<>();
        usuario.put(Constants.KEY_ID_USER, u.getId());
        usuario.put(Constants.KEY_EMAIL,u.getEmail().toLowerCase(Locale.ROOT));
        usuario.put(Constants.KEY_PASSWORD,u.getPassword());
        usuario.put(Constants.KEY_DATE,u.getFecha());
        usuario.put(Constants.KEY_NAME,u.getNombre());
        usuario.put(Constants.KEY_NAME_USER,u.getNombreCuenta());
        usuario.put(Constants.KEY_HABILITADO,false);
        usuario.put(Constants.KEY_EMAIL_VERIFIED, false);
        return usuario;
    }

    public HashMap<String,Object> userToHashMapUsers(User u){
        HashMap<String, Object> usuario = new HashMap<>();
        usuario.put(Constants.KEY_ID_USER, u.getId());
        usuario.put(Constants.KEY_NAME_USER, u.getNombreCuenta());
        usuario.put(Constants.KEY_NAME, u.getNombre());
        usuario.put(Constants.KEY_HABILITADO, true);
        usuario.put(Constants.KEY_PHOTO_PERFIL, u.getUrlPerfil());
        usuario.put(Constants.KEY_EMAIL, u.getEmail());

        usuario.put(Constants.KEY_SEGUIDORES,0);
        usuario.put(Constants.KEY_SEGUIDOS,0);
        usuario.put(Constants.KEY_PRICE, 0);
        usuario.put(Constants.KEY_DESCARGABLE, false);
        return usuario;
    }

    public HashMap<String, Object> createMapfromPost(Post post){
        HashMap<String,Object> map = new HashMap<>();
        map.put(Constants.KEY_DESCARGABLE, post.isDescargable());
        map.put(Constants.KEY_DESCRIPCION,post.getDescripcion());
        map.put(Constants.KEY_PRICE,post.getPrecio());
        map.put(Constants.KEY_ID_CUENTA, preferenceManager.getString(Constants.KEY_ID_USER));
        map.put(Constants.KEY_DATE, new Date());
        return map;
    }


}
