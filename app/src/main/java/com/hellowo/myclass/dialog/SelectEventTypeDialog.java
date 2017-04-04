package com.hellowo.myclass.dialog;

import android.app.Dialog;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;

import com.hellowo.myclass.R;
import com.hellowo.myclass.adapter.StudentGridAdapter;
import com.hellowo.myclass.databinding.DialogSelectEventTypeBinding;
import com.hellowo.myclass.databinding.DialogSelectStudentBinding;
import com.hellowo.myclass.model.Event;
import com.hellowo.myclass.model.Student;

import java.util.Map;

import io.realm.Realm;
import io.realm.RealmResults;

public class SelectEventTypeDialog extends Dialog {
    DialogSelectEventTypeBinding binding;
    private SelectEventTypeInterface selectEventTypeInterface;
    private int type;

    public SelectEventTypeDialog(Context context, SelectEventTypeInterface selectEventTypeInterface,
                                 int type) {
        super(context);
        this.selectEventTypeInterface = selectEventTypeInterface;
        this.type = type;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.inflate(
                LayoutInflater.from(getContext()), R.layout.dialog_select_event_type, null, false);
        setContentView(binding.getRoot());

        if(type >= Event.TYPE_EVENT) {
            for(int i = 0; i < Event.TYPE_EVENT; i++) {
                binding.root.getChildAt(i).setVisibility(View.GONE);
            }
        } else{
            for(int i = Event.TYPE_EVENT; i < binding.root.getChildCount(); i++) {
                binding.root.getChildAt(i).setVisibility(View.GONE);
            }
        }

        for(int i = 0; i < binding.root.getChildCount(); i++) {
            final int type = i;
            binding.root.getChildAt(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(selectEventTypeInterface != null) {
                        selectEventTypeInterface.onSelected(type);
                    }
                    dismiss();
                }
            });
        }
    }

    @Override
    public void dismiss() {
        super.dismiss();
    }

    public interface SelectEventTypeInterface{
        public void onSelected(int type);
    }
}
