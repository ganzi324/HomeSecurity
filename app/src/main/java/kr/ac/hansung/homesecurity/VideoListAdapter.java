package kr.ac.hansung.homesecurity;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by sky on 2017-11-07.
 */

public class VideoListAdapter extends RecyclerView.Adapter<VideoListAdapter.ViewHolder>  {

    private static ItemClickListener itemClickListener;

    public interface ItemClickListener {
        void onItemClick(int position, View v);
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    private Context mContext;
    private List<Video> videos;

    public VideoListAdapter(Context context, List<Video> videos) {
        mContext = context;
        this.videos = videos;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private Context context;

        public TextView txt_title;
        public TextView txt_created;
        public TextView txt_length;
        public TextView txt_fileSize;

        public ViewHolder(Context context, View itemView) {
            super(itemView);

            this.context = context;

            txt_title = itemView.findViewById(R.id.title);
            txt_created = itemView.findViewById(R.id.created_date);
            txt_length = itemView.findViewById(R.id.video_length);
            txt_fileSize = itemView.findViewById(R.id.file_size);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            itemClickListener.onItemClick(getLayoutPosition(), view);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater layoutInflater = LayoutInflater.from(context);

        View view = layoutInflater.inflate(R.layout.item_record, parent,false);
        ViewHolder viewHolder = new ViewHolder(context, view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Video video = videos.get(position);
        holder.txt_title.setText(video.getFilename());
        holder.txt_created.setText("생성날짜 : " + getDate(video.getDate()));
        holder.txt_length.setText("길이 : " + video.getDuration() + "초");
        holder.txt_fileSize.setText("파일크기 : " + video.getFilesize() + "MB");
    }

    @Override
    public int getItemCount() {
        return videos.size();
    }

    public void removeItem(int position) {
        videos.remove(position);
        notifyItemRemoved(position);
    }

    private String getDate(String strDate) {

        SimpleDateFormat oldDateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
        SimpleDateFormat newDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date date = oldDateFormat.parse(strDate);
            return newDateFormat.format(date);
        } catch (ParseException e) {
            return "생성날짜 알 수 없음";
        }

    }

}
