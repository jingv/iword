package com.example.iword;

import java.util.List;





import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class StudentAdapter2 extends ArrayAdapter<Student> {

	private int resourceId;

	public StudentAdapter2(Context context, int textViewResourceId, List<Student> objects) {
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
			viewHolder.studentSerialno = (TextView) view.findViewById(R.id.student_serialno);
			viewHolder.studentName = (TextView) view.findViewById(R.id.student_name);
			viewHolder.studentSum = (TextView) view.findViewById(R.id.student_sum);
			view.setTag(viewHolder);
		} else {
			view = convertView;
			viewHolder = (ViewHolder) view.getTag();
		}
//		viewHolder.studentImage.setImageResource(student.getImageId()+0x7f020000);
		viewHolder.studentSerialno.setText(student.getSerialno());
		viewHolder.studentName.setText(student.getName());
		viewHolder.studentSum.setText(String.valueOf(student.getCur_day()));
		return view;
	}
	
	class ViewHolder {
		
		TextView studentSerialno;
		TextView studentName;
		TextView studentSum;
		
	}

}
