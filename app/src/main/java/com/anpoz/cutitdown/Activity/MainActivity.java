package com.anpoz.cutitdown.Activity;


import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.anpoz.cutitdown.Adapter.PagerAdapter;
import com.anpoz.cutitdown.App;
import com.anpoz.cutitdown.Fragment.CollectPageFragment;
import com.anpoz.cutitdown.Fragment.MainPageFragment;
import com.anpoz.cutitdown.R;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements MainPageFragment.OnItemStaredListener, CollectPageFragment.OnItemUnstaredListener {

    private ViewPager mViewPager;
    private TabLayout mTabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        initData();
    }

    /**
     * 使Volley与Activity生命周期联动
     */
    @Override
    public void onStop() {
        super.onStop();
        App.getHttpQueues().cancelAll("URL");
    }

    private void initData() {

        Toolbar toolbar= (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        List<Fragment> fragments = new ArrayList<>();
        List<String> titles = new ArrayList<>();
        fragments.add(new MainPageFragment());
        fragments.add(new CollectPageFragment());
        titles.add(getResources().getString(R.string.tab1_fragment_title));
        titles.add(getResources().getString(R.string.tab2_fragment_title));

        PagerAdapter adapter = new PagerAdapter(getSupportFragmentManager(), fragments, titles);

        mViewPager.setAdapter(adapter);
        mTabLayout.setupWithViewPager(mViewPager);

    }

    private void initViews() {
        mViewPager = (ViewPager) findViewById(R.id.vp_view);
        mTabLayout = (TabLayout) findViewById(R.id.tabs);
    }

    /**
     * 从ViewPager中获取实例化的fragment
     */
    @Override
    public void OnItemStared() {
        CollectPageFragment collectPageFragment = (CollectPageFragment) mViewPager.getAdapter().instantiateItem(mViewPager, 1);
        collectPageFragment.updateData();
    }

    @Override
    public void onItemUnstared() {
        MainPageFragment mainPageFragment = (MainPageFragment) mViewPager.getAdapter().instantiateItem(mViewPager, 0);
        mainPageFragment.updateData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(MainActivity.this, PreferencesActivity.class));

            return true;
        }

        if (id == R.id.action_about) {
            try {
                Toast.makeText(MainActivity.this,
                        "version:" + getPackageManager().getPackageInfo(getPackageName(), 0).versionName,
                        Toast.LENGTH_SHORT).show();
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
