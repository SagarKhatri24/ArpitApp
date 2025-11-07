package arpit.app;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

public class ProfileActivity extends AppCompatActivity {

    Button editProfile, submit;
    EditText name, email, contact, password, confirmPassword;
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

    RadioButton male, female;
    RadioGroup gender;

    Spinner city;

    //String[] cityArray = {"Ahmedabad","Vadodara","Surat","Rajkot"};
    ArrayList<String> arrayList;
    SQLiteDatabase db;
    String sGender;
    String sCity = "";
    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        sp = getSharedPreferences(ConstantSp.PREF, MODE_PRIVATE);

        db = openOrCreateDatabase("Arpit.db", MODE_PRIVATE, null);

        String tableQuery = "CREATE TABLE IF NOT EXISTS USERS(USERID INTEGER PRIMARY KEY AUTOINCREMENT,NAME VARCHAR(50),EMAIL VARCHAR(50),CONTACT BIGINT(10),PASSWORD VARCHAR(20),GENDER VARCHAR(10),CITY VARCHAR(20))";
        db.execSQL(tableQuery);

        name = findViewById(R.id.profile_username);
        email = findViewById(R.id.profile_email);
        contact = findViewById(R.id.profile_contact);

        password = findViewById(R.id.profile_password);
        confirmPassword = findViewById(R.id.profile_confirm_password);

        city = findViewById(R.id.profile_city);

        arrayList = new ArrayList<>();
        arrayList.add("Ahmedabad");
        arrayList.add("Surat");
        arrayList.add("Vadodara");
        arrayList.add("Demo");
        arrayList.add("Gandhinagar");
        arrayList.add("Rajkot");

        arrayList.remove(3);
        arrayList.add(0, "Select City");

        ArrayAdapter adapter = new ArrayAdapter(ProfileActivity.this, android.R.layout.simple_list_item_1, arrayList);
        adapter.setDropDownViewResource(android.R.layout.simple_list_item_checked);
        city.setAdapter(adapter);

        city.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i != 0) {
                    sCity = arrayList.get(i);
                    Toast.makeText(ProfileActivity.this, sCity, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        male = findViewById(R.id.profile_male);
        female = findViewById(R.id.profile_female);
        gender = findViewById(R.id.profile_gender);

        gender.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(@NonNull RadioGroup radioGroup, int i) {
                RadioButton radioButton = findViewById(i);
                sGender = radioButton.getText().toString();
                Toast.makeText(ProfileActivity.this, sGender, Toast.LENGTH_SHORT).show();
            }
        });

        /*male = findViewById(R.id.profile_male);
        female = findViewById(R.id.profile_female);

        male.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(ProfileActivity.this, "Male", Toast.LENGTH_SHORT).show();
            }
        });

        female.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(ProfileActivity.this, "Female", Toast.LENGTH_SHORT).show();
            }
        });*/

        submit = findViewById(R.id.profile_submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (name.getText().toString().trim().equals("")) {
                    name.setError("Name Required");
                } else if (email.getText().toString().trim().equals("")) {
                    email.setError("Email Id Required");
                } else if (!email.getText().toString().trim().matches(emailPattern)) {
                    email.setError("Valid Email Id Required");
                } else if (contact.getText().toString().trim().equals("")) {
                    contact.setError("Contact No. Required");
                } else if (contact.getText().toString().trim().length() < 10) {
                    contact.setError("Valid Contact No. Required");
                } else if (password.getText().toString().trim().equals("")) {
                    password.setError("Password Required");
                } else if (password.getText().toString().trim().length() < 6) {
                    password.setError("Min. 6 Char Password Required");
                } else if (confirmPassword.getText().toString().trim().equals("")) {
                    confirmPassword.setError("Confirm Password Required");
                } else if (confirmPassword.getText().toString().trim().length() < 6) {
                    confirmPassword.setError("Min. 6 Char Confirm Password Required");
                } else if (!password.getText().toString().trim().matches(confirmPassword.getText().toString().trim())) {
                    confirmPassword.setError("Password Does Not Match");
                } else if (gender.getCheckedRadioButtonId() == -1) {
                    Toast.makeText(ProfileActivity.this, "Please Select Gender", Toast.LENGTH_SHORT).show();
                } else {
                    String insertQuery = "UPDATE USERS SET NAME='"+name.getText().toString()+"',EMAIL='"+email.getText().toString()+"',CONTACT='"+contact.getText().toString()+"',PASSWORD='"+password.getText().toString()+"',GENDER='"+sGender+"',CITY='"+sCity+"' WHERE USERID='"+sp.getString(ConstantSp.USERID,"")+"'";
                    db.execSQL(insertQuery);

                    Toast.makeText(ProfileActivity.this, "Profile Update Successfully", Toast.LENGTH_LONG).show();

                    sp.edit().putString(ConstantSp.NAME,name.getText().toString()).commit();
                    sp.edit().putString(ConstantSp.EMAIL,email.getText().toString()).commit();
                    sp.edit().putString(ConstantSp.CONTACT,contact.getText().toString()).commit();
                    sp.edit().putString(ConstantSp.PASSWORD,password.getText().toString()).commit();
                    sp.edit().putString(ConstantSp.GENDER,sGender).commit();
                    sp.edit().putString(ConstantSp.CITY,sCity).commit();

                    setData(false);

                }
            }
        });

        editProfile = findViewById(R.id.profile_edit);

        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setData(true);
            }
        });

        setData(false);

    }

    private void setData(boolean b) {
        name.setEnabled(b);
        email.setEnabled(b);
        contact.setEnabled(b);
        password.setEnabled(b);
        confirmPassword.setEnabled(b);
        male.setEnabled(b);
        female.setEnabled(b);
        city.setEnabled(b);

        if (b) {
            confirmPassword.setVisibility(VISIBLE);
            submit.setVisibility(VISIBLE);
            editProfile.setVisibility(GONE);
        } else {
            confirmPassword.setVisibility(GONE);
            submit.setVisibility(GONE);
            editProfile.setVisibility(VISIBLE);
        }

        name.setText(sp.getString(ConstantSp.NAME, ""));
        email.setText(sp.getString(ConstantSp.EMAIL, ""));
        contact.setText(sp.getString(ConstantSp.CONTACT, ""));
        password.setText(sp.getString(ConstantSp.PASSWORD, ""));
        confirmPassword.setText(sp.getString(ConstantSp.PASSWORD, ""));

        sGender = sp.getString(ConstantSp.GENDER, "");
        if (sGender.equals("Male")) {
            male.setChecked(true);
            female.setChecked(false);
        } else if (sGender.equals("Female")) {
            male.setChecked(false);
            female.setChecked(true);
        } else {
            male.setChecked(false);
            female.setChecked(false);
        }

        sCity = sp.getString(ConstantSp.CITY, "");
        int iCityPosition = 0;
        for (int i = 0; i < arrayList.size(); i++) {
            if (sCity.equals(arrayList.get(i))) {
                iCityPosition = i;
                break;
            }
        }
        city.setSelection(iCityPosition);

    }
}