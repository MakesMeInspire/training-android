package com.quick.trainingandroid;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.quick.trainingandroid.Adapter.RVApekerja;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import static com.quick.trainingandroid.Config.Link.baseUrl;

public class MainActivity extends AppCompatActivity {

    SwipeRefreshLayout srl_home;

    RecyclerView rv_pekerja;
    RecyclerView.LayoutManager rlm_pekerja;
    RVApekerja adapterPekerja;

    ArrayList<String> list_id, list_nama, list_kelamin, list_alamat, list_hobi, list_trans;
    ProgressDialog progressDialog;
    AlertDialog dialog;

    String hasilQuery="0";

    JSONArray jsonArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        rv_pekerja = (RecyclerView) findViewById(R.id.rv_pekerja);
        srl_home = (SwipeRefreshLayout) findViewById(R.id.srl_home);
        progressDialog = new ProgressDialog(MainActivity.this);

        rv_pekerja.hasFixedSize();
        rlm_pekerja = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        rv_pekerja.setLayoutManager(rlm_pekerja);

        initArray();
        srl_home.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                srl_home.setRefreshing(false);
                initArray();
            }
        });
    }

    void initArray(){
        list_id = new ArrayList<String>();
        list_nama = new ArrayList<String>();
        list_kelamin = new ArrayList<String>();
        list_alamat = new ArrayList<String>();
        list_hobi = new ArrayList<String>();
        list_trans = new ArrayList<String>();

        //hapus isi array
        list_id.clear();
        list_nama.clear();
        list_kelamin.clear();
        list_alamat.clear();
        list_hobi.clear();
        list_trans.clear();

        //get data pekerja
        getPekerja();
    }

    void getPekerja(){
        class dapatkanPekerja extends AsyncTask<String, Void, String>{
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressDialog.setMessage("Loading Data Pekerja...");
                progressDialog.setCancelable(false);
                progressDialog.show();
            }

            @Override
            protected String doInBackground(String... strings) {
                hasilQuery="0";

                InputStream is=null;
                String line;

                try{
                    HttpClient httpClient = new DefaultHttpClient();
                    HttpGet httpGet = new HttpGet(baseUrl+"show.php");
                    HttpResponse response = httpClient.execute(httpGet);
                    HttpEntity entity = response.getEntity();
                    is = entity.getContent();
                }catch (IOException e){
                    e.printStackTrace();
                }

                try{
                    BufferedReader reader = new BufferedReader(new InputStreamReader(is,"iso-8859-1"),8);
                    StringBuilder sb = new StringBuilder();
                    while ((line = reader.readLine()) != null){
                        sb.append(line);
                    }
                    if (is!=null){
                        is.close();
                        hasilQuery=sb.toString();
                    }else {
                        Log.e("ERROR","INPUTSTREAM ERROR");
                    }

                }catch (Exception e){
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
                        if(s.contains("pg_connect()")){
                            dialog = new AlertDialog.Builder(MainActivity.this)
                                    .setTitle("Perhatian !!!")
                                    .setMessage("Server Sedang Maintenance")
                                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                            MainActivity.this.finish();
                                        }
                                    })
                                    .show();
                        }else {
                            if (s.matches("0")){
                                dialog = new AlertDialog.Builder(MainActivity.this)
                                        .setTitle("Perhatian !!!")
                                        .setMessage("Cek Koneksi Anda")
                                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                                MainActivity.this.finish();
                                            }
                                        })
                                        .show();
                            }else {
                                prosesDataPekerja(s);
                            }
                        }
                    }
                },3000);
            }
        }
        dapatkanPekerja g = new dapatkanPekerja();
        g.execute();
    }

    void prosesDataPekerja(String data){
        try{
            JSONObject jsonObject = new JSONObject(data);
            Log.d("RESULT STATUS"," => "+jsonObject.getBoolean("status"));
            Boolean status = jsonObject.getBoolean("status");
            if (status){
                jsonArray = jsonObject.getJSONArray("result");

                for (int a=0;a<jsonArray.length();a++){
                    Log.d("PERULANGAN KE-",""+a+" dari : "+jsonArray.length());
                    JSONObject c = jsonArray.getJSONObject(a);

                    list_id.add(c.getString("id"));
                    list_nama.add(c.getString("nama"));
                    list_kelamin.add(c.getString("jenis_kelamin"));
                    list_alamat.add(c.getString("alamat"));
                    list_hobi.add(c.getString("hobi"));
                    list_trans.add(c.getString("transportasi"));

                    if (a==jsonArray.length()-1){
                        adapterPekerja = new RVApekerja(this,list_id,list_nama,list_kelamin,list_alamat,list_hobi,list_trans);
                        rv_pekerja.setAdapter(adapterPekerja);
                    }
                }
            }else {
                Log.d("DATA","=>"+jsonObject.getJSONArray("result").getString(0));

                adapterPekerja = new RVApekerja(this,list_id,list_nama,list_kelamin,list_alamat,list_hobi,list_trans);
                rv_pekerja.setAdapter(adapterPekerja);

                dialog = new AlertDialog.Builder(this)
                        .setTitle("Perhatian !!!")
                        .setMessage(""+jsonObject.getJSONArray("result").getString(0))
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .show();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menuapp, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id== R.id.add){
            Intent a = new Intent(MainActivity.this,TambahPekerja.class);
            startActivityForResult(a,777);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==777){
            initArray();
        }
    }
}
