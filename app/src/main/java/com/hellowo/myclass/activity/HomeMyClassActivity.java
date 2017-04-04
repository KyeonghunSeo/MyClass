package com.hellowo.myclass.activity;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.flaviofaria.kenburnsview.KenBurnsView;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.Entry;
import com.hellowo.myclass.AppConst;
import com.hellowo.myclass.AppDateFormat;
import com.hellowo.myclass.AppScreen;
import com.hellowo.myclass.R;
import com.hellowo.myclass.adapter.EventListAdapter;
import com.hellowo.myclass.adapter.StudentListAdapter;
import com.hellowo.myclass.databinding.ActivityHomeClassBinding;
import com.hellowo.myclass.model.Event;
import com.hellowo.myclass.model.MyClass;
import com.hellowo.myclass.model.Student;
import com.hellowo.myclass.utils.CalendarUtil;
import com.hellowo.myclass.utils.StudentUtil;
import com.hellowo.myclass.view.CalendarView;
import com.nononsenseapps.filepicker.FilePickerActivity;
import com.pixplicity.easyprefs.library.Prefs;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import devlight.io.library.ntb.NavigationTabBar;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import io.realm.Sort;
import jp.wasabeef.glide.transformations.CropCircleTransformation;

import static com.hellowo.myclass.AppConst.INTENT_KEY_EVENT_TYPE;
import static com.hellowo.myclass.AppConst.INTENT_KEY_MY_CLASS_ID;
import static com.hellowo.myclass.AppConst.INTENT_KEY_STUDENT_ID;

public class HomeMyClassActivity extends AppCompatActivity {
    public final static int FILE_CODE = 8250;
    private ActivityHomeClassBinding binding;
    private Realm realm;
    private MyClass myClass;
    private RealmResults<Student> studentRealmResults;
    private RealmResults<Event> calendarEventRealmResults;
    private RealmResults<Event> homeEventRealmResults;
    private RealmResults<Event> homeTodoRealmResults;
    private Calendar calendar = Calendar.getInstance();
    private KenBurnsView classImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_home_class);
        realm = Realm.getDefaultInstance();

        initClassData();
        initViewPager();
        initNavigationTabBar();
        initFabEvent();
    }

    private void initFabEvent() {
        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(binding.homeNavigationTabBar.getModelIndex() == 0) {
                    Intent intent = new Intent(HomeMyClassActivity.this, EventActivity.class);
                    intent.putExtra(INTENT_KEY_EVENT_TYPE, Event.TYPE_COMMENT);
                    intent.putExtra(INTENT_KEY_MY_CLASS_ID, myClass.classId);
                    startActivity(intent);
                }else {
                    Intent intent = new Intent(HomeMyClassActivity.this, EventActivity.class);
                    intent.putExtra(INTENT_KEY_EVENT_TYPE, Event.TYPE_EVENT);
                    intent.putExtra(INTENT_KEY_MY_CLASS_ID, myClass.classId);
                    startActivity(intent);
                }
            }
        });
    }

    private void initClassData() {
        String myClassId = getIntent().getStringExtra(INTENT_KEY_MY_CLASS_ID);

        myClass = realm.where(MyClass.class)
                .equalTo(MyClass.KEY_ID, myClassId)
                .findFirst();

        studentRealmResults = realm.where(Student.class)
                .equalTo(Student.KEY_MY_CLASS_ID, myClassId)
                .findAllSorted(Student.KEY_NUMBER);

        myClass.addChangeListener(new RealmChangeListener<MyClass>() {
            @Override
            public void onChange(MyClass element) {
                setPageLayout(binding.homeNavigationTabBar.getModelIndex());
                setClassImage();
            }
        });
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
                View view;

                switch (position){
                    case 0:
                        view = createStudentListView();
                        break;
                    case 1:
                        view = createCalendarView();
                        break;
                    case 2:
                        view = createHomeView();
                        break;
                    case 3:
                        view = createGraphView();
                        break;
                    case 4:
                        view = createSettingsView();
                        break;
                    default:
                        view = new View(HomeMyClassActivity.this);
                        break;
                }

                container.addView(view);
                return view;
            }
        });
    }

    private View createStudentListView() {
        final View view = LayoutInflater.from(
                getBaseContext()).inflate(R.layout.item_pager_student_list, null, false);
        final RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.rv);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(
                new LinearLayoutManager(
                        getBaseContext(),
                        LinearLayoutManager.VERTICAL,
                        false)
        );
        recyclerView.setAdapter(new StudentListAdapter(
                HomeMyClassActivity.this,
                studentRealmResults)
        );
        return view;
    }

    private View createCalendarView() {
        final View view = LayoutInflater.from(
                getBaseContext()).inflate(R.layout.item_pager_calendar, null, false);

        final RecyclerView recyclerView = (RecyclerView)view.findViewById(R.id.dailyRecyclerView);
        recyclerView.setLayoutManager(
                new LinearLayoutManager(
                        getBaseContext(),
                        LinearLayoutManager.VERTICAL,
                        false)
        );

        final CalendarView calendarView = (CalendarView)view.findViewById(R.id.calendarView);
        final Calendar calendar = Calendar.getInstance();

        calendarView.setCalendarInterface(new CalendarView.CalendarInterface() {
            @Override
            public void onClicked(Calendar clickedCal) {

                CalendarUtil.setCalendarTime0(clickedCal);
                long startTime = clickedCal.getTimeInMillis();

                CalendarUtil.setCalendarTime23(clickedCal);
                long endTime = clickedCal.getTimeInMillis();

                RealmResults<Event> eventRealmResults = realm.where(Event.class)
                        .greaterThanOrEqualTo(Event.KEY_DT_END, startTime)
                        .lessThanOrEqualTo(Event.KEY_DT_START, endTime)
                        .findAllSorted(Event.KEY_DT_START, Sort.DESCENDING);

                recyclerView.setAdapter(new EventListAdapter(
                        HomeMyClassActivity.this,
                        eventRealmResults,
                        myClass.classId)
                );

            }
        });
        calendarView.init(this, calendar, realm);

        binding.prevMonthButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calendarView.prevMonth();
                binding.topTitleText.setText(AppDateFormat.ym.format(calendar.getTime()));
            }
        });

        binding.nextMonthButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calendarView.nextMonth();
                binding.topTitleText.setText(AppDateFormat.ym.format(calendar.getTime()));
            }
        });

        return view;
    }

    private View createHomeView() {
        final View view = LayoutInflater.from(
                getBaseContext()).inflate(R.layout.item_pager_home, null, false);
        classImage = (KenBurnsView)view.findViewById(R.id.classImage);

        classImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeMyClassActivity.this, EditMyClassActivity.class);
                intent.putExtra(AppConst.INTENT_KEY_MY_CLASS_ID, myClass.classId);
                startActivity(intent);
            }
        });

        setClassImage();

        return view;
    }

    private void setClassImage() {
        if(classImage != null) {

            if(!TextUtils.isEmpty(myClass.classImageUri)) {

                Glide.with(this)
                        .load(new File(myClass.classImageUri))
                        .into(classImage);

            }else {

                Glide.with(this)
                        .load(R.drawable.default_class_img)
                        .into(classImage);

            }

            classImage.restart();

        }
    }

    private View createGraphView() {
        final View view = LayoutInflater.from(
                getBaseContext()).inflate(R.layout.item_pager_graph, null, false);

        BarChart barChart = (BarChart) view.findViewById(R.id.barChart);

        List<Entry> entries = new ArrayList<Entry>();


        return view;
    }

    private View createSettingsView() {
        final View view = LayoutInflater.from(
                getBaseContext()).inflate(R.layout.item_pager_settings, null, false);
        Button getStudentsFromCSVButton = (Button) view.findViewById(R.id.getStudentsFromCSVButton);

        getStudentsFromCSVButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startFilePickerActivity();
            }
        });

        return view;
    }

    private void initNavigationTabBar() {
        final NavigationTabBar navigationTabBar = binding.homeNavigationTabBar;
        final ArrayList<NavigationTabBar.Model> models = new ArrayList<>();
        models.add(
                new NavigationTabBar.Model.Builder(
                        getResources().getDrawable(R.drawable.ic_face_black_48dp),
                        getResources().getColor(R.color.primary))
                        .build()
        );
        models.add(
                new NavigationTabBar.Model.Builder(
                        getResources().getDrawable(R.drawable.ic_date_range_black_48dp),
                        getResources().getColor(R.color.primary))
                        .build()
        );
        models.add(
                new NavigationTabBar.Model.Builder(
                        getResources().getDrawable(R.drawable.ic_home_black_48dp),
                        getResources().getColor(R.color.primary))
                        .build()
        );
        models.add(
                new NavigationTabBar.Model.Builder(
                        getResources().getDrawable(R.drawable.ic_equalizer_black_48dp),
                        getResources().getColor(R.color.primary))
                        .build()
        );
        models.add(
                new NavigationTabBar.Model.Builder(
                        getResources().getDrawable(R.drawable.ic_settings_black_48dp),
                        getResources().getColor(R.color.primary))
                        .build()
        );

        navigationTabBar.setModels(models);
        navigationTabBar.setInactiveColor(getResources().getColor(R.color.primary));
        navigationTabBar.setAnimationDuration(75);
        navigationTabBar.setViewPager(binding.homeViewPager, 2);
        navigationTabBar.setBehaviorEnabled(true);

        navigationTabBar.setOnTabBarSelectedIndexListener(new NavigationTabBar.OnTabBarSelectedIndexListener() {
            @Override
            public void onStartTabSelected(final NavigationTabBar.Model model, final int index) {}
            @Override
            public void onEndTabSelected(final NavigationTabBar.Model model, final int index) {}
        });

        navigationTabBar.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(final int position, final float positionOffset,
                                       final int positionOffsetPixels) {}
            @Override
            public void onPageSelected(final int position) {
                setPageLayout(position);
            }
            @Override
            public void onPageScrollStateChanged(final int state) {}
        });

        setPageLayout(2);
    }

    private void setPageLayout(int position) {
        switch (position) {
            case 0:
                binding.topTitleText.setText(R.string.student_list);
                binding.nextMonthButton.setVisibility(View.GONE);
                binding.prevMonthButton.setVisibility(View.GONE);
                binding.fab.show();
                break;
            case 1:
                binding.topTitleText.setText(AppDateFormat.ym.format(calendar.getTime()));
                binding.nextMonthButton.setVisibility(View.VISIBLE);
                binding.prevMonthButton.setVisibility(View.VISIBLE);
                binding.fab.show();
                break;
            case 2:
                binding.topTitleText.setText(myClass.getClassTitle());
                binding.nextMonthButton.setVisibility(View.GONE);
                binding.prevMonthButton.setVisibility(View.GONE);
                binding.fab.show();
                break;
            case 3:
                binding.topTitleText.setText(R.string.analytics);
                binding.nextMonthButton.setVisibility(View.GONE);
                binding.prevMonthButton.setVisibility(View.GONE);
                binding.fab.hide();
                break;
            case 4:
                binding.topTitleText.setText(R.string.settings);
                binding.nextMonthButton.setVisibility(View.GONE);
                binding.prevMonthButton.setVisibility(View.GONE);
                binding.fab.hide();
                break;
            default:
                break;
        }
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
            binding.homeNavigationTabBar.setModelIndex(0);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        Prefs.putString(AppConst.KEY_LAST_MY_CLASS_ID, myClass.classId);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        myClass.removeAllChangeListeners();
        realm.close();
    }
}
