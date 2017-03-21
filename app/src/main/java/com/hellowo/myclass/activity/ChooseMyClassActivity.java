package com.hellowo.myclass.activity;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import com.bumptech.glide.Glide;
import com.hellowo.myclass.AppScreen;
import com.hellowo.myclass.R;
import com.hellowo.myclass.databinding.ActivityChooseClassBinding;
import com.hellowo.myclass.databinding.ItemClassPagerBinding;
import com.hellowo.myclass.model.MyClass;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;

import static com.hellowo.myclass.AppConst.INTENT_KEY_MY_CLASS_ID;

public class ChooseMyClassActivity extends AppCompatActivity {
    final static float PAGER_ITEM_DEFAULT_SCALE = 0.1f;
    private ActivityChooseClassBinding binding;
    private Realm realm;
    private RealmResults<MyClass> myClassRealmResults;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_choose_class);
        realm = Realm.getDefaultInstance();

        initClassData();
        initViewPager();
    }

    private void initClassData() {
        myClassRealmResults = realm.where(MyClass.class).findAll();
        myClassRealmResults.addChangeListener(new RealmChangeListener<RealmResults<MyClass>>() {
            @Override
            public void onChange(RealmResults<MyClass> element) {
                for(MyClass myClass : element){
                    Log.i("aaa", myClass.toString());
                }
                binding.classViewPager.getAdapter().notifyDataSetChanged();
            }
        });
    }

    private void initViewPager() {
        final float RATIO_SCALE = PAGER_ITEM_DEFAULT_SCALE;
        final ViewPager pager = binding.classViewPager;
        final ClassPagerAdapter adapter = new ClassPagerAdapter(this);

        pager.setAdapter(adapter);
        pager.setPageMargin(-AppScreen.dpToPx(80)); // 페이저 아이템들이 겹쳐보이게끔 설정
        pager.setOffscreenPageLimit(2); // 유지하는 페이지 오프셋 수
        pager.setHorizontalFadingEdgeEnabled(false); // 페이징 엣지 끄기
        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                View current_view = adapter.findCardViewAt(position);
                float scale = 1 - (positionOffset * RATIO_SCALE);
                current_view.setScaleX(scale);
                current_view.setScaleY(scale);
                if (position + 1 < adapter.getCount()) {
                    View next_view = adapter.findCardViewAt(position + 1);
                    scale = positionOffset * RATIO_SCALE + (1 - RATIO_SCALE);
                    next_view.setScaleX(scale);
                    next_view.setScaleY(scale);
                }
            }
            @Override
            public void onPageSelected(int position) {}
            @Override
            public void onPageScrollStateChanged(int state) {}
        });
    }

    private class ClassPagerAdapter extends PagerAdapter {
        private Map<Integer, ItemClassPagerBinding> bindingMap;
        private LayoutInflater inflater;

        private ClassPagerAdapter(Context c){
            super();
            bindingMap = new HashMap<>();
            inflater = LayoutInflater.from(c);
        }

        @Override
        public Object instantiateItem(View pager, int position) {
            final View v = inflater.inflate(R.layout.item_class_pager, null);
            ItemClassPagerBinding binding = DataBindingUtil.bind(v);

            if(position > 0){
                MyClass myClass = myClassRealmResults.get(position - 1);
                setMyClassCardView(binding, myClass);
            }else{
                setNewMyClassCardView(binding);
            }

            binding.classCard.setScaleX(1 - PAGER_ITEM_DEFAULT_SCALE);
            binding.classCard.setScaleY(1 - PAGER_ITEM_DEFAULT_SCALE);

            bindingMap.put(position, binding);
            ((ViewPager)pager).addView(v, 0);
            return v;
        }

        private void setMyClassCardView(ItemClassPagerBinding binding, final MyClass myClass) {
            binding.classCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(ChooseMyClassActivity.this, HomeMyClassActivity.class);
                    intent.putExtra(INTENT_KEY_MY_CLASS_ID, myClass.classId);
                    startActivity(intent);
                    finish();
                }
            });
            binding.schoolNameText.setText(myClass.schoolName);

            if(!TextUtils.isEmpty(myClass.classImageUri)){ // 이미지 경로가 있으면 로드함
                Glide.with(ChooseMyClassActivity.this)
                        .load(new File(myClass.classImageUri))
                        .into(binding.classImage);
            }
        }

        private void setNewMyClassCardView(ItemClassPagerBinding binding) {
            binding.classCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(
                            new Intent(ChooseMyClassActivity.this, EditMyClassActivity.class)
                    );
                }
            });
            binding.schoolNameText.setText("......");
        }

        @Override
        public void destroyItem(View pager, int position, Object view) {
            bindingMap.remove(position);
            ((ViewPager)pager).removeView((View)view);
        }

        @Override
        public int getCount() {
            return myClassRealmResults.size() + 1;
        }

        @Override
        public boolean isViewFromObject(View pager, Object obj) {
            return pager == obj;
        }

        @Override
        public void restoreState(Parcelable arg0, ClassLoader arg1) {}

        @Override
        public Parcelable saveState() { return null; }

        View findCardViewAt(int position) {
            return bindingMap.get(position).classCard;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        myClassRealmResults.removeAllChangeListeners();
        realm.close();
    }
}
