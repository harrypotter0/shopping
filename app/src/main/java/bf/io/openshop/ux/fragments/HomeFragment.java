package bf.io.openshop.ux.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import bf.io.openshop.CONST;
import bf.io.openshop.MyApplication;
import bf.io.openshop.R;
import bf.io.openshop.api.EndPoints;
import bf.io.openshop.api.GsonRequest;
import bf.io.openshop.entities.Banner;
import bf.io.openshop.entities.BannersResponse;
import bf.io.openshop.entities.Metadata;
import bf.io.openshop.interfaces.BannersRecyclerInterface;
import bf.io.openshop.listeners.OnSingleClickListener;
import bf.io.openshop.utils.EndlessRecyclerScrollListener;
import bf.io.openshop.utils.MsgUtils;
import bf.io.openshop.utils.RecyclerDividerDecorator;
import bf.io.openshop.utils.RecyclerMarginDecorator;
import bf.io.openshop.utils.Utils;
import bf.io.openshop.ux.MainActivity;
import bf.io.openshop.ux.adapters.BannersRecyclerAdapter;
import bf.io.openshop.ux.adapters.CategoriesRecyclerAdapter;
import bf.io.openshop.ux.adapters.ProductImagesPagerAdapter;
import timber.log.Timber;

public class HomeFragment extends Fragment implements ViewPager.OnPageChangeListener,CategoriesRecyclerAdapter.ItemClickListener{

    private ArrayList<String> images;
    private int defaultPosition = 0;
    private int totalIndicators;
    private ViewPager imagesPager;
    private ImageView imageView;
    private LinearLayout mIndicatorLayout;
    private PagerAdapter mPagerAdapter;
    private static int currentPage = 0;
    private static int NUM_PAGES = 0;

    private ProgressDialog progressDialog;
    private RecyclerView mRVCategories;
    private BannersRecyclerAdapter mBannersAdapter;
    private EndlessRecyclerScrollListener endlessRecyclerScrollListener;
    private Metadata bannersMetadata;
    private Context mContext;

    /**
     * Indicates if user data should be loaded from server or from memory.
     */
    private boolean mAlreadyLoaded = false;

    /**
     * Holds reference for empty view. Show to user when no data loaded.
     */
    private View emptyContent;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
         super.onCreateView(inflater, container, savedInstanceState);

        Timber.d("%s - OnCreateView", this.getClass().getSimpleName());
        MainActivity.setActionBarTitle(getString(R.string.Just_arrived));
        View view = inflater.inflate(R.layout.fragment_home,container,false);

        progressDialog = Utils.generateProgressDialog(getActivity(), false);
        initViews(view);
        prepareEmptyContent(view);
        // Don't reload data when return from backStack. Reload if a new instance was created or data was empty.
        if ((savedInstanceState == null && !mAlreadyLoaded) || mBannersAdapter == null || mBannersAdapter.isEmpty()) {
            Timber.d("Reloading banners.");
            mAlreadyLoaded = true;

            // Prepare views and listeners
            prepareContentViews(view, true);
            loadBanners(null);
        } else {
            Timber.d("Banners already loaded.");
            prepareContentViews(view, false);
            // Already loaded
        }
        return view;
    }

    /**
     * Prepares views and listeners associated with content.
     *
     * @param view       fragment root view.
     * @param freshStart indicates when everything should be recreated.
     */
    private void prepareContentViews(View view, boolean freshStart) {
        mRVCategories = (RecyclerView) view.findViewById(R.id.rv_categories);
        mRVCategories.setHasFixedSize(true);
        int numberOfColumns = 3;
        mRVCategories.setLayoutManager(new GridLayoutManager(getActivity(), numberOfColumns));
        if (freshStart) {
            mBannersAdapter = new BannersRecyclerAdapter(getActivity(), new BannersRecyclerInterface() {
                @Override
                public void onBannerSelected(Banner banner) {
                    Activity activity = getActivity();
                    if (activity instanceof MainActivity) {
                        ((MainActivity) activity).onBannerSelected(banner);
                    }
                }
            });
        }
        GridLayoutManager layoutManager = new GridLayoutManager(mRVCategories.getContext(),numberOfColumns);
        mRVCategories.setLayoutManager(layoutManager);
        mRVCategories.setItemAnimator(new DefaultItemAnimator());
        mRVCategories.setHasFixedSize(true);
        mRVCategories.setAdapter(mBannersAdapter);
        endlessRecyclerScrollListener = new EndlessRecyclerScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int currentPage) {
                if (bannersMetadata != null && bannersMetadata.getLinks() != null && bannersMetadata.getLinks().getNext() != null) {
                    loadBanners(bannersMetadata.getLinks().getNext());
                } else {
                    Timber.d("CustomLoadMoreDataFromApi NO MORE DATA");
                }
            }
        };
        mRVCategories.addOnScrollListener(endlessRecyclerScrollListener);
    }

    private void initViews(View view) {

        imagesPager = (ViewPager) view.findViewById(R.id.dialog_product_detail_images_pager);
        mIndicatorLayout = (LinearLayout) view.findViewById(R.id.indicator_layout);

      /*  mRVCategories = (RecyclerView) view.findViewById(R.id.rv_categories);
        mRVCategories.setHasFixedSize(true);
        int numberOfColumns = 3;
        mRVCategories.setLayoutManager(new GridLayoutManager(getActivity(), numberOfColumns));
        String[] data = new String[9];
        mCategoriesAdapter = new CategoriesRecyclerAdapter(mContext, data);
        mCategoriesAdapter.setClickListener(this);
        mRVCategories.setAdapter(mCategoriesAdapter);*/

        images = new ArrayList<>();

        images.add("https://dummyimage.com/400x200/000/fff");
        images.add("https://dummyimage.com/400x200/ff0000/fff");
        images.add("https://dummyimage.com/400x200/0011ff/fff");
        images.add("https://dummyimage.com/400x200/ffff00/fff");
        // Prepare endless image adapter
        mPagerAdapter = new ProductImagesPagerAdapter(mContext, images);
        imagesPager.setAdapter(mPagerAdapter);
        imagesPager.setCurrentItem(0);
        imagesPager.addOnPageChangeListener(this);
        totalIndicators = mPagerAdapter.getCount();
        for (int i = 0; i < totalIndicators; i++) {
            imageView = new ImageView(getActivity());
            imageView.setImageResource(R.drawable.inactive);
            imageView.setId(i);
            imageView.setPadding(10, 0, 0, 0);
            imageView.setLayoutParams(new ViewGroup.LayoutParams(25, 25));

            mIndicatorLayout.addView(imageView);
            mIndicatorLayout.setGravity(Gravity.CENTER);
        }
        setActiveIndicator(0);
        autoScrollImages();

        if (defaultPosition > 0 && defaultPosition < images.size())
            imagesPager.setCurrentItem(defaultPosition);
        else
            imagesPager.setCurrentItem(0);

    }

    private void autoScrollImages() {
        // Auto start of viewpager
        final Handler handler = new Handler();
        final Runnable Update = new Runnable() {
            public void run() {
                if (currentPage == NUM_PAGES) {
                    currentPage = 0;
                } else if (currentPage == totalIndicators) {
                    currentPage = 0;
                }
                imagesPager.setCurrentItem(currentPage++, true);

            }
        };

        Timer swipeTimer = new Timer();
        swipeTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(Update);
            }
        }, 500, 3000);
    }

    private void setActiveIndicator(int id) {
        ImageView view = (ImageView) mIndicatorLayout.findViewById(id);
        view.setImageResource(R.drawable.active);
    }

    private void deselectAllIndicatorImages() {
        for (int i = 0; i < totalIndicators; i++) {
            ImageView view = (ImageView) mIndicatorLayout.findViewById(i);
            view.setImageResource(R.drawable.inactive);
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        currentPage = position;

        deselectAllIndicatorImages();
        setActiveIndicator(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onItemClick(View view, int position) {
        Log.e("HomeFrag","onitemClick position"+position);
    }

    /**
     * Prepares views and listeners associated with empty content. Visible only when no content loads.
     *
     * @param view fragment root view.
     */
    private void prepareEmptyContent(View view) {
        emptyContent = view.findViewById(R.id.banners_empty);
        TextView emptyContentAction = (TextView) view.findViewById(R.id.banners_empty_action);
        emptyContentAction.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                // Just open drawer menu.
                Activity activity = getActivity();
                if (activity instanceof MainActivity) {
                    MainActivity mainActivity = (MainActivity) activity;
                    if (mainActivity.drawerFragment != null)
                        mainActivity.drawerFragment.toggleDrawerMenu();
                }
            }
        });
    }

    /**
     * Endless content loader. Should be used after views inflated.
     *
     * @param url null for fresh load. Otherwise use URLs from response metadata.
     */
    private void loadBanners(String url) {
        progressDialog.show();
        if (url == null) {
            mBannersAdapter.clear();
            url = String.format(EndPoints.BANNERS);
        }
        GsonRequest<BannersResponse> getBannersRequest = new GsonRequest<>(Request.Method.GET, url, null, BannersResponse.class,
                new Response.Listener<BannersResponse>() {
                    @Override
                    public void onResponse(@NonNull BannersResponse response) {
                        Timber.d("response: %s", response.toString());
                        bannersMetadata = response.getMetadata();
                        mBannersAdapter.addBanners(response.getRecords());

                        if (mBannersAdapter.getItemCount() > 0) {
                            emptyContent.setVisibility(View.INVISIBLE);
                            mRVCategories.setVisibility(View.VISIBLE);
                        } else {
                            emptyContent.setVisibility(View.VISIBLE);
                            mRVCategories.setVisibility(View.INVISIBLE);
                        }

                        progressDialog.cancel();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (progressDialog != null) progressDialog.cancel();
                MsgUtils.logAndShowErrorMessage(getActivity(), error);
            }
        });
        getBannersRequest.setRetryPolicy(MyApplication.getDefaultRetryPolice());
        getBannersRequest.setShouldCache(false);
        MyApplication.getInstance().addToRequestQueue(getBannersRequest, CONST.BANNER_REQUESTS_TAG);
    }

    @Override
    public void onStop() {
        if (progressDialog != null) {
            // Hide progress dialog if exist.
            if (progressDialog.isShowing() && endlessRecyclerScrollListener != null) {
                // Fragment stopped during loading data. Allow new loading on return.
                endlessRecyclerScrollListener.resetLoading();
            }
            progressDialog.cancel();
        }
        MyApplication.getInstance().cancelPendingRequests(CONST.BANNER_REQUESTS_TAG);
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        if (mRVCategories != null) mRVCategories.clearOnScrollListeners();
        super.onDestroyView();
    }
}
