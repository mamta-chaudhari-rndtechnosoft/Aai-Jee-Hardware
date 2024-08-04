package com.aaijee.app.Adapter;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.aaijee.app.Activity.CartActivity;
import com.aaijee.app.Activity.MenuDetailActivity;
import com.aaijee.app.Activity.WebViewActivity;
import com.aaijee.app.Model.Banner;
import com.aaijee.app.R;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class BannerViewPagerAdapter extends RecyclerView.Adapter<BannerViewPagerAdapter.ViewHolder> {

    private Activity activity;
    private ArrayList<Banner> bannerList;

    public BannerViewPagerAdapter(Activity activity, ArrayList<Banner> bannerList) {
        this.activity = activity;
        this.bannerList = bannerList;
    }

    @NonNull
    @Override
    public BannerViewPagerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.banner_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BannerViewPagerAdapter.ViewHolder holder, int position) {
        Banner banner = bannerList.get(position);
        Glide.with(activity)
                .load(banner.getImage())
                .centerCrop()
                .thumbnail(Glide.with(activity).load(R.drawable.loading))
                .into(holder.imgBanner);
        holder.tvBanner.setText(banner.getTitle());
        holder.tvBanner.setVisibility(View.GONE);

        holder.rel_layout.setOnClickListener(v -> handleBannerClick(banner));
    }

    @Override
    public int getItemCount() {
        return bannerList.size();
    }

    private void handleBannerClick(Banner banner) {
        Intent intent;
        switch (banner.getType().toLowerCase()) {
            case "food":
                intent = new Intent(activity, MenuDetailActivity.class);
                intent.putExtra("menu_id", banner.getLink());
                intent.putExtra("menu_name", banner.getTitle());
                activity.startActivity(intent);
                break;
            case "cart":
                intent = new Intent(activity, CartActivity.class);
                activity.startActivity(intent);
                break;
            case "link":
                intent = new Intent(activity, WebViewActivity.class);
                intent.putExtra("link", banner.getLink());
                intent.putExtra("title", banner.getTitle());
                activity.startActivity(intent);
                break;
            default:
                // Handle other banner types accordingly
                break;
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView imgBanner;
        TextView tvBanner;
        RelativeLayout rel_layout;

        ViewHolder(View itemView) {
            super(itemView);
            imgBanner = itemView.findViewById(R.id.imgBanner);
            tvBanner = itemView.findViewById(R.id.tvBanner);
            rel_layout = itemView.findViewById(R.id.rel_layout);
        }
    }
}
