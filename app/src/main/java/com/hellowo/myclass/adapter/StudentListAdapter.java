package com.hellowo.myclass.adapter;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.hellowo.myclass.R;
import com.hellowo.myclass.activity.StudentActivity;
import com.hellowo.myclass.databinding.ItemStudentListBinding;
import com.hellowo.myclass.model.Student;

import java.io.File;

import io.realm.OrderedRealmCollection;
import io.realm.RealmRecyclerViewAdapter;

import static com.hellowo.myclass.AppConst.INTENT_KEY_STUDENT_ID;

public class StudentListAdapter
        extends RealmRecyclerViewAdapter<Student, StudentListAdapter.MyViewHolder> {
    Activity activity;
    int currentExpandedItemPosition = -1;

    public StudentListAdapter(Activity activity, OrderedRealmCollection<Student> data) {
        super(data, true);
        this.activity = activity;
        setHasStableIds(true);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_student_list, parent, false);
        ItemStudentListBinding binding = DataBindingUtil.bind(itemView);
        return new MyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final Student student = getItem(position);
        holder.binding.nameText.setText(student.name);

        if(position == currentExpandedItemPosition){
            holder.binding.expandableMenuLayout.setVisibility(View.VISIBLE);
        }else{
            holder.binding.expandableMenuLayout.setVisibility(View.GONE);
        }

        holder.binding.studentImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity, StudentActivity.class);
                intent.putExtra(INTENT_KEY_STUDENT_ID, student.studentId);
                activity.startActivity(intent);
            }
        });

        holder.binding.itemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                itemClicked(position);
            }
        });

        setStudentImage(holder.binding, student);
    }

    private void setStudentImage(ItemStudentListBinding binding, Student student) {
        if(!TextUtils.isEmpty(student.profileImageUri)){ // 이미지 경로가 있으면 로드함
            Glide.with(activity)
                    .load(new File(student.profileImageUri))
                    .into(binding.studentImage);
        }else{
            binding.studentImage.setImageResource(R.drawable.default_profile_circle);
        }
    }

    private void itemClicked(final int position) {
        if(currentExpandedItemPosition >= 0){
            notifyItemChanged(currentExpandedItemPosition);
        }
        currentExpandedItemPosition = position;
        notifyItemChanged(currentExpandedItemPosition);
    }

    @Override
    public long getItemId(int index) {
        return getItem(index).number;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        ItemStudentListBinding binding;

        MyViewHolder(ItemStudentListBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
