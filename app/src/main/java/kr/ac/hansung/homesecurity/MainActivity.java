package kr.ac.hansung.homesecurity;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
/**
 * Created by sky on 2017-11-21.
 */

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private final String TAG = MainActivity.class.getSimpleName();

    public static final int REQUEST_EXTERNAL_STORAGE = 1001;

    ImageButton b_level;
    ImageButton b_record;
    ImageButton b_live;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        b_level = findViewById(R.id.level);
        b_record = findViewById(R.id.record);
        b_live = findViewById(R.id.live);

        b_level.setOnClickListener(this);
        b_record.setOnClickListener(this);
        b_live.setOnClickListener(this);
        findViewById(R.id.l_level).setOnClickListener(this);
        findViewById(R.id.l_record).setOnClickListener(this);
        findViewById(R.id.l_live).setOnClickListener(this);

        requestPermission();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.l_level || id == R.id.level) {
            updateBtnUI(0);
            updateFragment(0);
        } else if (id == R.id.l_record || id == R.id.record) {
            updateBtnUI(1);
            updateFragment(1);
        } else if (id == R.id.l_live || id == R.id.live) {
            updateBtnUI(2);
            updateFragment(2);
        }
    }

    public void updateBtnUI(int btnNum) {
        switch (btnNum) {
            case 0:
                b_level.setImageResource(R.drawable.ic_level_on);
                b_record.setImageResource(R.drawable.ic_record_off);
                b_live.setImageResource(R.drawable.ic_cam_off);
                break;
            case 1:
                b_level.setImageResource(R.drawable.ic_level_off);
                b_record.setImageResource(R.drawable.ic_record_on);
                b_live.setImageResource(R.drawable.ic_cam_off);
                break;
            case 2:
                b_level.setImageResource(R.drawable.ic_level_off);
                b_record.setImageResource(R.drawable.ic_record_off);
                b_live.setImageResource(R.drawable.ic_cam_on);
                break;
        }
    }

    public void updateFragment(int number) {
        Fragment fragment = null;
        switch (number) {
            case 0:
                fragment = new SecurityFragment();
                break;
            case 1:
                fragment = new RecordsFragment();
                break;
            case 2:
                fragment = new LiveFragment();
                break;
        }
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.container, fragment).commit();
    }

    private void requestPermission() {
        String[] PERMISSIONS_STORAGE = { android.Manifest.permission.READ_EXTERNAL_STORAGE,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE};
        int permission = ActivityCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_EXTERNAL_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                updateBtnUI(0);
                updateFragment(0);
                Log.d(TAG, "permission result:success");
            }
        }
    }

}