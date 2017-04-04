package com.hellowo.myclass.adapter;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hellowo.myclass.AppConst;
import com.hellowo.myclass.AppDateFormat;
import com.hellowo.myclass.AppScreen;
import com.hellowo.myclass.R;
import com.hellowo.myclass.activity.EventActivity;
import com.hellowo.myclass.databinding.ItemEventListBinding;
import com.hellowo.myclass.model.Event;

import java.util.Date;

import io.realm.OrderedRealmCollection;
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
        holder.binding.typeText.setText(event.getTypeTitle());
        holder.binding.typeImage.setImageResource(event.getTypeIconId());
        holder.binding.dateText.setText(AppDateFormat.smallmdeDate.format(new Date(event.dtStart)));

        if(!TextUtils.isEmpty(event.description)) {
            holder.binding.memoText.setVisibility(View.VISIBLE);
            holder.binding.memoText.setText(event.description);
        }else{
            holder.binding.memoText.setVisibility(View.GONE);
        }

        holder.binding.studentChipView.makeStudentChips(event.students);
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
