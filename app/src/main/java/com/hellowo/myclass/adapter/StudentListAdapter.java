package com.hellowo.myclass.adapter;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hellowo.myclass.R;
import com.hellowo.myclass.activity.StudentActivity;
import com.hellowo.myclass.databinding.ItemStudentListBinding;
import com.hellowo.myclass.model.Student;

import io.realm.OrderedRealmCollection;
import io.realm.RealmRecyclerViewAdapter;

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
        holder.binding.studentImage.setColorFilter(activity.getResources().getColor(R.color.primary));

        if(position == currentExpandedItemPosition){
            Log.i("aaa", "currentExpandedItemPosition " + currentExpandedItemPosition);
            holder.binding.expandableMenuLayout.setVisibility(View.VISIBLE);
        }else{
            holder.binding.expandableMenuLayout.setVisibility(View.GONE);
        }

        holder.binding.studentImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity, StudentActivity.class);
                intent.putExtra(StudentActivity.INTENT_KEY_STUDENT_ID, student.studentId);
                activity.startActivity(intent);
            }
        });

        holder.binding.itemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                itemClicked(holder.binding, position);
            }
        });
    }

    private void itemClicked(ItemStudentListBinding binding, final int position) {
        if(currentExpandedItemPosition > 0){
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
