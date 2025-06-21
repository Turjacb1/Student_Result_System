
package com.turja.student_result;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import com.turja.student_result.R;
import androidx.viewbinding.ViewBinding;

public final class ActivityTotalStudentBinding implements ViewBinding {
    @NonNull
    public final RecyclerView rvStudents;
    @NonNull
    public final TextView textView;
    @NonNull
    private final LinearLayout rootView;

    private ActivityTotalStudentBinding(@NonNull LinearLayout rootView, @NonNull RecyclerView rvStudents, @NonNull TextView textView) {
        this.rootView = rootView;
        this.rvStudents = rvStudents;
        this.textView = textView;
    }

    @NonNull
    public static ActivityTotalStudentBinding inflate(@NonNull LayoutInflater inflater) {
        return inflate(inflater, null, false);
    }

    @NonNull
    public static ActivityTotalStudentBinding inflate(@NonNull LayoutInflater inflater, @Nullable ViewGroup parent, boolean attachToParent) {
        View root = inflater.inflate(R.layout.activity_total_student, parent, false);
        if (attachToParent) {
            parent.addView(root);
        }
        return bind(root);
    }

    @NonNull
    public static ActivityTotalStudentBinding bind(@NonNull View rootView) {
        // The body would contain the binding logic for all views with IDs
        int id;
        RecyclerView rvStudents = rootView.findViewById(R.id.rvStudents);
        if (rvStudents == null) {
            throw new NullPointerException("Missing required view with ID: rvStudents");
        }

        TextView textView = rootView.findViewById(R.id.textView); // Auto-generated ID for the TextView
        if (textView == null) {
            throw new NullPointerException("Missing required view with ID: textView");
        }

        return new ActivityTotalStudentBinding((LinearLayout) rootView, rvStudents, textView);
    }

    @NonNull
    public LinearLayout getRoot() {
        return rootView;
    }
}