package org.zju.cadcg.watao.activity;

import org.zju.cadcg.watao.R;
import org.zju.cadcg.watao.adapter.PersonalCenterAdatper;
import org.zju.cadcg.watao.listener.BackButtonClickListener;
import org.zju.cadcg.watao.listener.PersonalCenterMenuListener;

import android.app.Activity;
import android.os.Bundle;
import android.widget.GridView;

public class PersonanCenterActivity extends Activity {
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initData();
		initUI();
	}

	private void initData() {
		// TODO Auto-generated method stub
		
	}

	private void initUI() {
		setContentView(R.layout.activity_my_watao);
		findViewById(R.id.back).setOnClickListener(new BackButtonClickListener(this));
		
		GridView gridView = (GridView) findViewById(R.id.shape_menu);
		gridView.setAdapter(new PersonalCenterAdatper(this));
		
		gridView.setOnItemClickListener(new PersonalCenterMenuListener(this));
	}

}
