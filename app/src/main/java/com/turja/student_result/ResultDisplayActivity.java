//package com.turja.student_result;
//
//import android.os.Bundle;
//import android.util.Log;
//import android.widget.LinearLayout;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.cardview.widget.CardView;
//
//import com.google.firebase.firestore.FirebaseFirestore;
//import com.google.firebase.firestore.QueryDocumentSnapshot;
//import com.google.firebase.firestore.QuerySnapshot;
//
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.Comparator;
//import java.util.HashMap;
//
//public class ResultDisplayActivity extends AppCompatActivity {
//
//    private static final String TAG = "ResultDisplayActivity";
//    private FirebaseFirestore db;
//    private String studentId;
//    private String department;
//    private boolean isAdmin;
//    private LinearLayout resultContainer;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_result_display);
//
//        db = FirebaseFirestore.getInstance();
//        resultContainer = findViewById(R.id.resultContainer);
//
//        // Get intent extras
//        studentId = getIntent().getStringExtra("studentId");
//        department = getIntent().getStringExtra("department");
//        isAdmin = getIntent().getBooleanExtra("isAdmin", false);
//
//        Log.d(TAG, "Received studentId: " + studentId + ", department: " + department + ", isAdmin: " + isAdmin);
//
//        if (isAdmin || (studentId != null && department != null)) {
//            fetchResults();
//        } else {
//            Toast.makeText(this, "Invalid data", Toast.LENGTH_SHORT).show();
//            finish();
//        }
//    }
//
//    private void fetchResults() {
//        db.collection("results")
//                .get() // Fetch all documents for debugging and admin mode
//                .addOnSuccessListener(querySnapshot -> {
//                    Log.d(TAG, "Total documents in collection: " + querySnapshot.size());
//                    ArrayList<HashMap<String, Object>> results = new ArrayList<>();
//                    for (QueryDocumentSnapshot document : querySnapshot) {
//                        HashMap<String, Object> result = new HashMap<>(document.getData());
//                        Log.d(TAG, "Document ID: " + document.getId() + ", Data: " + result.toString());
//                        // Filter for student mode if not admin
//                        if (!isAdmin) {
//                            String resultStudentId = (String) result.get("studentId");
//                            String resultDepartment = (String) result.get("department");
//                            if (resultStudentId != null && resultDepartment != null &&
//                                    resultStudentId.equals(studentId) && resultDepartment.equals(department)) {
//                                results.add(result);
//                                Log.d(TAG, "Matched document for studentId: " + studentId + ", department: " + department);
//                            }
//                        } else {
//                            results.add(result); // Add all results for admin
//                        }
//                    }
//
//                    if (results.isEmpty() && !isAdmin) {
//                        Log.w(TAG, "No results found for studentId: " + studentId + ", department: " + department);
//                        Toast.makeText(this, "No results found for this student.", Toast.LENGTH_SHORT).show();
//                    }
//
//                    // Sort results by timestamp (earliest to latest)
//                    Collections.sort(results, new Comparator<HashMap<String, Object>>() {
//                        @SuppressWarnings("unchecked")
//                        @Override
//                        public int compare(HashMap<String, Object> r1, HashMap<String, Object> r2) {
//                            Long t1 = (Long) r1.get("timestamp");
//                            Long t2 = (Long) r2.get("timestamp");
//                            if (t1 == null || t2 == null) {
//                                Log.w(TAG, "Timestamp missing or invalid in one or both documents");
//                                return 0;
//                            }
//                            return t1.compareTo(t2);
//                        }
//                    });
//
//                    HashMap<String, HashMap<String, ArrayList<HashMap<String, Object>>>> resultsByYearSemester = new HashMap<>();
//                    for (HashMap<String, Object> result : results) {
//                        String year = (String) result.get("year");
//                        String semester = (String) result.get("semester");
//                        @SuppressWarnings("unchecked")
//                        ArrayList<HashMap<String, String>> subjects = (ArrayList<HashMap<String, String>>) result.get("subjects");
//                        String name = (String) result.get("name");
//                        String resultStudentId = (String) result.get("studentId");
//                        String registrationNumber = (String) result.get("registrationNumber");
//                        String session = (String) result.get("session");
//                        String resultDepartment = (String) result.get("department");
//                        String totalGpa = (String) result.get("totalGpa");
//                        String cgpa = (String) result.get("cgpa");
//
//                        if (year != null && semester != null && subjects != null) {
//                            HashMap<String, Object> semesterData = new HashMap<>();
//                            semesterData.put("subjects", subjects);
//                            semesterData.put("name", name);
//                            semesterData.put("studentId", resultStudentId);
//                            semesterData.put("registrationNumber", registrationNumber);
//                            semesterData.put("session", session);
//                            semesterData.put("department", resultDepartment);
//                            semesterData.put("totalGpa", totalGpa);
//                            semesterData.put("cgpa", cgpa);
//
//                            resultsByYearSemester.computeIfAbsent(year, k -> new HashMap<>())
//                                    .computeIfAbsent(semester, k -> new ArrayList<>())
//                                    .add(semesterData);
//                        } else {
//                            Log.w(TAG, "Missing required fields in result: year=" + year + ", semester=" + semester + ", subjects=" + subjects);
//                        }
//                    }
//
//                    displayResults(resultsByYearSemester);
//                })
//                .addOnFailureListener(e -> {
//                    Log.e(TAG, "Failed to fetch results: " + e.getMessage());
//                    Toast.makeText(this, "Error loading results: " + e.getMessage(), Toast.LENGTH_SHORT).show();
//                });
//    }
//
//    private void displayResults(HashMap<String, HashMap<String, ArrayList<HashMap<String, Object>>>> resultsByYearSemester) {
//        resultContainer.removeAllViews();
//        if (resultsByYearSemester.isEmpty()) {
//            TextView noResults = new TextView(this);
//            noResults.setText("No results found for this " + (isAdmin ? "admin" : "student") + ".");
//            noResults.setTextSize(16);
//            noResults.setPadding(16, 16, 16, 16);
//            resultContainer.addView(noResults);
//            Log.d(TAG, "No results to display");
//            return;
//        }
//
//        for (String year : resultsByYearSemester.keySet()) {
//            CardView yearCard = new CardView(this);
//            LinearLayout.LayoutParams yearCardParams = new LinearLayout.LayoutParams(
//                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
//            yearCardParams.setMargins(0, 0, 0, 16);
//            yearCard.setLayoutParams(yearCardParams);
//            yearCard.setCardElevation(4);
//            yearCard.setRadius(8);
//            yearCard.setPadding(16, 16, 16, 16);
//
//            LinearLayout yearLayout = new LinearLayout(this);
//            yearLayout.setOrientation(LinearLayout.VERTICAL);
//            yearLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
//
//            TextView yearHeader = new TextView(this);
//            yearHeader.setText("Year: " + year);
//            yearHeader.setTextSize(18);
//            yearLayout.addView(yearHeader);
//
//            HashMap<String, ArrayList<HashMap<String, Object>>> semesters = resultsByYearSemester.get(year);
//            for (String semester : semesters.keySet()) {
//                CardView semesterCard = new CardView(this);
//                LinearLayout.LayoutParams semesterCardParams = new LinearLayout.LayoutParams(
//                        LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
//                semesterCardParams.setMargins(16, 8, 16, 8);
//                semesterCard.setLayoutParams(semesterCardParams);
//                semesterCard.setCardElevation(2);
//                semesterCard.setRadius(4);
//                semesterCard.setPadding(12, 12, 12, 12);
//
//                LinearLayout semesterLayout = new LinearLayout(this);
//                semesterLayout.setOrientation(LinearLayout.VERTICAL);
//                semesterLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
//
//                TextView semesterHeader = new TextView(this);
//                semesterHeader.setText("Semester: " + semester);
//                semesterHeader.setTextSize(16);
//                semesterLayout.addView(semesterHeader);
//
//                ArrayList<HashMap<String, Object>> semesterDataList = semesters.get(semester);
//                for (HashMap<String, Object> semesterData : semesterDataList) {
//                    @SuppressWarnings("unchecked")
//                    ArrayList<HashMap<String, String>> subjects = (ArrayList<HashMap<String, String>>) semesterData.get("subjects");
//                    String name = (String) semesterData.get("name");
//                    String resultStudentId = (String) semesterData.get("studentId");
//                    String registrationNumber = (String) semesterData.get("registrationNumber");
//                    String session = (String) semesterData.get("session");
//                    String resultDepartment = (String) semesterData.get("department");
//                    String totalGpa = (String) semesterData.get("totalGpa");
//                    String cgpa = (String) semesterData.get("cgpa");
//
//                    // Display student details
//                    TextView studentDetails = new TextView(this);
//                    studentDetails.setText("Name: " + (name != null ? name : "N/A") + ", ID: " + (resultStudentId != null ? resultStudentId : "N/A") +
//                            ", Reg: " + (registrationNumber != null ? registrationNumber : "N/A") + ", Session: " + (session != null ? session : "N/A") +
//                            ", Dept: " + (resultDepartment != null ? resultDepartment : "N/A"));
//                    studentDetails.setTextSize(14);
//                    studentDetails.setPadding(16, 8, 0, 8);
//                    semesterLayout.addView(studentDetails);
//
//                    // Display GPA and CGPA
//                    TextView gpaView = new TextView(this);
//                    gpaView.setText("Total GPA: " + (totalGpa != null ? totalGpa : "N/A") + ", CGPA: " + (cgpa != null ? cgpa : "N/A"));
//                    gpaView.setTextSize(14);
//                    gpaView.setPadding(16, 8, 0, 8);
//                    semesterLayout.addView(gpaView);
//
//                    // Display subjects
//                    for (HashMap<String, String> subject : subjects) {
//                        TextView subjectView = new TextView(this);
//                        subjectView.setText("Subject: " + (subject.get("subject") != null ? subject.get("subject") : "N/A") +
//                                ", Total: " + (subject.get("totalNumber") != null ? subject.get("totalNumber") : "N/A") +
//                                ", Grade: " + (subject.get("grade") != null ? subject.get("grade") : "N/A"));
//                        subjectView.setTextSize(14);
//                        subjectView.setPadding(16, 4, 0, 4);
//                        semesterLayout.addView(subjectView);
//                    }
//                }
//
//                semesterCard.addView(semesterLayout);
//                yearLayout.addView(semesterCard);
//            }
//
//            yearCard.addView(yearLayout);
//            resultContainer.addView(yearCard);
//        }
//    }
//}


package com.turja.student_result;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

public class ResultDisplayActivity extends AppCompatActivity {

    private static final String TAG = "ResultDisplayActivity";
    private FirebaseFirestore db;
    private String studentId;
    private String department;
    private boolean isAdmin;
    private LinearLayout resultContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result_display);

        db = FirebaseFirestore.getInstance();
        resultContainer = findViewById(R.id.resultContainer);

        // Get intent extras
        studentId = getIntent().getStringExtra("studentId");
        department = getIntent().getStringExtra("department");
        isAdmin = getIntent().getBooleanExtra("isAdmin", false);

        Log.d(TAG, "Received studentId: " + studentId + ", department: " + department + ", isAdmin: " + isAdmin);

        if (isAdmin || (studentId != null && department != null)) {
            fetchResults();
        } else {
            Toast.makeText(this, "Invalid data", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void fetchResults() {
        db.collection("results")
                .get() // Fetch all documents for debugging and admin mode
                .addOnSuccessListener(querySnapshot -> {
                    Log.d(TAG, "Total documents in collection: " + querySnapshot.size());
                    ArrayList<HashMap<String, Object>> results = new ArrayList<>();
                    for (QueryDocumentSnapshot document : querySnapshot) {
                        HashMap<String, Object> result = new HashMap<>(document.getData());
                        Log.d(TAG, "Document ID: " + document.getId() + ", Data: " + result.toString());
                        // Filter for student mode if not admin
                        if (!isAdmin) {
                            String resultStudentId = (String) result.get("studentId");
                            String resultDepartment = (String) result.get("department");
                            if (resultStudentId != null && resultDepartment != null &&
                                    resultStudentId.equals(studentId) && resultDepartment.equals(department)) {
                                results.add(result);
                                Log.d(TAG, "Matched document for studentId: " + studentId + ", department: " + department);
                            }
                        } else {
                            results.add(result); // Add all results for admin
                        }
                    }

                    if (results.isEmpty() && !isAdmin) {
                        Log.w(TAG, "No results found for studentId: " + studentId + ", department: " + department);
                        Toast.makeText(this, "No results found for this student.", Toast.LENGTH_SHORT).show();
                    }

                    // Sort results by timestamp (earliest to latest)
                    Collections.sort(results, new Comparator<HashMap<String, Object>>() {
                        @SuppressWarnings("unchecked")
                        @Override
                        public int compare(HashMap<String, Object> r1, HashMap<String, Object> r2) {
                            Long t1 = (Long) r1.get("timestamp");
                            Long t2 = (Long) r2.get("timestamp");
                            if (t1 == null || t2 == null) {
                                Log.w(TAG, "Timestamp missing or invalid in one or both documents");
                                return 0;
                            }
                            return t1.compareTo(t2);
                        }
                    });

                    HashMap<String, HashMap<String, ArrayList<HashMap<String, Object>>>> resultsByYearSemester = new HashMap<>();
                    for (HashMap<String, Object> result : results) {
                        String year = (String) result.get("year");
                        String semester = (String) result.get("semester");
                        @SuppressWarnings("unchecked")
                        ArrayList<HashMap<String, String>> subjects = (ArrayList<HashMap<String, String>>) result.get("subjects");
                        String name = (String) result.get("name");
                        String resultStudentId = (String) result.get("studentId");
                        String registrationNumber = (String) result.get("registrationNumber");
                        String session = (String) result.get("session");
                        String resultDepartment = (String) result.get("department");
                        String totalGpa = (String) result.get("totalGpa");
                        String cgpa = (String) result.get("cgpa");

                        if (year != null && semester != null && subjects != null) {
                            HashMap<String, Object> semesterData = new HashMap<>();
                            semesterData.put("subjects", subjects);
                            semesterData.put("name", name);
                            semesterData.put("studentId", resultStudentId);
                            semesterData.put("registrationNumber", registrationNumber);
                            semesterData.put("session", session);
                            semesterData.put("department", resultDepartment);
                            semesterData.put("totalGpa", totalGpa);
                            semesterData.put("cgpa", cgpa);

                            resultsByYearSemester.computeIfAbsent(year, k -> new HashMap<>())
                                    .computeIfAbsent(semester, k -> new ArrayList<>())
                                    .add(semesterData);
                        } else {
                            Log.w(TAG, "Missing required fields in result: year=" + year + ", semester=" + semester + ", subjects=" + subjects);
                        }
                    }

                    displayResults(resultsByYearSemester);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to fetch results: " + e.getMessage());
                    Toast.makeText(this, "Error loading results: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void displayResults(HashMap<String, HashMap<String, ArrayList<HashMap<String, Object>>>> resultsByYearSemester) {
        resultContainer.removeAllViews();
        if (resultsByYearSemester.isEmpty()) {
            TextView noResults = new TextView(this);
            noResults.setText("No results found for this " + (isAdmin ? "admin" : "student") + ".");
            noResults.setTextSize(20);
            noResults.setPadding(16, 16, 16, 16);
            resultContainer.addView(noResults);
            Log.d(TAG, "No results to display");
            return;
        }


        for (String year : resultsByYearSemester.keySet()) {
            CardView yearCard = new CardView(this);
            LinearLayout.LayoutParams yearCardParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            yearCardParams.setMargins(0, 0, 0, 16);
            yearCard.setLayoutParams(yearCardParams);
            yearCard.setCardElevation(4);
            yearCard.setRadius(8);
            yearCard.setPadding(16, 16, 16, 16);

            LinearLayout yearLayout = new LinearLayout(this);
            yearLayout.setOrientation(LinearLayout.VERTICAL);
            yearLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));



            TextView yearHeader = new TextView(this);
            yearHeader.setText("Year: " + year);
            yearHeader.setTextSize(20);
            yearHeader.setTextColor(ContextCompat.getColor(this, R.color.error_red));
            yearHeader.setTypeface(null, Typeface.BOLD);
            yearLayout.addView(yearHeader);




            HashMap<String, ArrayList<HashMap<String, Object>>> semesters = resultsByYearSemester.get(year);
            for (String semester : semesters.keySet()) {
                CardView semesterCard = new CardView(this);
                LinearLayout.LayoutParams semesterCardParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                semesterCardParams.setMargins(16, 8, 16, 8);
                semesterCard.setLayoutParams(semesterCardParams);
                semesterCard.setCardElevation(2);
                semesterCard.setRadius(4);
                semesterCard.setPadding(12, 12, 12, 12);

                LinearLayout semesterLayout = new LinearLayout(this);
                semesterLayout.setOrientation(LinearLayout.VERTICAL);
                semesterLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

                TextView semesterHeader = new TextView(this);
                semesterHeader.setText("Semester: " + semester);
                semesterHeader.setTypeface(null, Typeface.BOLD);
                semesterHeader.setTextSize(20);
                semesterHeader.setTextColor(ContextCompat.getColor(this, R.color.error_red));
                semesterLayout.addView(semesterHeader);

                ArrayList<HashMap<String, Object>> semesterDataList = semesters.get(semester);
                for (HashMap<String, Object> semesterData : semesterDataList) {
                    @SuppressWarnings("unchecked")
                    ArrayList<HashMap<String, String>> subjects = (ArrayList<HashMap<String, String>>) semesterData.get("subjects");
                    String name = (String) semesterData.get("name");
                    String resultStudentId = (String) semesterData.get("studentId");
                    String registrationNumber = (String) semesterData.get("registrationNumber");
                    String session = (String) semesterData.get("session");
                    String resultDepartment = (String) semesterData.get("department");
                    String totalGpa = (String) semesterData.get("totalGpa");
                    String cgpa = (String) semesterData.get("cgpa");

                    // Display student details
                    TextView studentDetails = new TextView(this);
                    studentDetails.setText("Name: " + (name != null ? name : "N/A") + ", ID: " + (resultStudentId != null ? resultStudentId : "N/A") +
                            ", Reg: " + (registrationNumber != null ? registrationNumber : "N/A") + ", Session: " + (session != null ? session : "N/A") +
                            ", Dept: " + (resultDepartment != null ? resultDepartment : "N/A"));
                    studentDetails.setTextSize(20);
                    studentDetails.setTypeface(null, Typeface.BOLD);
                    studentDetails.setTextColor(ContextCompat.getColor(this, R.color.info_blue));
                    studentDetails.setPadding(16, 8, 0, 8);
                    semesterLayout.addView(studentDetails);

                    // Display GPA and CGPA
                    TextView gpaView = new TextView(this);
                    gpaView.setText("Total GPA: " + (totalGpa != null ? totalGpa : "N/A") + ", CGPA: " + (cgpa != null ? cgpa : "N/A"));
                    gpaView.setTextSize(25);
                    gpaView.setTypeface(null, Typeface.BOLD);
                    gpaView.setTextColor(ContextCompat.getColor(this, R.color.success_green));
                    gpaView.setPadding(16, 8, 0, 8);
                    semesterLayout.addView(gpaView);

                    // Display subjects
                    for (HashMap<String, String> subject : subjects) {
                        TextView subjectView = new TextView(this);
                        subjectView.setText("Subject: " + (subject.get("subject") != null ? subject.get("subject") : "N/A") +
                                ", Total: " + (subject.get("totalNumber") != null ? subject.get("totalNumber") : "N/A") +
                                ", Grade: " + (subject.get("grade") != null ? subject.get("grade") : "N/A"));
                        subjectView.setTextSize(22);
                        subjectView.setTypeface(null, Typeface.BOLD);
                        subjectView.setTextColor(ContextCompat.getColor(this, R.color.secondary));
                        subjectView.setPadding(16, 4, 0, 4);
                        semesterLayout.addView(subjectView);
                    }
                }

                semesterCard.addView(semesterLayout);
                yearLayout.addView(semesterCard);
            }

            yearCard.addView(yearLayout);
            resultContainer.addView(yearCard);
        }

        // Add download button for students only
        if (!isAdmin) {
            Button downloadButton = new Button(this);
            downloadButton.setText("Download Result");
            downloadButton.setBackgroundColor(Color.parseColor("#4CAF50"));
            downloadButton.setTextColor(Color.WHITE);
            downloadButton.setPadding(16, 8, 16, 8);
            LinearLayout.LayoutParams buttonParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            buttonParams.setMargins(0, 16, 0, 0);
            downloadButton.setLayoutParams(buttonParams);
            downloadButton.setOnClickListener(v -> downloadResult(resultsByYearSemester));
            resultContainer.addView(downloadButton);
        }
    }

    private void downloadResult(HashMap<String, HashMap<String, ArrayList<HashMap<String, Object>>>> resultsByYearSemester) {
        if (isAdmin) {
            Toast.makeText(this, "Download option not available for admin", Toast.LENGTH_SHORT).show();
            return;
        }

        PdfDocument document = new PdfDocument();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(595, 842, 1).create(); // A4 size in points (1/72 inch)
        PdfDocument.Page page = document.startPage(pageInfo);
        Canvas canvas = page.getCanvas();
        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setTextSize(12);

        int y = 20;
        canvas.drawText("Student Result", 50, y, paint);
        y += 20;
        canvas.drawText("Name: " + (studentId != null ? studentId : "N/A") + ", Department: " + (department != null ? department : "N/A"), 50, y, paint);
        y += 20;

        for (String year : resultsByYearSemester.keySet()) {
            y += 20;
            canvas.drawText("Year: " + year, 50, y, paint);
            HashMap<String, ArrayList<HashMap<String, Object>>> semesters = resultsByYearSemester.get(year);
            for (String semester : semesters.keySet()) {
                y += 20;
                canvas.drawText("Semester: " + semester, 50, y, paint);
                ArrayList<HashMap<String, Object>> semesterDataList = semesters.get(semester);
                for (HashMap<String, Object> semesterData : semesterDataList) {
                    @SuppressWarnings("unchecked")
                    ArrayList<HashMap<String, String>> subjects = (ArrayList<HashMap<String, String>>) semesterData.get("subjects");
                    String name = (String) semesterData.get("name");
                    String resultStudentId = (String) semesterData.get("studentId");
                    String registrationNumber = (String) semesterData.get("registrationNumber");
                    String session = (String) semesterData.get("session");
                    String resultDepartment = (String) semesterData.get("department");
                    String totalGpa = (String) semesterData.get("totalGpa");
                    String cgpa = (String) semesterData.get("cgpa");

                    y += 20;
                    canvas.drawText("Name: " + (name != null ? name : "N/A") + ", ID: " + (resultStudentId != null ? resultStudentId : "N/A") +
                            ", Reg: " + (registrationNumber != null ? registrationNumber : "N/A") + ", Session: " + (session != null ? session : "N/A") +
                            ", Dept: " + (resultDepartment != null ? resultDepartment : "N/A"), 50, y, paint);
                    y += 20;
                    canvas.drawText("Total GPA: " + (totalGpa != null ? totalGpa : "N/A") + ", CGPA: " + (cgpa != null ? cgpa : "N/A"), 50, y, paint);

                    for (HashMap<String, String> subject : subjects) {
                        y += 20;
                        canvas.drawText("Subject: " + (subject.get("subject") != null ? subject.get("subject") : "N/A") +
                                ", Total: " + (subject.get("totalNumber") != null ? subject.get("totalNumber") : "N/A") +
                                ", Grade: " + (subject.get("grade") != null ? subject.get("grade") : "N/A"), 50, y, paint);
                    }
                    y += 20;
                }
            }
        }

        document.finishPage(page);

        // Save the PDF
        File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        String fileName = "Result_" + studentId + "_" + System.currentTimeMillis() + ".pdf";
        File file = new File(downloadsDir, fileName);

        try {
            document.writeTo(new FileOutputStream(file));
            Toast.makeText(this, "Result downloaded to: " + file.getAbsolutePath(), Toast.LENGTH_LONG).show();
            Log.d(TAG, "PDF saved to: " + file.getAbsolutePath());
        } catch (IOException e) {
            Log.e(TAG, "Error saving PDF: " + e.getMessage());
            Toast.makeText(this, "Error downloading result: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        } finally {
            document.close();
        }
    }
}