package me.tictok.hiddenwheelview;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

public class WheelAdapter extends RecyclerView.Adapter<WheelAdapter.WheelViewHolder> {

    private List<String> mStringList;
    private int mItemLayoutId;
    private Context context;

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
        void onItemLongClick(View view, int position);
    }

    private OnItemClickListener mOnItemClickListener;

    public void setmOnItemClickListener(OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    public static class WheelViewHolder extends RecyclerView.ViewHolder {
        public LinearLayout mListItem;
        public WheelViewHolder(LinearLayout listItem) {
            super(listItem);
            mListItem = listItem;
        }
    }

    public WheelAdapter(List<String> stringList, int itemLayoutId) {
        this.mStringList = stringList;
        this.mItemLayoutId = itemLayoutId;
    }

    @NonNull
    @Override
    public WheelViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        LinearLayout listItem = (LinearLayout) LayoutInflater
                .from(viewGroup.getContext())
                .inflate(mItemLayoutId, viewGroup, false);
        if (context == null) {
            context = viewGroup.getContext();
        }

        return new WheelViewHolder(listItem);
    }

    @Override
    public void onBindViewHolder(@NonNull final WheelViewHolder wheelViewHolder, int i) {

        TextView textView = wheelViewHolder.mListItem.findViewById(R.id.textView);
        textView.setText(mStringList.get(i));
        if (mOnItemClickListener != null) {
            wheelViewHolder.mListItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClickListener.onItemClick(wheelViewHolder.mListItem, wheelViewHolder.getAdapterPosition());
                }
            });

            wheelViewHolder.mListItem.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    mOnItemClickListener.onItemLongClick(wheelViewHolder.mListItem, wheelViewHolder.getAdapterPosition());
                    return false;
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mStringList.size();
    }
}
