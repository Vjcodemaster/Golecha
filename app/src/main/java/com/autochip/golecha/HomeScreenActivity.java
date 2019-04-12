package com.autochip.golecha;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import app_utility.DatabaseHandler;
import app_utility.OnFragmentInteractionListener;
import app_utility.GolechaAsyncTask;

import android.animation.Animator;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.TextView;

public class HomeScreenActivity extends AppCompatActivity implements OnFragmentInteractionListener {

    public static OnFragmentInteractionListener onFragmentInteractionListener;
    Fragment newFragment;
    FragmentTransaction transaction;
    String sBackStackParent;

    View viewAnimate;

    int nUserDisplayHeight;
    int[] nOffSetLocation;
    int nDisplayDDXOffSet; //display drop down x off set
    int nDisplayOffSetD3;

    Toolbar toolbar;
    TextView tvSubTitle;

    DatabaseHandler dbh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        initClasses();
        initViews();

        GolechaAsyncTask golechaAsyncTask = new GolechaAsyncTask(HomeScreenActivity.this, dbh);
        golechaAsyncTask.execute(String.valueOf(4), "");
    }

    private void initClasses(){
        onFragmentInteractionListener = this;
        dbh = new DatabaseHandler(HomeScreenActivity.this);
    }

    private void initViews(){
        toolbar = findViewById(R.id.toolbar);

        tvSubTitle = toolbar.findViewById(R.id.tv_sub_title);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    public void onClickOrderButton(View view) {

        switch (view.getId()) {
            case R.id.btn_my_orders:
                newFragment = MyOrdersFragment.newInstance("","");
                sBackStackParent = newFragment.getClass().getName();
                viewAnimate = findViewById(R.id.btn_my_orders);
                //tvSubTitle.setText(R.string.my_orders);
                changeTitleTo(1, "");
                break;
            case R.id.btn_catalogue:
                newFragment = CatalogueFragment.newInstance("","");
                sBackStackParent = newFragment.getClass().getName();

                viewAnimate = findViewById(R.id.btn_catalogue);
                changeTitleTo(2, "");
                break;
            case R.id.btn_dashboard:
                newFragment = DashboardFragment.newInstance("","");
                sBackStackParent = newFragment.getClass().getName();

                viewAnimate = findViewById(R.id.btn_dashboard);
                changeTitleTo(3, "");
                break;
            case R.id.btn_settings:
                newFragment = SettingsFragment.newInstance("","");
                sBackStackParent = newFragment.getClass().getName();

                viewAnimate = findViewById(R.id.btn_settings);
                changeTitleTo(4, "");
                break;
        }
        transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.t2b, R.anim.b2t);
        transaction.replace(R.id.fl_container, newFragment, sBackStackParent);
        transaction.addToBackStack(null);
        transaction.commit();
        show(viewAnimate, findViewById(R.id.fl_container));
    }

    public void changeTitleTo(int nCase, String sCase) {
        switch (nCase) {
            case 1:
                tvSubTitle.setText(R.string.my_orders);
                tvSubTitle.setVisibility(View.VISIBLE);
                break;
            case 2:
                tvSubTitle.setText(R.string.catalogue);
                tvSubTitle.setVisibility(View.VISIBLE);
                break;
            case 3:
                tvSubTitle.setText(R.string.dashboard);
                tvSubTitle.setVisibility(View.VISIBLE);
                break;
            case 4:
                tvSubTitle.setText(R.string.settings);
                tvSubTitle.setVisibility(View.VISIBLE);
                break;
            case 5:
                tvSubTitle.setText(R.string.create_order);
                tvSubTitle.setVisibility(View.VISIBLE);
                break;
            case 6:
                tvSubTitle.setText(R.string.view_order);
                tvSubTitle.setVisibility(View.VISIBLE);
                break;
            default:
                //tvSubTitle.setText(R.string.app_name);
                tvSubTitle.setVisibility(View.GONE);
                break;
        }
    }

    /*
     To reveal a previously invisible view using this effect:
     below method show is used to produce circular animation effect on home screen buttons.
     */
    private void show(final View view, final View mParentView) {

        nUserDisplayHeight = getResources().getDisplayMetrics().heightPixels; //holds height of screen in pixels

        nOffSetLocation = new int[2];
        view.getLocationInWindow(nOffSetLocation);
        nDisplayOffSetD3 = (nUserDisplayHeight + nOffSetLocation[1]) / 10;

        nDisplayDDXOffSet = (nOffSetLocation[0] / 2) + nDisplayOffSetD3;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // get the final radius for the clipping circle
            int finalRadius = Math.max(mParentView.getWidth(), mParentView.getHeight());

            //create the animator for this view (the start radius is zero)
            Animator anim;
            anim = ViewAnimationUtils.createCircularReveal(mParentView, nDisplayDDXOffSet, nOffSetLocation[1],
                    0, finalRadius);
            anim.setInterpolator(new AccelerateDecelerateInterpolator());
            anim.setDuration(350);
            mParentView.setVisibility(View.VISIBLE);
            anim.start();
        }
    }

    private void backPressed(){
        String[] saTag;
        int size = getSupportFragmentManager().getBackStackEntryCount();
        if (size >= 1) {
            Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fl_container);
            saTag = currentFragment.getTag().replace(".", ",").split(",");
            switch (saTag[3]) {
                case "MyOrdersFragment":
                    changeTitleTo(1, "");
                    break;
                case "CatalogueFragment":
                    changeTitleTo(2, "");
                    break;
                case "DashboardFragment":
                    changeTitleTo(3, "");
                    break;
                case "SettingFragment":
                    changeTitleTo(4, "");
                    break;
                case "CreateOrderFragment":
                    changeTitleTo(5, "");
                    break;
                case "ViewOrderFragment":
                    changeTitleTo(6, "");
                    break;
                default:
                    changeTitleTo(0, "");
                    break;
            }
        } else if(size==0){
            tvSubTitle.setVisibility(View.GONE);
        }

    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        backPressed();
    }

    @Override
    public void onFragmentMessage(String sCase, int nFlag, String sDate,String sStatus) {
        switch (sCase) {
            case "MY_ORDER_FAB_CLICK":
                newFragment = new CreateOrderFragment();
                sBackStackParent = newFragment.getClass().getName();
                transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fl_container, newFragment, sBackStackParent);
                transaction.addToBackStack(null);
                transaction.commit();

                viewAnimate = findViewById(R.id.fab_create_order);
                show(viewAnimate, findViewById(R.id.fl_container));
                changeTitleTo(5, "");
                break;
            case "MY_ORDER_ITEM_CLICK":
                changeTitleTo(6, "");
                break;
        }
    }
}
