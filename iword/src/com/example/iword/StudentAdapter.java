package com.example.iword;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class StudentAdapter extends ArrayAdapter<Student> {

	private int resourceId;

	public StudentAdapter(Context context, int textViewResourceId,
			List<Student> objects) {
		// TODO Auto-generated constructor stub
		super(context, textViewResourceId, objects);
		resourceId = textViewResourceId;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Student student = getItem(position);
		View view;
		ViewHolder viewHolder;
		if (convertView == null) {
			view = LayoutInflater.from(getContext()).inflate(resourceId, null);
			viewHolder = new ViewHolder();
			viewHolder.studentSerialno = (TextView) view
					.findViewById(R.id.student_serialno);
			viewHolder.studentName = (TextView) view
					.findViewById(R.id.student_name);
			viewHolder.studentSum = (TextView) view
					.findViewById(R.id.student_sum);
			view.setTag(viewHolder);
		} else {
			view = convertView;
			viewHolder = (ViewHolder) view.getTag();
		}
		viewHolder.studentSerialno.setText(student.getSerialno());
		viewHolder.studentName.setText(student.getName());
		viewHolder.studentSum.setText(String.valueOf(student.getSum()));
		return view;
	}

	class ViewHolder {

		TextView studentSerialno;
		TextView studentName;
		TextView studentSum;

	}
}
