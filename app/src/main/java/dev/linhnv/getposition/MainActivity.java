package dev.linhnv.getposition;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.provider.Settings.Secure;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    Button btnStart, btnStop;
    TextView txtGetposition;
    private BroadcastReceiver broadcastReceiver;
    private DatabaseReference mDatabase;
    private String android_id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        btnStart = (Button) findViewById(R.id.startService);
        btnStop = (Button) findViewById(R.id.stopService);
        txtGetposition = (TextView) findViewById(R.id.txtgetPosition);
        if(!runtime_permissions()){
            enable_button();
        }
        mDatabase = FirebaseDatabase.getInstance().getReference();
        //lay id tren may android
        android_id = Secure.getString(getApplicationContext().getContentResolver(),
                Secure.ANDROID_ID);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(broadcastReceiver == null){
            broadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    //txtGetposition.setText(intent.getExtras().get("getPosition")+"");
                    //txtGetposition.setText(intent.getExtras().get("getLatitude")+"");
                    Toast.makeText(MainActivity.this, ""+intent.getExtras().get("getPosition"), Toast.LENGTH_SHORT).show();
                    final double latitude = (double) intent.getExtras().get("getLatitude");
                    final double longtitude = (double) intent.getExtras().get("getLongtitude");
                    txtGetposition.setText(latitude +"l"+ longtitude +"");
                    Toast.makeText(MainActivity.this, "la: "+latitude + " long: " +longtitude, Toast.LENGTH_SHORT).show();
                    //writePosition(android_id, latitude, longtitude);
                    //writeNewPosition(latitude, longtitude);
                }
            };
        }
        registerReceiver(broadcastReceiver, new IntentFilter("location_update"));

    }
    private void writePosition(String id, double latitude, double longtitude) {
        Position getPosition = new Position(latitude, longtitude);
        mDatabase.child("position").child(id).setValue(getPosition);
    }
    private void writeNewPosition(final double latitude, final double longtitude){
        //update vị trí mới
        new Handler().postDelayed(new TimerTask() {
            @Override
            public void run() {
                Position position = new Position(latitude, longtitude);
                Map<String, Object> postLocaction = position.toMap();
                Map<String, Object> childUpdate = new HashMap<String, Object>();
                mDatabase.updateChildren(childUpdate);
            }
        }, 2000);
    }
    private void enable_button() {
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), GPS_Service.class);
                startService(i);
            }
        });
        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), GPS_Service.class);
                stopService(i);
            }
        });
    }

    private boolean runtime_permissions() {
        if(Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    100);
            return true;
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 100){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED){
                enable_button();
            }else{
                runtime_permissions();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(broadcastReceiver != null){
            unregisterReceiver(broadcastReceiver);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
