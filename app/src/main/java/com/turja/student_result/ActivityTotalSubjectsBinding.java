package com.turja.student_result;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;

import com.turja.student_result.R;

public final class ActivityTotalSubjectsBinding implements ViewBinding {
    @NonNull
    private final LinearLayout rootView;
    @NonNull
    public final RecyclerView rvSubjects;

    private ActivityTotalSubjectsBinding(@NonNull LinearLayout rootView, @NonNull RecyclerView rvSubjects) {
        this.rootView = rootView;
        this.rvSubjects = rvSubjects;
    }

    @Override
    @NonNull
    public LinearLayout getRoot() {
        return rootView;
    }

    @NonNull
    public static ActivityTotalSubjectsBinding inflate(@NonNull LayoutInflater inflater) {
        return inflate(inflater, null, false);
    }

    @NonNull
    public static ActivityTotalSubjectsBinding inflate(@NonNull LayoutInflater inflater, @Nullable ViewGroup parent, boolean attachToParent) {
        View root = inflater.inflate(R.layout.activity_total_subjects, parent, false);
        if (attachToParent) {
            parent.addView(root);
        }
        return bind(root);
    }

    @NonNull
    public static ActivityTotalSubjectsBinding bind(@NonNull View rootView) {
        int id;
        missingId: {
            id = R.id.rvSubjects;
            RecyclerView rvSubjects = ViewBindings.findChildViewById(rootView, id);
            if (rvSubjects == null) {
                break missingId;
            }
            return new ActivityTotalSubjectsBinding((LinearLayout) rootView, rvSubjects);
        }
        String missingId = rootView.getResources().getResourceName(id);
        throw new NullPointerException("Missing required view with ID: ".concat(missingId));
    }
}