package kr.ac.hansung.homesecurity;


import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class RecordsFragment extends Fragment
        implements VideoListAdapter.ItemClickListener, View.OnClickListener{

    private final String TAG = RecordsFragment.class.getSimpleName();

    VideoView videoView;
    ProgressBar progressBar;
    ImageButton btn_refresh;

    FirebaseAuth mAuth;

    private VideoListAdapter mAdapter;
    private List<Video> videos;

    Activity activity;

    private boolean initialized = false;

    public RecordsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_records, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        activity = getActivity();

        btn_refresh = activity.findViewById(R.id.refresh);
        btn_refresh.setOnClickListener(this);
        progressBar = getView().findViewById(R.id.progress);
        videoView = getView().findViewById(R.id.videoView);
        MediaController mediaController = new MediaController(activity);
        mediaController.setAnchorView(videoView);
        videoView.setMediaController(mediaController);

        videos = new ArrayList<>();
        mAuth = FirebaseAuth.getInstance();

        loadItems();
    }

    private void initialize() {
        mAdapter = new VideoListAdapter(activity, videos);
        mAdapter.setItemClickListener(this);

        RecyclerView recyclerView = getView().findViewById(R.id.listView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(mAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(activity));
        recyclerView.addItemDecoration(new DividerItemDecoration(activity,
                new LinearLayoutManager(activity).getOrientation()));

        initialized = true;
    }

    public void loadItems() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            Toast.makeText(activity, R.string.request_auth, Toast.LENGTH_SHORT).show();
            return;
        }
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        database.getReference("/data/video")
                .addListenerForSingleValueEvent(postListener);
        btn_refresh.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
    }

    ValueEventListener postListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            Log.d(TAG, "data changed");
            videos.clear();
            for (DataSnapshot itemSnapshot : dataSnapshot.getChildren()) {
                Video video = itemSnapshot.getValue(Video.class);
                videos.add(video);
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

    @Override
    public void onItemClick(int position, View v) {
        Video video = videos.get(position);

        //startVideo(Uri.parse(video.getFileUrl()));
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference ref = storage.getReference("/records/" + video.getFilename());
        ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                startVideo(uri);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG, "get uri from firebase is failed:", e);
            }
        });
    }

    private void startVideo(Uri uri) {
        Log.d(TAG, "video player start");
        videoView.setVisibility(View.VISIBLE);

        try {
            videoView.setVideoURI(uri);
            videoView.requestFocus();
            videoView.start();
        } catch (Exception e) {
            Log.w(TAG, "video player start:failed", e);
        }
    }

    private void updateUI(boolean isLoading) {

    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.refresh) {
            loadItems();
        }
    }

    /*private File getLocalFile(String title) {
        String dir = Environment.getExternalStorageDirectory().getAbsolutePath();
        File filePath = new File(dir);
        File[] fileList = filePath.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.getName().toLowerCase(Locale.US).endsWith(".jpg");
            }
        });

        if (fileList == null || fileList.length == 0) return null;

        for (File file : fileList) {
            if (file.getName().equals(title))
                return file.getAbsoluteFile();
        }
        return null;
    }*/

    /*private void downloadVideo(final String filename) {
        String dir = Environment.getExternalStorageDirectory().getAbsolutePath();
        File filepath = new File(dir);
        if (!filepath.exists()) {
            filepath.mkdir();
            Log.d(TAG, "make file directory");
        }
        // Create a reference to a file from a Google Cloud Storage URI
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        StorageReference pathReference = storageRef.child("records/Desert.jpg");

        StorageReference referenceFromUrl = storage.getReferenceFromUrl("gs://homesecurity-a3f0a.appspot.com/records/Desert.jpg");

        File file = new File(dir, "Desert.jgp");
        FileDownloadTask task = referenceFromUrl.getFile(file);

        task.addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                Log.i(TAG, "File Download Success");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
                Log.i(TAG, "Download failed:" + exception.getMessage());
            }
        });

        try {
            localFile = File.createTempFile("images", ".jpg");
        } catch (Exception e) {
            Log.w(TAG, "create file failed:", e);
        }
        pathReference.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                // Local temp file has been created
                //moveFile(localFile, filename);
                Log.i(TAG, "File downloaded:" + taskSnapshot.getBytesTransferred());
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
                Log.i(TAG, "Download failed:" + exception.getMessage());
            }
        });

    }*/

    /*public void moveFile(File localFile, String filename) {
        InputStream inStream;
        OutputStream outStream;

        try {
            String dir = Environment.getExternalStorageDirectory().getAbsolutePath();
            File filepath = new File(dir);
            if (!filepath.exists())
                filepath.mkdir();

            File file = new File(filepath + "Desert.jpg");

            inStream = new FileInputStream(localFile);
            outStream = new FileOutputStream(file);

            byte[] buffer = new byte[1024];

            int length;
            //copy the file content in bytes
            while ((length = inStream.read(buffer)) > 0) {
                outStream.write(buffer, 0, length);
            }

            inStream.close();
            outStream.close();
        } catch (IOException e) {
            Log.d(TAG, "exception occured:", e);
        }
    }*/

    /*public void saveCurrentItems() {
        FirebaseUser user = mAuth.getCurrentUser();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        if (user == null) {
            Toast.makeText(this, R.string.request_auth, Toast.LENGTH_SHORT).show();
            return;
        }
        String userId = user.getUid();
        final Map<String, Object> childUpdates = new HashMap<>();
        DatabaseReference ref = database.getReference(userId + "/items");
        for (Video video : videos) {
            String id = ref.push().getKey();
            video.setKey(id);
            childUpdates.put(id, video);
        }
        ref.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                mutableData.setValue(null);
                mutableData.setValue(childUpdates);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {

            }
        });
    }*/
}
