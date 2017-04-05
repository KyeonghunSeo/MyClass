package com.hellowo.myclass.adapter;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.hellowo.myclass.AppConst;
import com.hellowo.myclass.AppDateFormat;
import com.hellowo.myclass.AppScreen;
import com.hellowo.myclass.R;
import com.hellowo.myclass.activity.EventActivity;
import com.hellowo.myclass.databinding.ItemEventListBinding;
import com.hellowo.myclass.model.Event;
import com.hellowo.myclass.model.Student;

import java.util.Date;
import java.util.UUID;

import io.realm.OrderedRealmCollection;
import io.realm.Realm;
import io.realm.RealmRecyclerViewAdapter;

import static com.hellowo.myclass.AppConst.INTENT_KEY_MY_CLASS_ID;

public class EventListAdapter
        extends RealmRecyclerViewAdapter<Event, EventListAdapter.MyViewHolder> {

    Activity activity;
    private String classId;

    public EventListAdapter(Activity activity, OrderedRealmCollection<Event> data, String classId) {
        super(data, true);
        this.activity = activity;
        this.classId = classId;
        setHasStableIds(true);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_event_list, parent, false);
        ItemEventListBinding binding = DataBindingUtil.bind(itemView);
        return new MyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final Event event = getItem(position);

        holder.binding.doneCheck.setOnCheckedChangeListener(null);

        if(event.isTeachersEvent()) {

            if(event.isTodo()) {
                holder.binding.doneCheck.setVisibility(View.VISIBLE);
                holder.binding.doneCheck.setChecked(event.isDone());
                holder.binding.doneCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                        doneTodo(event, checked);
                    }
                });

            } else{
                holder.binding.doneCheck.setVisibility(View.GONE);
            }

            if(event.isDone()) {
                holder.binding.typeText.setPaintFlags(
                        holder.binding.typeText.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            }else {
                holder.binding.typeText.setPaintFlags(
                        holder.binding.typeText.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
            }

            holder.binding.typeText.setText(event.getEventTitle());
            holder.binding.dateText.setText(event.getDateText());

            holder.binding.typeImage.setVisibility(View.GONE);

            holder.binding.memoText.setVisibility(View.GONE);

            holder.binding.studentChipView.setVisibility(View.GONE);

        }else{
            holder.binding.typeText.setPaintFlags(
                    holder.binding.typeText.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
            holder.binding.doneCheck.setVisibility(View.GONE);

            holder.binding.typeText.setText(event.getTypeTitle());
            holder.binding.dateText.setText(event.getDateText());

            holder.binding.typeImage.setVisibility(View.VISIBLE);
            holder.binding.typeImage.setImageResource(event.getTypeIconId());

            if(!TextUtils.isEmpty(event.description)) {
                holder.binding.memoText.setVisibility(View.VISIBLE);
                holder.binding.memoText.setText(event.description);
            }else{
                holder.binding.memoText.setVisibility(View.GONE);
            }

            holder.binding.studentChipView.setVisibility(View.VISIBLE);
            holder.binding.studentChipView.makeStudentChips(event.students);
        }
    }

    private void doneTodo(final Event event, final boolean isDone) {
        Realm realm = Realm.getDefaultInstance();
        try {
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    if(isDone){
                        event.dtDone = System.currentTimeMillis();
                        Toast.makeText(activity,
                                event.getEventTitle() + " " + activity.getString(R.string.done),
                                Toast.LENGTH_SHORT).show();
                    }else{
                        event.dtDone = 0;
                        Toast.makeText(activity,
                                event.getEventTitle() + " " + activity.getString(R.string.undone),
                                Toast.LENGTH_SHORT).show();
                    }
                    realm.copyToRealmOrUpdate(event);
                }
            });
        } finally {
            realm.close();
        }
    }

    @Override
    public long getItemId(int index) {
        return getItem(index).hashCode();
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        ItemEventListBinding binding;

        MyViewHolder(ItemEventListBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            binding.studentChipView.setChipSize(AppScreen.dpToPx(25));
            binding.studentChipView.setMaxWidth(AppScreen.dpToPx(150));
            binding.itemButton.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            Event event = getItem(getAdapterPosition());
            Intent intent = new Intent(activity, EventActivity.class);
            intent.putExtra(AppConst.INTENT_KEY_EVENT_ID, event.eventId);
            intent.putExtra(INTENT_KEY_MY_CLASS_ID, classId);
            activity.startActivity(intent);
        }
    }
}
