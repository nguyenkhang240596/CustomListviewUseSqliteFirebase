package edu.ptit.kalis.customlistviewusesqlitefirebase.activity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import edu.ptit.kalis.customlistviewusesqlitefirebase.R;
import edu.ptit.kalis.customlistviewusesqlitefirebase.adapter.MyAdapter;
import edu.ptit.kalis.customlistviewusesqlitefirebase.model.FakeObject;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "FireBase";
    private static final String tableName = "App";
    private ArrayList<FakeObject> datas = new ArrayList<>();
    private MyAdapter mApdater = null;
    private ListView mListView;
    private SQLiteDatabase sqLiteDB;
    private Button btnFireBase;
    private Button btnSqlite;
    private ProgressBar progressBar;
    private FirebaseDatabase firebaseDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //add Firebase
        firebaseDB = FirebaseDatabase.getInstance();
        sqLiteDB = openOrCreateDatabase("androidapps.s3db", MODE_PRIVATE, null);

        addControls();
        addEvents();
//        loadSqliteDatabase();
//        addFakeDatas();
//        loadFireBaseDb();

    }

    private void loadFireBaseDb() {
        DatabaseReference myRef = firebaseDB.getReference("App");
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot child: dataSnapshot.getChildren()) {
                    FakeObject obj = child.getValue(FakeObject.class);
                    obj.setImgBytes(Base64.decode(obj.getImgBase64(),Base64.DEFAULT));
                    datas.add(obj);
                }

                mApdater.notifyDataSetChanged();
                mListView.setAlpha(1f);
                progressBar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
////        save data from sqlite to Firebase
//        if (datas.size() < 1) {
//            loadSqliteDatabase();
//            for(FakeObject o : datas) {
//                String key = myRef.child("_id").push().getKey();
////                myRef.child(key).child("Name").setValue(o.getName());
////                myRef.child(key).child("Owner").setValue(o.getOwner());
////                myRef.child(key).child("Rating").setValue(o.getRating());
//
//                String imageFile = Base64.encodeToString(o.getImgBytes(), Base64.DEFAULT);
//                myRef.child(key).setValue(new FakeObject(o.getName() + " - from FireBase", o.getOwner(), o.getRating(), imageFile));
//            }
//        }
    }
    private void loadSqliteDatabase() {
//        database = openOrCreateDatabase("androidapps.s3db", MODE_PRIVATE, null);
//        Cursor c = database.query(tableName, null, null, null, null, null, null);
//        String data = "";
//        while(c.moveToNext()) {
//            datas.add(new FakeObject(c.getString(0), c.getString(1), c.getFloat(2), c.getBlob(3)));
//        }
//        c.close();
//        addFakeDatas();
//
//        mApdater.notifyDataSetChanged();
//        mListView.setAlpha(1f);
//        progressBar.setVisibility(View.INVISIBLE);
    }

    private void addFakeDatas() {
        int size = datas.size();
        for(int i=0;i<size;i++) {
            datas.add(datas.get(i));
//            fakeDatas.add(new FakeObject("Khang", "Khang version:"+i, i%6));
        }
    }

    private void addControls() {
        progressBar = findViewById(R.id.progressBar);
        btnFireBase = findViewById(R.id.btnLoadFireBase);
        btnSqlite = findViewById(R.id.btnLoadSqlite);
        mListView = findViewById(R.id.mListView);
        mApdater = new MyAdapter(this, R.layout.my_custom_layout, datas);
        mListView.setAdapter(mApdater);

    }

    private void addEvents() {
        btnSqlite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                mListView.setAlpha(0f);
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        SqliteLoadTask _task = new SqliteLoadTask(mApdater);
                        _task.execute();
                    }
                }, 2000);
//                progressBar.setVisibility(View.VISIBLE);
//                mApdater.clear();
//                mListView.setAlpha(0f);
//                Handler handler
// = new Handler();
//                handler.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        loadSqliteDatabase();
//                    }
//                }, 2000);
            }
        });
        btnFireBase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                mApdater.clear();
                mListView.setAlpha(0f);
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        loadFireBaseDb();
                    }
                }, 2000);
            }
        });
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String text = datas.get(position).toString();
                Log.d(TAG, text);
                Toast.makeText(getBaseContext(), text,Toast.LENGTH_LONG);
            }
        });
        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                String text = datas.get(position).toString();
                Log.d(TAG, text);
                Toast.makeText(getBaseContext(), text,Toast.LENGTH_LONG);
                return false;
            }
        });
    }

    private class SqliteLoadTask extends AsyncTask<Void, Void, ArrayList<FakeObject>> {

        MyAdapter _adapter = null;
        public SqliteLoadTask(MyAdapter adapter) {
            _adapter = adapter;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            _adapter.clear();
        }

        @Override
        protected ArrayList<FakeObject> doInBackground(Void... params) {
            ArrayList<FakeObject> data = new ArrayList<>();

            Cursor c = sqLiteDB.query(tableName, null, null, null, null, null, null);
            while(c.moveToNext()) {
                data.add(new FakeObject(c.getString(0), c.getString(1), c.getFloat(2), c.getBlob(3)));
            }

            int size = data.size();
            for(int i=0;i< size;i++) {
                data.add(data.get(i));
            }
            c.close();
            return data;
        }


        @Override
        protected void onPostExecute(ArrayList<FakeObject> data) {
            _adapter.addAll(data);
            _adapter.notifyDataSetChanged();
            progressBar.setVisibility(View.INVISIBLE);
            mListView.setAlpha(1f);
        }

    }
}
