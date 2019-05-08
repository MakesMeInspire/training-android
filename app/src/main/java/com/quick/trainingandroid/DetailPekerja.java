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

import com.quick.trainingandroid.Adapter.RVApekerja;

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

public class DetailPekerja extends AppCompatActivity {

    EditText et_nama, et_alamat, et_hobi;
    RadioGroup rg_kelamin;
    RadioButton rb_laki, rb_perempuan;
    Spinner sp_trans;
    Button bt_delete, bt_update;
    ProgressDialog progressDialog;
    AlertDialog dialog;
    String id="", nama="", alamat="", kelamin="", hobi="", trans="", hasilQuery="0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        initLayout();
        getBundle();

        bt_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog = new AlertDialog.Builder(DetailPekerja.this)
                        .setTitle("Perhatian !!!")
                        .setMessage("Ingin menghapus data pekerja atas nama "+nama+" ?")
                        .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                if (!id.equals("")){
                                    deletePekerja(id);
                                }
                            }
                        })
                        .setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .show();
            }
        });

        bt_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog = new AlertDialog.Builder(DetailPekerja.this)
                        .setTitle("Perhatian !!!")
                        .setMessage("Ingin mengupdate data atas nama "+nama+" ?")
                        .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                if (!id.equals("")){
                                    cekUpdateData(id);
                                }
                            }
                        })
                        .setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .show();
            }
        });
    }

    void initLayout(){
        progressDialog = new ProgressDialog(DetailPekerja.this);
        et_nama = (EditText)findViewById(R.id.et_nama);
        et_alamat = (EditText)findViewById(R.id.et_alamat);
        et_hobi = (EditText)findViewById(R.id.et_hobi);
        rg_kelamin = (RadioGroup)findViewById(R.id.rg_kelamin);
        rb_laki = (RadioButton) findViewById(R.id.rb_laki);
        rb_perempuan = (RadioButton) findViewById(R.id.rb_perempuan);
        sp_trans = (Spinner) findViewById(R.id.sp_trans);
        bt_delete = (Button) findViewById(R.id.bt_delete);
        bt_update = (Button) findViewById(R.id.bt_update);
    }

    void getBundle(){
        Bundle c = this.getIntent().getExtras();
        if (c!=null){
            id = c.getString("id");
            nama = c.getString("nama");
            trans = c.getString("trans");
            et_nama.setText(c.getString("nama"));
            et_alamat.setText(c.getString("alamat"));
            et_hobi.setText(c.getString("hobi"));

            if (c.getString("kelamin").equalsIgnoreCase("l")){
                rb_laki.setChecked(true);
            }else {
                rb_perempuan.setChecked(true);
            }

            if (trans.equalsIgnoreCase("motor")){
                sp_trans.setSelection(0);
            }else if(trans.equalsIgnoreCase("mobil")){
                sp_trans.setSelection(1);
            }else if(trans.equalsIgnoreCase("kendaraan umum")){
                sp_trans.setSelection(2);
            }else if(trans.equalsIgnoreCase("ojek")){
                sp_trans.setSelection(3);
            }else {
                sp_trans.setSelection(4);
            }
        }
    }

    void deletePekerja(final String idPekerja){
        class delPekerja extends AsyncTask<String,Void,String>{
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressDialog.setCancelable(false);
                progressDialog.setMessage("Menghapus Data Pekerja");
                progressDialog.show();
            }

            @Override
            protected String doInBackground(String... strings) {
                hasilQuery="0";

                ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("id", idPekerja));

                InputStream is = null;
                String line;

                try {
                    HttpClient httpclient = new DefaultHttpClient();
                    HttpPost httppost = new HttpPost(baseUrl+"delete.php");
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
                Log.d("HASIL QUERY",s);
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
        delPekerja g = new delPekerja();
        g.execute();
    }

    void cekUpdateData(String idPekerja){
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
        Log.d("DATA","=>"+id+", "+nama+", "+kelamin+", "+alamat+", "+hobi+", "+trans);
        if (!nama.equals("")&&!alamat.equals("")&&!kelamin.equals("")&&!hobi.equals("")&&!trans.equals("")){
            updatePekerja(idPekerja);
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

    void updatePekerja(final String idPekerja){
        class update extends AsyncTask<String, Void, String>{
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressDialog.setCancelable(false);
                progressDialog.setMessage("Mengupdate Data Pekerja");
                progressDialog.show();
            }

            @Override
            protected String doInBackground(String... strings) {
                hasilQuery="0";

                ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("id", idPekerja));
                nameValuePairs.add(new BasicNameValuePair("nama", nama));
                nameValuePairs.add(new BasicNameValuePair("jenis_kelamin", kelamin));
                nameValuePairs.add(new BasicNameValuePair("alamat", alamat));
                nameValuePairs.add(new BasicNameValuePair("hobi", hobi));
                nameValuePairs.add(new BasicNameValuePair("transportasi", trans));

                InputStream is = null;
                String line;

                try {
                    HttpClient httpclient = new DefaultHttpClient();
                    HttpPost httppost = new HttpPost(baseUrl+"edit.php");
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
        update g = new update();
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
                            DetailPekerja.this.finish();
                        }
                    })
                    .show();

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    void dialogError(){
        dialog = new AlertDialog.Builder(DetailPekerja.this)
                .setTitle("Perhatian !!!")
                .setMessage("Periksa Koneksi Anda")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        DetailPekerja.this.finish();
                    }
                })
                .show();
    }

    void dialogServerError(){
        dialog = new AlertDialog.Builder(DetailPekerja.this)
                .setTitle("Perhatian !!!")
                .setMessage("Server Sedang Maintenance")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        DetailPekerja.this.finish();
                    }
                })
                .show();
    }
}
