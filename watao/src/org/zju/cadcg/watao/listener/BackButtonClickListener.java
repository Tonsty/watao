package org.zju.cadcg.watao.listener;


import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;

public class BackButtonClickListener implements OnClickListener {

	protected Activity activity; 

	public BackButtonClickListener(Activity activity) {
		this.activity = activity;
	}


	@Override
	public void onClick(View v) {
		this.activity.finish();
	}

}
