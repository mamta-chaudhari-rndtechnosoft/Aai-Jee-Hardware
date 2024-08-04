package com.aaijee.app.Adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.aaijee.app.Activity.CartActivity;
import com.aaijee.app.Activity.CategoryDetailActivity;
import com.aaijee.app.Activity.MainActivity;
import com.aaijee.app.Activity.MenuDetailActivity;
import com.aaijee.app.Activity.SearchActivity;
import com.aaijee.app.Activity.WebViewActivity;
import com.aaijee.app.Fragment.HomeFragment;
import com.aaijee.app.Model.Banner;
import com.aaijee.app.Model.Model;
import com.aaijee.app.R;
import com.aaijee.app.Util.SharedPref;

import java.util.ArrayList;

public class MultiViewTypeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Activity activity;
    ArrayList<Model> homeModel;
    HomeFragment fragment;

    public MultiViewTypeAdapter(Activity activity, ArrayList<Model> randomModelList, HomeFragment homeFragment) {
        this.activity = activity;
        this.homeModel = randomModelList;
        this.fragment = homeFragment;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {
            case Model.HOME_BANNER:
                view = layoutInflater.inflate(R.layout.banner_recycler, parent, false);
                return new BannerViewHolder(view);
            case Model.HOME_SEARCH:
                view = layoutInflater.inflate(R.layout.search_layout, parent, false);
                return new SearchViewHolder(view);
            case Model.HOME_CATEGORY:
                view = layoutInflater.inflate(R.layout.home_category_recycler, parent, false);
                return new CategoryViewHolder(view);
            case Model.HOME_MENU:
                view = layoutInflater.inflate(R.layout.home_category_recycler, parent, false);
                return new MenuViewHolder(view);
            case Model.HOME_STRIP:
                view = layoutInflater.inflate(R.layout.strip_layout, parent, false);
                return new StripViewHolder(view);
            case Model.HOME_TRENDING:
                view = layoutInflater.inflate(R.layout.home_category_recycler, parent, false);
                return new MenuViewHolder(view);
            case Model.HOME_STEPS:
                view = layoutInflater.inflate(R.layout.home_category_recycler, parent, false);
                return new StepsViewHolder(view);
            default:
                return null;
        }

    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") final int position) {

        final Model object = homeModel.get(position);
        if (object != null) {
            switch (object.type) {

                //case Model.HOME_BANNER:

                //((BannerViewHolder) holder).slider_banner.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
                //((BannerViewHolder) holder).slider_banner.setCustomAnimation(new DescriptionAnimation());
                //setBannerList(object.getBanners(), ((BannerViewHolder) holder).slider_banner);

                //((BannerViewHolder) holder).viewPagerBanner.setAdapter(new BannerAdapter(activity, object.getBanners()));
                // break;

                case Model.HOME_BANNER:
                    ((BannerViewHolder) holder).viewPagerBanner.setAdapter(new BannerViewPagerAdapter(activity, object.getBanners()));
                    ((BannerViewHolder) holder).viewPagerBanner.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
                        @Override
                        public void onPageSelected(int position) {
                            super.onPageSelected(position);
                            Banner banner = object.getBanners().get(position);
                            ((BannerViewHolder) holder).viewPagerBanner.setTag(banner);
                        }
                    });
                    ((BannerViewHolder) holder).viewPagerBanner.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Banner banner = (Banner) ((BannerViewHolder) holder).viewPagerBanner.getTag();
                            if (banner != null) {
                                //handleBannerClick(banner);
                                Toast.makeText(activity, "Banner is not null", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    setBannerList(object.getBanners(), ((BannerViewHolder) holder).viewPagerBanner);
                    break;

                case Model.HOME_SEARCH:
                    ((SearchViewHolder) holder).tvSearch.setHint(object.getTitle());
                    ((SearchViewHolder) holder).tvSearch.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            activity.startActivity(new Intent(activity, SearchActivity.class));
                        }
                    });
                    break;

                case Model.HOME_CATEGORY:
                    ((CategoryViewHolder) holder).tvTitle.setText(object.getTitle());
                    ((CategoryViewHolder) holder).viewall.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            activity.startActivity(new Intent(activity, CategoryDetailActivity.class));
                        }
                    });
                    if (object.getCat_list().equalsIgnoreCase("HORIZONTAL")) {
                        ((CategoryViewHolder) holder).recyclerView.setLayoutManager(new LinearLayoutManager(activity, RecyclerView.HORIZONTAL, false));
                    } else if (object.getCat_list().equalsIgnoreCase("VERTICAL")) {
                        ((CategoryViewHolder) holder).recyclerView.setLayoutManager(new GridLayoutManager(activity, 2));
                    } else {
                        ((CategoryViewHolder) holder).recyclerView.setLayoutManager(new GridLayoutManager(activity, 2));
                    }
                    ((CategoryViewHolder) holder).recyclerView.setAdapter(new HomeCategoryAdapter(activity, object.getCategoryLists()));
                    break;

                case Model.HOME_MENU:
                    ((MenuViewHolder) holder).tvTitle.setText(object.getTitle());
                    ((MenuViewHolder) holder).recyclerView.setLayoutManager(new LinearLayoutManager(activity, RecyclerView.HORIZONTAL, false));
                    ((MenuViewHolder) holder).recyclerView.setAdapter(new HomeMenuAdapter(activity, object.getMenuLists(), object.getLink(), fragment));
                    ((MenuViewHolder) holder).viewall.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(activity, CategoryDetailActivity.class);
                            intent.putExtra("position", position);
                            intent.putExtra("cat_id", object.getLink());
                            activity.startActivity(intent);
                        }
                    });
                    break;

                case Model.HOME_STRIP:
                    ((StripViewHolder) holder).tvTitle.setText(object.getTitle());
                    ((StripViewHolder) holder).tvSubtitle.setText(object.getSubtitle());
                    GradientDrawable gd = new GradientDrawable();
                    if (SharedPref.getDASHED(activity).equalsIgnoreCase("1")) {
                        gd.setColor(Color.WHITE);
                        gd.setCornerRadius(50);
                        gd.setStroke(4, Color.parseColor(object.getBorder_color()), 12, 16);
                    } else {
                        gd.setColor(Color.WHITE);
                        gd.setCornerRadius(50);
                        gd.setStroke(4, Color.parseColor(object.getBorder_color()));
                    }
                    ((StripViewHolder) holder).rel_layout.setBackgroundDrawable(gd);
                    Glide.with(activity).load(object.getIcon()).thumbnail(Glide.with(activity).load(R.drawable.loading)).into(((StripViewHolder) holder).img_strip);

                    ((StripViewHolder) holder).rel_layout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            MainActivity mainActivity = (MainActivity) activity;
                            if (object.getLink().equalsIgnoreCase("Food")) {
                                Intent intent = new Intent(activity, MenuDetailActivity.class);
                                intent.putExtra("menu_id", object.getStrip_link());
                                intent.putExtra("menu_name", object.getTitle());
                                activity.startActivity(intent);
                            } else if (object.getLink().equalsIgnoreCase("Cart")) {
                                Intent intent = new Intent(activity, CartActivity.class);
                                activity.startActivity(intent);
                            } else if (object.getLink().equalsIgnoreCase("Link")) {
                                Intent intent = new Intent(activity, WebViewActivity.class);
                                intent.putExtra("link", object.getStrip_link());
                                intent.putExtra("title", object.getTitle());
                                activity.startActivity(intent);
                            } else if (object.getLink().equalsIgnoreCase("categories")) {
                                Intent intent = new Intent(activity, CategoryDetailActivity.class);
                                intent.putExtra("position", position);
                                intent.putExtra("cat_id", object.getStrip_link());
                                activity.startActivity(intent);
                            } else if (object.getLink().equalsIgnoreCase("Orders")) {
                                mainActivity.viewPager.setCurrentItem(1);
                            } else {
                                mainActivity.viewPager.setCurrentItem(0);
                            }

                        }
                    });

                    break;

                case Model.HOME_TRENDING:
                    ((MenuViewHolder) holder).tvTitle.setText(object.getTitle());
                    ((MenuViewHolder) holder).viewall.setVisibility(View.GONE);
                    ((MenuViewHolder) holder).recyclerView.setLayoutManager(new LinearLayoutManager(activity, RecyclerView.HORIZONTAL, false));
                    ((MenuViewHolder) holder).recyclerView.setAdapter(new HomeMenuAdapter(activity, object.getMenuLists(), object.getLink(), fragment));
                    ((MenuViewHolder) holder).viewall.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(activity, CategoryDetailActivity.class);
                            intent.putExtra("position", position);
                            intent.putExtra("cat_id", object.getLink());
                            activity.startActivity(intent);
                        }
                    });
                    break;

                case Model.HOME_STEPS:
                    ((StepsViewHolder) holder).tvTitle.setText(Html.fromHtml(object.getTitle()));
                    ((StepsViewHolder) holder).viewall.setVisibility(View.GONE);
                    ((StepsViewHolder) holder).recyclerView.setLayoutManager(new GridLayoutManager(activity, 3));
                    ((StepsViewHolder) holder).recyclerView.setAdapter(new StepsAdapter(activity, object.getSteps()));
                    break;


                default:
                    break;
            }
        }

    }

   /* private void handleBannerClick(Banner banner) {
        if (banner.getLink().equalsIgnoreCase("Food")) {
            Intent intent = new Intent(activity, MenuDetailActivity.class);
            intent.putExtra("menu_id", banner.getCat_id());
            intent.putExtra("menu_name", banner.getTitle());
            activity.startActivity(intent);
        } else if (banner.getLink().equalsIgnoreCase("categories")) {
            Intent intent = new Intent(activity, CategoryDetailActivity.class);
            intent.putExtra("cat_id", banner.getCat_id());
            activity.startActivity(intent);
        } else if (banner.getLink().equalsIgnoreCase("Link")) {
            Intent intent = new Intent(activity, WebViewActivity.class);
            intent.putExtra("link", banner.getStrip_link());
            intent.putExtra("title", banner.getTitle());
            activity.startActivity(intent);
        } else {
            activity.startActivity(new Intent(activity, MainActivity.class));
        }
    }*/


    private void setBannerList(ArrayList<Banner> banners, ViewPager2 viewPagerBanner) {
        BannerViewPagerAdapter adapter = new BannerViewPagerAdapter(activity, banners);
        viewPagerBanner.setAdapter(adapter);
        if (banners.size() > 1) {
            viewPagerBanner.setClipToPadding(false);
            viewPagerBanner.setClipChildren(false);
            viewPagerBanner.setOffscreenPageLimit(3);
            viewPagerBanner.getChildAt(0).setOverScrollMode(View.OVER_SCROLL_NEVER);
        }
    }


    @Override
    public int getItemViewType(int position) {
        switch (homeModel.get(position).type) {
            case 1:
                return Model.HOME_BANNER;
            case 2:
                return Model.HOME_SEARCH;
            case 3:
                return Model.HOME_CATEGORY;
            case 4:
                return Model.HOME_MENU;
            case 5:
                return Model.HOME_STRIP;
            case 6:
                return Model.HOME_TRENDING;
            case 7:
                return Model.HOME_STEPS;
            default:
                return -1;
        }


    }

    @Override
    public int getItemCount() {
        return homeModel.size();
    }

    /*private void setBannerList(final ArrayList<Banner> banners, ViewPager2 viewPager2) {
        for (int i = 0; i < banners.size(); i++) {
            DefaultSliderView defaultSliderView = new DefaultSliderView(activity);
            // initialize a SliderLayout
            defaultSliderView
                    .image(banners.get(i).getImage())
                    .setScaleType(BaseSliderView.ScaleType.Fit);
            slider_banner.addSlider(defaultSliderView);
            final MainActivity mainActivity = (MainActivity) activity;
            final int finalI = i;
            final int finalI1 = i;
            defaultSliderView.image(banners.get(i).getImage())
                    .setOnSliderClickListener(new BaseSliderView.OnSliderClickListener() {
                        @Override
                        public void onSliderClick(BaseSliderView slider) {
                            if (banners.get(finalI1).getType().equalsIgnoreCase("Food")) {
                                Intent intent = new Intent(activity, MenuDetailActivity.class);
                                intent.putExtra("menu_id", banners.get(finalI1).getLink());
                                intent.putExtra("menu_name", banners.get(finalI1).getTitle());
                                activity.startActivity(intent);
                            } else if (banners.get(finalI1).getType().equalsIgnoreCase("Cart")) {
                                Intent intent = new Intent(activity, CartActivity.class);
                                activity.startActivity(intent);
                            } else if (banners.get(finalI1).getType().equalsIgnoreCase("Link")) {
                                Intent intent = new Intent(activity, WebViewActivity.class);
                                intent.putExtra("link", banners.get(finalI1).getLink());
                                intent.putExtra("title", banners.get(finalI1).getTitle());
                                activity.startActivity(intent);
                            } else if (banners.get(finalI1).getType().equalsIgnoreCase("Orders")) {
                                mainActivity.viewPager.setCurrentItem(1);
                            } else {
                                mainActivity.viewPager.setCurrentItem(0);
                            }
                        }
                    });
        }
    }*/

    /*private void setBannerList(final ArrayList<Banner> banners, ViewPager2 viewPager) {
        BannerAdapter bannerAdapter = new BannerAdapter(activity, banners);
        viewPager.setAdapter(bannerAdapter);

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                Banner banner = banners.get(position);
                viewPager.setTag(banner);
            }
        });

        viewPager.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Banner banner = (Banner) viewPager.getTag();
                if (banner != null) {
                    // Handle item click events based on banner type
                    // Example:
                    if (banner.getType().equalsIgnoreCase("Food")) {
                        Intent intent = new Intent(activity, MenuDetailActivity.class);
                        intent.putExtra("menu_id", banner.getLink());
                        intent.putExtra("menu_name", banner.getTitle());
                        activity.startActivity(intent);
                    } else if (banner.getType().equalsIgnoreCase("Cart")) {
                        Intent intent = new Intent(activity, CartActivity.class);
                        activity.startActivity(intent);
                    } else if (banner.getType().equalsIgnoreCase("Link")) {
                        Intent intent = new Intent(activity, WebViewActivity.class);
                        intent.putExtra("link", banner.getLink());
                        intent.putExtra("title", banner.getTitle());
                        activity.startActivity(intent);
                    } else {
                        // Handle other banner types accordingly
                    }
                }
            }
        });
    }*/


    private class BannerViewHolder extends RecyclerView.ViewHolder {

        //SliderLayout slider_banner;
        ViewPager2 viewPagerBanner;


        public BannerViewHolder(View view) {
            super(view);
            //slider_banner = (SliderLayout) itemView.findViewById(R.id.slider_banner);
            viewPagerBanner = (ViewPager2) itemView.findViewById(R.id.slider_banner);
        }
    }

    private class SearchViewHolder extends RecyclerView.ViewHolder {

        TextView tvSearch;

        public SearchViewHolder(View view) {
            super(view);
            tvSearch = (TextView) itemView.findViewById(R.id.tvSearch);
        }
    }

    private class CategoryViewHolder extends RecyclerView.ViewHolder {

        RecyclerView recyclerView;
        TextView tvTitle, viewall;

        public CategoryViewHolder(View view) {
            super(view);
            recyclerView = (RecyclerView) itemView.findViewById(R.id.recycler_category);
            tvTitle = (TextView) itemView.findViewById(R.id.tvCategory);
            viewall = (TextView) itemView.findViewById(R.id.viewall);
        }
    }

    private class MenuViewHolder extends RecyclerView.ViewHolder {

        RecyclerView recyclerView;
        TextView tvTitle, viewall;

        public MenuViewHolder(View view) {
            super(view);
            recyclerView = (RecyclerView) itemView.findViewById(R.id.recycler_category);
            tvTitle = (TextView) itemView.findViewById(R.id.tvCategory);
            viewall = (TextView) itemView.findViewById(R.id.viewall);
        }
    }

    private class StripViewHolder extends RecyclerView.ViewHolder {

        TextView tvTitle, tvSubtitle;
        ImageView img_strip;
        RelativeLayout rel_layout;

        public StripViewHolder(View view) {
            super(view);
            tvTitle = (TextView) itemView.findViewById(R.id.tvTitle);
            tvSubtitle = (TextView) itemView.findViewById(R.id.tvSubtitle);
            img_strip = (ImageView) itemView.findViewById(R.id.img_strip);
            rel_layout = (RelativeLayout) itemView.findViewById(R.id.rel_layout);
        }
    }

    private class StepsViewHolder extends RecyclerView.ViewHolder {

        RecyclerView recyclerView;
        TextView tvTitle, viewall;

        public StepsViewHolder(View view) {
            super(view);
            recyclerView = (RecyclerView) itemView.findViewById(R.id.recycler_category);
            tvTitle = (TextView) itemView.findViewById(R.id.tvCategory);
            viewall = (TextView) itemView.findViewById(R.id.viewall);
        }
    }

}
