package org.zju.cadcg.watao.activity;

import java.util.ArrayList;
import java.util.List;

import org.zju.cadcg.watao.R;
import org.zju.cadcg.watao.utils.FileUtils;
import org.zju.cadcg.watao.utils.FileUtils.PotterySaved;
import org.zju.cadcg.watao.utils.GLManager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class ColloctActivity extends Activity {
	private List<PotterySaved> data;
	private ViewPager displayLayout;
	private List<ImageView> dataImageView = new ArrayList<ImageView>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initData();
		initUI();
	}

	private void initData() {
		data = FileUtils.getSavedPottery(this);
	}

	private void initUI() {
		setContentView(R.layout.activity_collect);
		GLManager.pushPottery();
		displayLayout = (ViewPager) findViewById(R.id.viewpager);
		for (PotterySaved ps : data) {
			if (ps.image != null) {
				
			ImageView iv = new ImageView(this);
			iv.setImageBitmap(ps.image);
			iv.setTag(ps.fileName);
			iv.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(ColloctActivity.this, PotteryFinishedActivity.class);
					intent.putExtra("fileName", (String)v.getTag());
					intent.putExtra("fromcolloct", "yes");
					ColloctActivity.this.startActivity(intent);
				}
			});
			dataImageView.add(iv);
			}
		}
		displayLayout.setAdapter(new PagerAdapter() {
			
			@Override
			public boolean isViewFromObject(View arg0, Object arg1) {
				return arg0 == arg1;
			}
			
			@Override
			public int getCount() {
				return dataImageView.size();
			}
			
			@Override
			public void destroyItem(ViewGroup container, int position,
					Object object) {
				container.removeView(dataImageView.get(position));
			}
			
			@Override
			public CharSequence getPageTitle(int position) {
				return "作品" + position;
			}
			
			@Override
			public Object instantiateItem(ViewGroup container, int position) {
				View child = dataImageView.get(position);
				container.addView(child, 0);
				return child;
			}
		});
		
	}
	
	@Override
	protected void onResume() {
		super.onResume();
	}
	
	
} 