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
import android.widget.BaseAdapter;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.ImageView;

public class ColloctActivity extends Activity {
	private List<PotterySaved> data;
	private List<ImageView> dataImageView = new ArrayList<ImageView>();
	private GridView displayLayout;

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
		displayLayout = (GridView) findViewById(R.id.collects);
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
		displayLayout.setAdapter(new BaseAdapter() {
			
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				return dataImageView.get(position);
			}
			
			@Override
			public long getItemId(int position) {
				return position;
			}
			
			@Override
			public Object getItem(int position) {
				dataImageView.get(position);
				return null;
			}
			
			@Override
			public int getCount() {
				return dataImageView.size();
			}
		});
		
	}
} 