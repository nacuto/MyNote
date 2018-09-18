package com.burytomorrow.final_project;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by BuryTomorrow on 2017/11/12.
 */


public abstract class CommonAdapter<T> extends RecyclerView.Adapter<MyViewHolder> {
    protected Context mContext;
    protected int mLayoutId;
    protected List<T> mDatas;
    protected LayoutInflater mInflater;
    protected OnItemClickListener mOnItemClickListener;

    public CommonAdapter(Context context, int layoutId, List datas){
        mContext = context;
        mInflater = LayoutInflater.from(context);
        mLayoutId = layoutId;
        mDatas = datas;
    }

    @Override
    public MyViewHolder onCreateViewHolder(final ViewGroup parent, int viewType){
        MyViewHolder viewHolder = MyViewHolder.get(mContext,parent,mLayoutId);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder,int position){
        convert(holder,mDatas.get(position));

        if(mOnItemClickListener!=null){
            holder.itemView.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    mOnItemClickListener.onClick(holder.getAdapterPosition());
                }
            });
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener(){
                @Override
                public boolean onLongClick(View v){
                    mOnItemClickListener.onLongClick(holder.getAdapterPosition());
                    return false;
                }
            });
        }
    }
    public abstract void convert(MyViewHolder holder,T t);

    @Override
    public int getItemCount(){
        return mDatas.size();
    }

    public interface OnItemClickListener{
        void onClick(int position);
        void onLongClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener){
        this.mOnItemClickListener = onItemClickListener;
    }

}