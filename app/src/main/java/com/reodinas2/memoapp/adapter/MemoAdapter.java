package com.reodinas2.memoapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.reodinas2.memoapp.R;
import com.reodinas2.memoapp.model.Memo;

import java.util.ArrayList;

public class MemoAdapter extends RecyclerView.Adapter<MemoAdapter.ViewHolder> {

    Context context;
    ArrayList<Memo> memoArrayList;

    public MemoAdapter(Context context, ArrayList<Memo> memoList) {
        this.context = context;
        this.memoArrayList = memoList;
    }

    @NonNull
    @Override
    public MemoAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.memo_row, parent, false);
        return new MemoAdapter.ViewHolder(view) ;
    }

    @Override
    public void onBindViewHolder(@NonNull MemoAdapter.ViewHolder holder, int position) {
        Memo memo = memoArrayList.get(position);

        holder.txtTitle.setText(memo.getTitle());
        // "datetime": "2023-01-19T12:00:00"
        // => "2023-01-19 12:00"
        String date = memo.getDatetime().replace("T", " ").substring(0, 15+1);
        holder.txtDatetime.setText(date);
        holder.txtContent.setText(memo.getContent());

    }

    @Override
    public int getItemCount() {
        return memoArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView txtTitle;
        TextView txtDatetime;
        TextView txtContent;
        ImageView imgDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            txtTitle = itemView.findViewById(R.id.txtTitle);
            txtDatetime = itemView.findViewById(R.id.txtDatetime);
            txtContent = itemView.findViewById(R.id.editContent);
            imgDelete = itemView.findViewById(R.id.imgDelete);

        }
    }
}
