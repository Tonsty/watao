package org.zju.cadcg.watao.listener;

import org.zju.cadcg.watao.activity.MyWorkActivity;
import org.zju.cadcg.watao.activity.SettingActivity;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

public class PersonalCenterMenuListener implements OnItemClickListener {

	private Context context;

	public PersonalCenterMenuListener(Context context) {
		this.context = context;
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		Intent intent = null;
		switch (arg2) {
		case 0:
			intent = new Intent(context, MyWorkActivity.class);
			break;
		case 5:
			intent = new Intent(context, SettingActivity.class);
			break;

		default:
			break;
		}
		context.startActivity(intent);
	}

}


