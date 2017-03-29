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
import com.hellowo.myclass.model.Student;

import java.util.Map;

import io.realm.Realm;
import io.realm.RealmResults;

public class SelectEventTypeDialog extends Dialog {
    DialogSelectEventTypeBinding binding;
    private SelectEventTypeInterface selectEventTypeInterface;

    public SelectEventTypeDialog(Context context, SelectEventTypeInterface selectEventTypeInterface) {
        super(context);
        this.selectEventTypeInterface = selectEventTypeInterface;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.inflate(
                LayoutInflater.from(getContext()), R.layout.dialog_select_event_type, null, false);
        setContentView(binding.getRoot());

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
