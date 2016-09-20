package com.mobiapp.nicewallpapers;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import com.mobiapp.nicewallpapers.fragments.BaseFragment;
import com.mobiapp.nicewallpapers.fragments.BlurFragment_;
import com.mobiapp.nicewallpapers.fragments.CategoriesFragment_;
import com.mobiapp.nicewallpapers.fragments.DownloadFragment_;
import com.mobiapp.nicewallpapers.fragments.FragmentDrawer;
import com.mobiapp.nicewallpapers.fragments.FragmentDrawer_;
import com.mobiapp.nicewallpapers.fragments.OEMFragment_;
import com.mobiapp.nicewallpapers.fragments.RandomFragment_;
import com.mobiapp.nicewallpapers.util.PreferenceUtil;
import com.mobiapp.nicewallpapers.util.ScreenUtil;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

@EActivity(R.layout.activity_main)
public class MainActivity extends AppCompatActivity implements BaseFragment.OnBaseFragmentListener {

    /**
     * HeaderBarType define types of header
     */
    public enum HeaderBarType {
        TYPE_HOME, TYPE_DOWNLOAD, TYPE_DOWNLOAD_AND_VIEW
    }

    private ActionBarDrawerToggle mDrawerToggle;

    @ViewById(R.id.drawerLayout)
    protected DrawerLayout mDrawerLayout;

    @ViewById(R.id.fragmentDrawer)
    protected View mFragmentDrawer;

    @ViewById(R.id.contentFrame)
    protected FrameLayout mContentFrame;

    private Fragment mFragmentCurrent;
    private boolean isDraw;

    @AfterViews
    void init() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(true);
            mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                    R.string.drawer_open, R.string.drawer_close) {
                @Override
                public void onDrawerOpened(View drawerView) {
                    super.onDrawerOpened(drawerView);
                }

                @Override
                public void onDrawerClosed(View drawerView) {
                    super.onDrawerClosed(drawerView);
                }
            };
            mDrawerLayout.addDrawerListener(mDrawerToggle);
            mDrawerLayout.post(new Runnable() {
                @Override
                public void run() {
                    mDrawerToggle.syncState();
                }
            });
        }

        mFragmentDrawer.getLayoutParams().width = ScreenUtil.getWidthScreen(this) * 3 / 4;
        FragmentDrawer_ drawerFragment = (FragmentDrawer_)
                getSupportFragmentManager().findFragmentById(R.id.fragmentDrawer);
        drawerFragment.setDrawerListener(new FragmentDrawer.OnDrawerListener() {
            @Override
            public void OnClickDrawerItem(Fragment fragment) {
                if (null != fragment && mFragmentCurrent != fragment) {
                    replaceFragment(fragment, false);
                }
                mDrawerLayout.closeDrawer(GravityCompat.START);
            }
        });
        replaceFragment(CategoriesFragment_.builder().build(), false);

        //create shortcut icon
        boolean isAppFirstInstall = PreferenceUtil.getBoolean(this, PreferenceUtil.APP_FIRST_INSTALL, false);
        if (!isAppFirstInstall) {
            createShortCutIcon();
            PreferenceUtil.saveBoolean(this, PreferenceUtil.APP_FIRST_INSTALL, true);
        }
    }

    public void replaceFragment(Fragment fragment, boolean addToBackStack) {
        FragmentTransaction transaction = getSupportFragmentManager()
                .beginTransaction();
        if (addToBackStack) {
            transaction.setCustomAnimations(R.anim.open_main, R.anim.close_next);
        } else {
            transaction.setCustomAnimations(R.anim.open_next, R.anim.close_main, R.anim.open_main, R.anim.close_next);
        }
        if (addToBackStack) {
            transaction.addToBackStack(null);
        } else {
            transaction.addToBackStack(fragment.toString());
        }
        if (fragment.getTag() == null) {
            transaction.replace(R.id.contentFrame, fragment, fragment.toString());
        } else {
            transaction.replace(R.id.contentFrame, fragment, fragment.getTag());
        }
        transaction.replace(R.id.contentFrame, fragment);
        mFragmentCurrent = fragment;
        transaction.commit();
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
            return;
        }
        FragmentManager fm = getSupportFragmentManager();
        if (mFragmentCurrent instanceof CategoriesFragment_
                || mFragmentCurrent instanceof DownloadFragment_
                || mFragmentCurrent instanceof BlurFragment_
                || mFragmentCurrent instanceof RandomFragment_
                || mFragmentCurrent instanceof OEMFragment_) {
            finish();
        } else {
            FragmentManager.BackStackEntry backStackEntry = fm.getBackStackEntryAt(fm.getBackStackEntryCount() - 2);
            mFragmentCurrent = getSupportFragmentManager().findFragmentByTag(backStackEntry.getName());
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if (!isDraw) {
                onBackPressed();
                return super.onOptionsItemSelected(item);
            }
        }
        return mDrawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }

    @Override
    public void setTitleHeader(String title) {
        if (null != getSupportActionBar()) {
            getSupportActionBar().setTitle(title);
        }
    }

    @Override
    public void setTypeHeader(HeaderBarType type) {
        //no op
    }

    @Override
    public void setDrawerLayout(boolean isDraw) {
        this.isDraw = isDraw;
        if (isDraw) {
            mDrawerToggle.setDrawerIndicatorEnabled(true);
            mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        } else {
            mDrawerToggle.setDrawerIndicatorEnabled(false);
            mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        }
    }

    private void createShortCutIcon() {
        //remove icon shortcut
        Intent shortcutIntentMain = new Intent(Intent.ACTION_MAIN);
        shortcutIntentMain.setClassName(getPackageName(), "ShortCutActivity");
        shortcutIntentMain.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        shortcutIntentMain.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        Intent removeIntent = new Intent();
        removeIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntentMain);
        removeIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, getString(R.string.app_name_random));
        removeIntent.putExtra("duplicate", false);

        removeIntent.setAction("com.android.launcher.action.UNINSTALL_SHORTCUT");
        sendBroadcast(removeIntent);

        // add icon shorcut
        Intent shortcutIntent = new Intent(getApplicationContext(), ShortCutActivity.class);
        shortcutIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        shortcutIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        Intent addIntent = new Intent();
        addIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
        addIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, getString(R.string.app_name_random));
        addIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE,
                Intent.ShortcutIconResource.fromContext(getApplicationContext(), R.mipmap.ic_launcher_random));
        addIntent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
        sendBroadcast(addIntent);
    }
}
