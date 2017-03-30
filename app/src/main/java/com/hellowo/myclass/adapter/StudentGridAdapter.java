package com.hellowo.myclass.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.hellowo.myclass.R;
import com.hellowo.myclass.model.Student;

import java.io.File;
import java.util.Map;

import io.realm.OrderedRealmCollection;
import io.realm.RealmRecyclerViewAdapter;

public class StudentGridAdapter
        extends RealmRecyclerViewAdapter<Student, StudentGridAdapter.MyViewHolder> {
    Context context;
    Map<String, Student> selectedMap;

    public StudentGridAdapter(Context context, OrderedRealmCollection<Student> data,
                              Map<String, Student> selectedMap) {
        super(data, true);
        this.context = context;
        this.selectedMap = selectedMap;
        setHasStableIds(true);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public View rootView;
        public TextView nameText;
        public ImageView studentImage;
        public FrameLayout selectedImage;

        public MyViewHolder(View container) {
            super(container);
            this.rootView = container;
            this.nameText = (TextView) container.findViewById(R.id.nameText);
            this.studentImage = (ImageView) container.findViewById(R.id.studentImage);
            this.selectedImage = (FrameLayout) container.findViewById(R.id.selectedImage);
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_student_grid, parent, false);
        MyViewHolder vh = new MyViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final Student student = getItem(position);

        holder.nameText.setText(student.name);

        if(!TextUtils.isEmpty(student.profileImageUri)) { // 이미지 경로가 있으면 로드함

            Glide.with(context)
                    .load(new File(student.profileImageUri))
                    .into(holder.studentImage);

        }

        setSelectedLayout(holder, student);

        holder.rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(selectedMap.containsKey(student.studentId)) {
                    selectedMap.remove(student.studentId);
                }else{
                    selectedMap.put(student.studentId, student);
                }
                setSelectedLayout(holder, student);

            }
        });

    }

    private void setSelectedLayout(MyViewHolder holder, Student student) {
        if(selectedMap.containsKey(student.studentId)) {
            holder.selectedImage.setVisibility(View.VISIBLE);
        }else{
            holder.selectedImage.setVisibility(View.GONE);
        }
    }

    public Map<String,Student> getSelectedMap() {
        return selectedMap;
    }

    @Override
    public long getItemId(int index) {
        return getItem(index).number;
    }
}