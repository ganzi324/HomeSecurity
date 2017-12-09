package kr.ac.hansung.homesecurity;


import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class SecurityFragment extends Fragment
        implements View.OnClickListener, EventListAdapter.ItemClickListener {

    private final String TAG = SecurityFragment.class.getSimpleName();
    private final String STREAM_SERVER = "http://192.168.0.11:8080/regist";

    ProgressBar progressBar;
    ImageButton btn_refresh;

    FirebaseAuth mAuth;

    private EventListAdapter mAdapter;
    private List<Event> events;
    private String idToken;

    Activity activity;

    private boolean initialized = false;

    public SecurityFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_security, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        activity = getActivity();

        btn_refresh = activity.findViewById(R.id.refresh);
        btn_refresh.setOnClickListener(this);
        activity.findViewById(R.id.send).setOnClickListener(this);

        progressBar = getView().findViewById(R.id.progress);

        events = new ArrayList<>();
        mAuth = FirebaseAuth.getInstance();

        loadItems();
    }

    public void loadItems() {
        FirebaseUser user = mAuth.getCurrentUser();
        user.getIdToken(true).addOnSuccessListener(new OnSuccessListener<GetTokenResult>() {
            @Override
            public void onSuccess(GetTokenResult getTokenResult) {
                idToken = getTokenResult.getToken();
            }
        });
        if (user == null) {
            Toast.makeText(activity, R.string.request_auth, Toast.LENGTH_SHORT).show();
            return;
        }
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        database.getReference("/data/capture")
                .addListenerForSingleValueEvent(postListener);
        btn_refresh.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
    }

    ValueEventListener postListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            Log.d(TAG, "data changed");
            events.clear();
            try {
                for (DataSnapshot itemSnapshot : dataSnapshot.getChildren()) {
                    Event event = itemSnapshot.getValue(Event.class);
                    events.add(event);
                }
            } catch (DatabaseException e) {
                Log.w(TAG, e);
            }
            if (!initialized) {
                initialize();
            }
            mAdapter.notifyDataSetChanged();
            progressBar.setVisibility(View.GONE);
            btn_refresh.setVisibility(View.VISIBLE);
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(activity, R.string.playlist_load_failed, Toast.LENGTH_SHORT).show();
            Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
        }
    };

    private void initialize() {
        mAdapter = new EventListAdapter(activity, events);
        mAdapter.setItemClickListener(this);

        RecyclerView recyclerView = getView().findViewById(R.id.listView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(mAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(activity));
        recyclerView.addItemDecoration(new DividerItemDecoration(activity,
                new LinearLayoutManager(activity).getOrientation()));

        initialized = true;
    }

    @Override
    public void onItemClick(int position, View v) {
        Toast.makeText(activity, "#" + position + "번째 item click", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.send:
                new HttpPost().execute();
                break;
            case R.id.refresh:
                loadItems();
                btn_refresh.setVisibility(View.GONE);
                break;
        }
    }

    public class HttpPost extends AsyncTask<String, Void, Void> {
        HttpURLConnection conn = null;

        @Override
        public Void doInBackground(String... params) {
            try {
                EditText e_level = activity.findViewById(R.id.level);
                String level = e_level.getText().toString().trim();

                URL obj = new URL(STREAM_SERVER);
                conn = (HttpURLConnection) obj.openConnection();
                conn.setReadTimeout(8000);
                conn.setConnectTimeout(8000);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);
                conn.setRequestProperty("Accept-Charset", "UTF-8");
                conn.setRequestProperty("Content-Type", "text/html");
                conn.setRequestProperty("Context_Type", "application/x-www-form-urlencoded;cahrset=UTF-8");

                String param = "id_token=" + idToken;
                param += "&level=" + level;

                OutputStream os = conn.getOutputStream();
                os.write(param.getBytes("UTF-8"));
                os.flush();
                os.close();

                Log.i(TAG, "level=" + level);

                if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    Log.d(TAG, "http response:" + conn.getResponseMessage());
                } else {
                    Log.w(TAG, "http response:" + conn.getResponseCode());
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (conn != null)
                    conn.disconnect();
            }
            return null;
        }

    }
}
