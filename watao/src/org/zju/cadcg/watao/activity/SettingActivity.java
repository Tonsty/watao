package org.zju.cadcg.watao.activity;

import org.zju.cadcg.watao.R;
import org.zju.cadcg.watao.listener.BackButtonClickListener;
import org.zju.cadcg.watao.listener.StartActivityListener;

import android.app.Activity;
import android.os.Bundle;

public class SettingActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initUI();
	}

	private void initUI() {
		setContentView(R.layout.activity_setting);
		findViewById(R.id.back).setOnClickListener(new BackButtonClickListener(this));
		findViewById(R.id.account_manage).setOnClickListener(new StartActivityListener(this, AccountManagerActivity.class));
	}
	
	
}
