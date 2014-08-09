package org.zju.cadcg.watao.activity;

import org.zju.cadcg.watao.R;
import org.zju.cadcg.watao.listener.BackButtonClickListener;

import android.app.Activity;
import android.os.Bundle;

public class AccountManagerActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initUI();
	}

	private void initUI() {
		setContentView(R.layout.activity_account_manager);
		findViewById(R.id.back).setOnClickListener(new BackButtonClickListener(this));
	}
}
