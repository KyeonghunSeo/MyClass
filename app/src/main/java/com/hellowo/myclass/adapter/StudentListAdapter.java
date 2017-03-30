package com.hellowo.myclass.adapter;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.hellowo.myclass.R;
import com.hellowo.myclass.activity.StudentActivity;
import com.hellowo.myclass.databinding.ItemStudentListBinding;
import com.hellowo.myclass.model.Event;
import com.hellowo.myclass.model.Student;

import java.io.File;

import io.realm.OrderedRealmCollection;
import io.realm.Realm;
import io.realm.RealmRecyclerViewAdapter;
import jp.wasabeef.glide.transformations.CropCircleTransformation;

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
                /*
                ActivityOptionsCompat options = ActivityOptionsCompat.
                        makeSceneTransitionAnimation(activity, view, "studentImage");
                activity.startActivity(intent, options.toBundle());
                */
                activity.startActivity(intent);
            }
        });

        holder.binding.itemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                itemClicked(position);
            }
        });

        holder.binding.announcementButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addEvent(student, Event.TYPE_ANNOUNCEMENT);
                itemClicked(position);
            }
        });

        holder.binding.thumbsUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addEvent(student, Event.TYPE_THUMBS_UP);
                itemClicked(position);
            }
        });

        holder.binding.thumbsDownButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addEvent(student, Event.TYPE_THUMBS_DOWN);
                itemClicked(position);
            }
        });

        setStudentImage(holder.binding, student);
    }

    private void addEvent(final Student student, final int type) {
        Realm realm = Realm.getDefaultInstance();
        try {
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    Event event = Event.creatNewEvent();
                    event.type = type;
                    event.students.add(student);
                    realm.copyToRealmOrUpdate(event);
                    Toast.makeText(activity,
                            student.name + " " + event.getTypeTitle(), Toast.LENGTH_SHORT).show();
                }
            });
        } finally {
            realm.close();
        }
    }

    private void setStudentImage(ItemStudentListBinding binding, Student student) {
        if(!TextUtils.isEmpty(student.profileImageUri)){ // 이미지 경로가 있으면 로드함
            Glide.with(activity)
                    .load(new File(student.profileImageUri))
                    .bitmapTransform(new CropCircleTransformation(activity))
                    .into(binding.studentImage);
        }else{
            binding.studentImage.setImageResource(R.drawable.default_profile_circle);
        }
    }

    private void itemClicked(final int position) {
        int prevPosition = currentExpandedItemPosition;
        currentExpandedItemPosition = position;

        if(prevPosition == currentExpandedItemPosition) {

            currentExpandedItemPosition = -1;
            notifyItemChanged(prevPosition);

        }else {

            if(prevPosition >= 0){
                notifyItemChanged(prevPosition);
            }
            notifyItemChanged(currentExpandedItemPosition);

        }
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
