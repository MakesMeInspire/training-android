package com.quick.trainingandroid;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import static com.quick.trainingandroid.Config.Link.baseUrl;

public class TambahPekerja extends AppCompatActivity {

    EditText et_nama, et_alamat, et_hobi;
    RadioGroup rg_kelamin;
    RadioButton rb_laki, rb_perempuan;
    Spinner sp_trans;
    Button bt_tambah;
    ProgressDialog progressDialog;
    AlertDialog dialog;
    String id="", nama="", alamat="", kelamin="", hobi="", trans="", hasilQuery="0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tambah);
        initLayout();
        bt_tambah.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cekData();
            }
        });
    }

    void initLayout(){
        progressDialog = new ProgressDialog(TambahPekerja.this);
        et_nama = (EditText)findViewById(R.id.et_nama);
        et_alamat = (EditText)findViewById(R.id.et_alamat);
        et_hobi = (EditText)findViewById(R.id.et_hobi);
        rg_kelamin = (RadioGroup)findViewById(R.id.rg_kelamin);
        rb_laki = (RadioButton) findViewById(R.id.rb_laki);
        rb_perempuan = (RadioButton) findViewById(R.id.rb_perempuan);
        sp_trans = (Spinner) findViewById(R.id.sp_trans);
        bt_tambah = (Button) findViewById(R.id.bt_tambah);
    }

    void cekData(){
        nama = et_nama.getText().toString();
        alamat = et_alamat.getText().toString();
        hobi = et_hobi.getText().toString();

        int jk = rg_kelamin.getCheckedRadioButtonId();

        if (jk==R.id.rb_laki){
            kelamin="L";
        }else if (jk==R.id.rb_perempuan){
            kelamin="P";
        }else {
            kelamin="";
        }

        trans = sp_trans.getSelectedItem().toString();
        Log.d("DATA","=>"+nama+", "+kelamin+", "+alamat+", "+hobi+", "+trans);
        if (!nama.equals("")&&!alamat.equals("")&&!kelamin.equals("")&&!hobi.equals("")&&!trans.equals("")){
            tambahPekerja();
        }else {
            dialog = new AlertDialog.Builder(this)
                    .setTitle("Perhatian !!!")
                    .setMessage("data tidak boleh ada yang kosong!!!")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .show();
        }
    }

    void tambahPekerja(){
        class tambah extends AsyncTask<String, Void, String> {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressDialog.setCancelable(false);
                progressDialog.setMessage("Menambah Data Pekerja");
                progressDialog.show();
            }

            @Override
            protected String doInBackground(String... strings) {
                hasilQuery="0";

                ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("nama", nama));
                nameValuePairs.add(new BasicNameValuePair("jenis_kelamin", kelamin));
                nameValuePairs.add(new BasicNameValuePair("alamat", alamat));
                nameValuePairs.add(new BasicNameValuePair("hobi", hobi));
                nameValuePairs.add(new BasicNameValuePair("transportasi", trans));

                InputStream is = null;
                String line;

                try {
                    HttpClient httpclient = new DefaultHttpClient();
                    HttpPost httppost = new HttpPost(baseUrl+"add.php");
                    httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                    HttpResponse response = httpclient.execute(httppost);
                    HttpEntity entity = response.getEntity();
                    is = entity.getContent();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                try {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
                    StringBuilder sb = new StringBuilder();
                    while ((line = reader.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                    is.close();
                    hasilQuery = sb.toString();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return hasilQuery;
            }

            @Override
            protected void onPostExecute(final String s) {
                super.onPostExecute(s);
                Log.d("HASIL QUERY",""+s);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.dismiss();
                        if (s.contains("pg_connect()")){
                            dialogServerError();
                        }else {
                            if (s.equals("0")){
                                dialogError();
                            }else {
                                notif(s);
                            }
                        }
                    }
                },2000);
            }
        }
        tambah g = new tambah();
        g.execute();
    }

    void notif(String s){
        try{
            JSONObject jsonObject = new JSONObject(s);
            Log.d("RESULT STATUS"," => "+jsonObject.getBoolean("status"));
            Log.d("DATA","=>"+jsonObject.getJSONArray("result").getString(0));
            dialog = new AlertDialog.Builder(this)
                    .setTitle("Perhatian !!!")
                    .setMessage(""+jsonObject.getJSONArray("result").getString(0))
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            TambahPekerja.this.finish();
                        }
                    })
                    .show();

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    void dialogError(){
        dialog = new AlertDialog.Builder(TambahPekerja.this)
                .setTitle("Perhatian !!!")
                .setMessage("Periksa Koneksi Anda")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        TambahPekerja.this.finish();
                    }
                })
                .show();
    }

    void dialogServerError(){
        dialog = new AlertDialog.Builder(TambahPekerja.this)
                .setTitle("Perhatian !!!")
                .setMessage("Server Sedang Maintenance")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        TambahPekerja.this.finish();
                    }
                })
                .show();
    }
}
