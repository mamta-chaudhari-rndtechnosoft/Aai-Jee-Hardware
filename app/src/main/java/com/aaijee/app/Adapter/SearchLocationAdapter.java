package com.aaijee.app.Adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aaijee.app.Activity.SearchDeliveryLocationActivity;
import com.aaijee.app.Model.Address;
import com.aaijee.app.R;

import java.util.ArrayList;

public class SearchLocationAdapter extends RecyclerView.Adapter {

    public ArrayList<Address> arrayList;
    Activity context;
    private SearchDeliveryLocationActivity.RecyclerViewClickListener mListener;

    private class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private SearchDeliveryLocationActivity.RecyclerViewClickListener mListener;


        private TextView cityTextView,fullTextView;
        ImageView imageView;


        private MyViewHolder(View v, SearchDeliveryLocationActivity.RecyclerViewClickListener listener) {
            super(v);

            mListener = listener;
            cityTextView = v.findViewById(R.id.city);
            imageView = v.findViewById(R.id.image);
            fullTextView = v.findViewById(R.id.fulltext);
            v.setOnClickListener(this);


        }
        @Override
        public void onClick(View view) {
            mListener.onClick(view, getAdapterPosition());
        }
    }

    public SearchLocationAdapter(Activity context, ArrayList<Address> arrayList, SearchDeliveryLocationActivity.RecyclerViewClickListener mListener) {
        this.arrayList = arrayList;
        this.context=context;
        this.mListener = mListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_location_item, parent, false);
        return new MyViewHolder(itemView,mListener);
    }


    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position) {

        MyViewHolder myViewHolder = (MyViewHolder)holder;

        String add_type = arrayList.get(position).getAddress_type();
        if (add_type.equals("home")) {
            ((SearchLocationAdapter.MyViewHolder) holder).cityTextView.setText(arrayList.get(position).getAddress_type());
            ((MyViewHolder) holder).fullTextView.setText(arrayList.get(position).getFlat_no()+","
                    +arrayList.get(position).getBuilding_name()+",\n"+arrayList.get(position).getLandmark()+", "
                    +arrayList.get(position).getCity());
        }else if (add_type.equals("office")){
            ((SearchLocationAdapter.MyViewHolder) holder).cityTextView.setText(arrayList.get(position).getAddress_type());

            ((MyViewHolder) holder).fullTextView.setText(arrayList.get(position).getFlat_no()+","
                    +arrayList.get(position).getBuilding_name()+",\n"+arrayList.get(position).getLandmark()+", "
                    +arrayList.get(position).getCity());

        }else if(add_type.equals("other")){
            ((SearchLocationAdapter.MyViewHolder) holder).cityTextView.setText(arrayList.get(position).getAddress_type());

            ((MyViewHolder) holder).fullTextView.setText(arrayList.get(position).getFlat_no()+","
                    +arrayList.get(position).getBuilding_name()+",\n"+arrayList.get(position).getLandmark()+", "
                    +arrayList.get(position).getCity());
        }
        else
        {
            myViewHolder.cityTextView.setText(arrayList.get(position).getCity());
            myViewHolder.fullTextView.setText(arrayList.get(position).getName());

        }
    }



    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }



}