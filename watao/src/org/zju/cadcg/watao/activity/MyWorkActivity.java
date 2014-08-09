package org.zju.cadcg.watao.activity;

import java.util.ArrayList;
import java.util.List;

import org.zju.cadcg.watao.R;
import org.zju.cadcg.watao.adapter.GeneralGridViewAdapter;
import org.zju.cadcg.watao.listener.BackButtonClickListener;
import org.zju.cadcg.watao.listener.StartActivityListener;
import org.zju.cadcg.watao.type.WTPottery;


import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.GridView;
import android.widget.LinearLayout;

public class MyWorkActivity extends Activity {

	List<WTPottery> data;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initData();
		initUI();
	}

	private void initData() {
		data = new ArrayList<WTPottery>();
		for (int i = 0; i < 15; i++) {
			data.add(new WTPottery());
		}
	}

	private void initUI() {
		setContentView(R.layout.activity_my_work);
		findViewById(R.id.back).setOnClickListener(new BackButtonClickListener(this));

		//init gridview
		GridView gridView = (GridView) findViewById(R.id.my_work_grid_view);
		gridView.setAdapter(new GeneralGridViewAdapter(this,GeneralGridViewAdapter.MY_WORK));
		//gridView.setOnItemClickListener(new GridViewChooseListenerForDecorate(glView));
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);  
		float density = dm.density;
		int numColumn = data.size();
		int allWidth = (int) (110 * numColumn * density);  
		int itemWidth = (int) (100 * density);  
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(  
				allWidth, LinearLayout.LayoutParams.WRAP_CONTENT);  
		gridView.setLayoutParams(params);  
		gridView.setColumnWidth(itemWidth);  
		gridView.setStretchMode(GridView.NO_STRETCH);  
		gridView.setNumColumns(numColumn);

		findViewById(R.id.pottery_share).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

			}
		});

		findViewById(R.id.pottery_buy).setOnClickListener(new StartActivityListener(this, BuyActivity.class));

	}
}
