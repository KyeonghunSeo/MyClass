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
import com.hellowo.myclass.databinding.DialogChooseStudentBinding;
import com.hellowo.myclass.model.Student;

import java.util.Map;

import io.realm.Realm;
import io.realm.RealmResults;

public class SelectStudentDialog extends Dialog {
    DialogChooseStudentBinding binding;
    private Realm realm;
    private RealmResults<Student> studentRealmResults;
    private Map<String, Student> selectedMap;
    private StudentGridAdapter studentGridAdapter;
    private ChooseStudentInterface chooseStudentInterface;

    public SelectStudentDialog(Context context, String classId, Map<String, Student> selectedMap,
                               ChooseStudentInterface chooseStudentInterface) {
        super(context);
        realm = Realm.getDefaultInstance();
        studentRealmResults = realm.where(Student.class)
                .equalTo(Student.KEY_MY_CLASS_ID, classId)
                .findAllSorted(Student.KEY_NUMBER);
        this.selectedMap = selectedMap;
        this.chooseStudentInterface = chooseStudentInterface;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.inflate(
                LayoutInflater.from(getContext()), R.layout.dialog_select_student, null, false);
        setContentView(binding.getRoot());

        binding.recyclerView.setHasFixedSize(true);
        binding.recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 5));
        studentGridAdapter = new StudentGridAdapter(getContext(), studentRealmResults, selectedMap);
        binding.recyclerView.setAdapter(studentGridAdapter);

        binding.okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(chooseStudentInterface != null){
                    chooseStudentInterface.onSelected(studentGridAdapter.getSelectedMap());
                }
                dismiss();
            }
        });

        binding.cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
    }

    @Override
    public void dismiss() {
        super.dismiss();
        realm.close();
    }

    public interface ChooseStudentInterface{
        public void onSelected(Map<String, Student> selectedMap);
    }
}
