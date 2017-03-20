package com.hellowo.myclass.activity;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hellowo.myclass.R;
import com.hellowo.myclass.adapter.StudentListAdapter;
import com.hellowo.myclass.databinding.ActivityHomeClassBinding;
import com.hellowo.myclass.model.MyClass;
import com.hellowo.myclass.model.Student;
import com.hellowo.myclass.utils.StudentUtil;
import com.nononsenseapps.filepicker.FilePickerActivity;

import java.io.File;
import java.util.ArrayList;

import devlight.io.library.ntb.NavigationTabBar;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;

public class HomeMyClassActivity extends AppCompatActivity {
    public final static String INTENT_KEY_MY_CLASS_ID = "INTENT_KEY_MY_CLASS_ID";
    public final static int FILE_CODE = 8250;
    private ActivityHomeClassBinding binding;
    private Realm realm;
    private MyClass myClass;
    private RealmResults<Student> studentRealmResults;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_home_class);
        realm = Realm.getDefaultInstance();

        initClassData();
        initToolBarLayout();
        initViewPager();
        initNavigationTabBar();
    }

    private void initClassData() {
        String myClassId = getIntent().getStringExtra(INTENT_KEY_MY_CLASS_ID);
        myClass = realm.where(MyClass.class)
                .equalTo(MyClass.KEY_ID, myClassId)
                .findFirst();
        studentRealmResults = realm.where(Student.class)
                .equalTo(Student.KEY_MY_CLASS_ID, myClassId)
                .findAll();
        studentRealmResults.addChangeListener(new RealmChangeListener<RealmResults<Student>>() {
            @Override
            public void onChange(RealmResults<Student> element) {
            }
        });
    }

    private void initToolBarLayout() {
        final CollapsingToolbarLayout collapsingToolbarLayout =
                (CollapsingToolbarLayout) findViewById(R.id.toolbar);
        collapsingToolbarLayout.setExpandedTitleColor(Color.parseColor("#009F90AF"));
        collapsingToolbarLayout.setCollapsedTitleTextColor(Color.parseColor("#9f90af"));
    }

    private void initViewPager() {
        binding.homeViewPager.setAdapter(new PagerAdapter() {
            @Override
            public int getCount() {
                return 5;
            }

            @Override
            public boolean isViewFromObject(final View view, final Object object) {
                return view.equals(object);
            }

            @Override
            public void destroyItem(final View container, final int position, final Object object) {
                ((ViewPager) container).removeView((View) object);
            }

            @Override
            public Object instantiateItem(final ViewGroup container, final int position) {
                final View view = LayoutInflater.from(
                        getBaseContext()).inflate(R.layout.item_home_myclass_pager, null, false);

                final RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.rv);
                recyclerView.setHasFixedSize(true);
                recyclerView.setLayoutManager(new LinearLayoutManager(
                                getBaseContext(), LinearLayoutManager.VERTICAL, false
                        )
                );
                recyclerView.setAdapter(new StudentListAdapter(
                        HomeMyClassActivity.this,
                        studentRealmResults)
                );
                container.addView(view);
                return view;
            }
        });
    }

    private void initNavigationTabBar() {
        final NavigationTabBar navigationTabBar = binding.homeNavigationTabBar;
        final ArrayList<NavigationTabBar.Model> models = new ArrayList<>();
        models.add(
                new NavigationTabBar.Model.Builder(
                        getResources().getDrawable(R.drawable.ic_face_black_48dp),
                        getResources().getColor(R.color.primary))
                        .title("Heart")
                        .build()
        );
        models.add(
                new NavigationTabBar.Model.Builder(
                        getResources().getDrawable(R.drawable.ic_date_range_black_48dp),
                        getResources().getColor(R.color.primary))
                        .title("Cup")
                        .build()
        );
        models.add(
                new NavigationTabBar.Model.Builder(
                        getResources().getDrawable(R.drawable.ic_home_black_48dp),
                        getResources().getColor(R.color.primary))
                        .title("Diploma")
                        .build()
        );
        models.add(
                new NavigationTabBar.Model.Builder(
                        getResources().getDrawable(R.drawable.ic_equalizer_black_48dp),
                        getResources().getColor(R.color.primary))
                        .title("Flag")
                        .build()
        );
        models.add(
                new NavigationTabBar.Model.Builder(
                        getResources().getDrawable(R.drawable.ic_settings_black_48dp),
                        getResources().getColor(R.color.primary))
                        .title("Medal")
                        .build()
        );

        navigationTabBar.setModels(models);
        navigationTabBar.setAnimationDuration(150);
        navigationTabBar.setViewPager(binding.homeViewPager, 2);
        navigationTabBar.setBehaviorEnabled(true);

        navigationTabBar.setOnTabBarSelectedIndexListener(new NavigationTabBar.OnTabBarSelectedIndexListener() {
            @Override
            public void onStartTabSelected(final NavigationTabBar.Model model, final int index) {}
            @Override
            public void onEndTabSelected(final NavigationTabBar.Model model, final int index) {
                model.hideBadge();
            }
        });

        navigationTabBar.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(final int position, final float positionOffset,
                                       final int positionOffsetPixels) {}
            @Override
            public void onPageSelected(final int position) {}
            @Override
            public void onPageScrollStateChanged(final int state) {}
        });
    }

    private void startFilePickerActivity() {
        // This always works
        Intent i = new Intent(HomeMyClassActivity.this, FilePickerActivity.class);
        // This works if you defined the intent filter
        // Intent i = new Intent(Intent.ACTION_GET_CONTENT);

        // Set these depending on your use case. These are the defaults.
        i.putExtra(FilePickerActivity.EXTRA_ALLOW_MULTIPLE, false);
        i.putExtra(FilePickerActivity.EXTRA_ALLOW_CREATE_DIR, false);
        i.putExtra(FilePickerActivity.EXTRA_MODE, FilePickerActivity.MODE_FILE);

        // Configure initial directory by specifying a String.
        // You could specify a String like "/storage/emulated/0/", but that can
        // dangerous. Always use Android's API calls to get paths to the SD-card or
        // internal memory.
        i.putExtra(FilePickerActivity.EXTRA_START_PATH,
                Environment.getExternalStorageDirectory().getPath());

        startActivityForResult(i, FILE_CODE);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == FILE_CODE && resultCode == Activity.RESULT_OK) {
            Uri uri = intent.getData();
            File file = com.nononsenseapps.filepicker.Utils.getFileForUri(uri);
            StudentUtil.insertDataToRealmFromCsvFile(HomeMyClassActivity.this, file, realm, myClass);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        studentRealmResults.removeAllChangeListeners();
        realm.close();
    }
}
