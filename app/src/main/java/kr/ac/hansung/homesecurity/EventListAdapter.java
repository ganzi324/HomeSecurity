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

public class EventListAdapter extends RecyclerView.Adapter<EventListAdapter.ViewHolder>  {

    private static ItemClickListener itemClickListener;

    public interface ItemClickListener {
        void onItemClick(int position, View v);
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    private Context mContext;
    private List<Event> events;

    public EventListAdapter(Context context, List<Event> events) {
        mContext = context;
        this.events = events;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private Context context;

        public TextView t_filename;
        public TextView t_date;
        public TextView t_level;
        public TextView t_type;

        public ViewHolder(Context context, View itemView) {
            super(itemView);

            this.context = context;

            t_filename = itemView.findViewById(R.id.filename);
            t_date = itemView.findViewById(R.id.date);
            t_level = itemView.findViewById(R.id.level);
            t_type = itemView.findViewById(R.id.type);

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

        View view = layoutInflater.inflate(R.layout.item_event, parent,false);
        ViewHolder viewHolder = new ViewHolder(context, view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Event event = events.get(position);
        holder.t_filename.setText(event.getFilename());
        holder.t_date.setText("생성날짜 : " + getDate(event.getDate()));
        holder.t_level.setText("등급 : " + event.getLevel());
        String type = "";
        if (event.getType() == 0) {
            type = "외부인 감지";
        } else {
            type = "동작 감지";
        }
        holder.t_type.setText("원인 : " + type);
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    public void removeItem(int position) {
        events.remove(position);
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
