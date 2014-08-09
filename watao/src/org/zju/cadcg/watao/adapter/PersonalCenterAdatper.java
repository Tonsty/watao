package org.zju.cadcg.watao.adapter;

import org.zju.cadcg.watao.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class PersonalCenterAdatper extends BaseAdapter {

	private LayoutInflater inflater;
	
	
	
	public PersonalCenterAdatper(Context context) {
		this.inflater = LayoutInflater.from(context);
	}



	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return 6;
	}



	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}



	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	String[] menuItem = {" "," "," "," "," "," "};

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		TextView view = (TextView) inflater.inflate(R.layout.item_my_watao, null);
		view.setText(menuItem[position]);
		return view;
	}


}
