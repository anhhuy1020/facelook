package com.example.facelook.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.facelook.R;
import com.example.facelook.data.History;

import java.util.Calendar;
import java.util.List;

public class HistoryAdaptor extends RecyclerView.Adapter<HistoryAdaptor.HistoryViewHolder> {
    List<History> mListHistory;
    Context context;

    public HistoryAdaptor(Context context, List<History> mListHistory) {
        this.context = context;
        this.mListHistory = mListHistory;
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.history_item, parent, false);
        return new HistoryViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, final int position) {
        holder.name.setText(mListHistory.get(position).getName());
        Calendar calendar = mListHistory.get(position).getDate();
        String theDate = calendar.get(Calendar.MONTH) + "/"
                + calendar.get(Calendar.DAY_OF_MONTH) + "/"
                + calendar.get(Calendar.YEAR) +" "
                + calendar.get(Calendar.HOUR) +":"
                + calendar.get(Calendar.MINUTE) +":"
                + calendar.get(Calendar.SECOND)
                ;

        holder.date.setText(theDate);
    }

    @Override
    public int getItemCount() {
        return mListHistory.size();
    }



    public class HistoryViewHolder extends RecyclerView.ViewHolder {
        TextView date;
        TextView name;

        public HistoryViewHolder(@NonNull View itemView) {
            super(itemView);
           date = itemView.findViewById(R.id.date);
           name = itemView.findViewById(R.id.name);
        }
    }
}
