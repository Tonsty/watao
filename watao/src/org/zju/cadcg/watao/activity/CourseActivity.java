package org.zju.cadcg.watao.activity;
import java.util.ArrayList;
import java.util.List;

import org.zju.cadcg.watao.R;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


public class CourseActivity extends Activity {

	private ViewPager viewPage;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initUI();
	}
	
	int[] courseImageId = new int[]{
			R.drawable.c1,
			R.drawable.c2,
			R.drawable.c3,
			R.drawable.c4,
			R.drawable.c5,
			R.drawable.c6,
			R.drawable.c7,
			R.drawable.c8,
			R.drawable.c9,
			R.drawable.c10,
	};

	List<View> viewList = new ArrayList<View>();

	private String[] courseText = {
			"哇陶APP是景德镇美德陶瓷科技实业有限公司、景德镇哇陶陶艺体验基地和浙江大学计算机学院共同开发的一款移动在线陶瓷个性定制软件。",
			"点选创造，可以开始动手DIY尺寸最高为24厘米，最宽为17厘米左右的陶瓷作品。\n" +
					"点选集市，可以浏览和选购设计师作品，您的DIY作品也有机会陈列其中哦。\n" + 
					"点选收藏，可以浏览和修改自己DIY并且收藏的作品。\n",
					
			"进入创造，首先是拉坯环节，用手指控制瓷土坯体，横向或竖向收缩扩展，以创造您想要的器型。\n" +
			"温馨提示：如果您的大作希望变成实物，请避免过于头重脚轻和腰部过细的形体。",

			"点击参考器型，选择一款为您精心挑选的经典或实用器型，并在此基础上进一步创作。",
			
			"完成拉坯后，点击“下一步”即进入装饰环节，点选一扇门，即进入“青花”或“釉上彩”装饰界面。",
			
			"如进入青花：在页面上方选择图案后点击坯体相应位置即可加上青花图案，未烧制前线条是黑色的，完成青花图案装饰后，点击“烧制”，作品入窑经高温煅烧后会呈现出幽蓝神采。",
			
			"如进入釉上彩：作品会先烧造成白胎，然后在页面上方选择图案后点击白胎相应位置即可加上釉上彩图案。",
			
			"无论青花还是釉上彩，都要入窑经1280℃左右高温烧造才能成瓷哦，其中青花在坯体绘图装饰完毕后经过此高温烧制作品即完成，而釉上彩经过此高温烧制成白胎瓷并绘制图案后还需要780℃烤炉才能完成作品。",
			
			"大作完成后您可以收藏，可以分享，还可以下单变成实物作品哦。所有实物作品均在景德镇以手工的方式精工细作。",
			"在购买环节系统会根据您拉坯、绘制中作品的复杂程度计算出相应的购买价格，器型越大越奇特和图案越多越复杂价格就越高，反之则越低。"
	};
	private void initUI() {
		setContentView(R.layout.activity_course);
		viewPage = (ViewPager) findViewById(R.id.viewpager);
		LayoutInflater layoutInflater = LayoutInflater.from(this);
		
		for (int i = 0; i < 10; ++i) {
			View view = layoutInflater.inflate(R.layout.item_course, null);
			TextView textView = (TextView) view.findViewById(R.id.cource_text);
			textView.setText(courseText[i]);
			ImageView imageView = (ImageView) view.findViewById(R.id.cource_image);
			imageView.setImageResource(courseImageId[i]);
			viewList.add(view);
		}
		
		viewPage.setAdapter(new PagerAdapter() {
			
			@Override
			public boolean isViewFromObject(View arg0, Object arg1) {
				return arg0 == arg1;
			}
			
			@Override
			public int getCount() {
				return viewList.size();
			}
			
			@Override
			public void destroyItem(ViewGroup container, int position,
					Object object) {
				container.removeView(viewList.get(position));
			}
			
			@Override
			public Object instantiateItem(ViewGroup container, int position) {
				View child = viewList.get(position);
				container.addView(child, 0);
				return child;
			}
		});
		
		
	}
}
