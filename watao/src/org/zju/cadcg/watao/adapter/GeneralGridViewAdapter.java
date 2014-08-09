package org.zju.cadcg.watao.adapter;

import java.util.ArrayList;
import java.util.List;

import org.zju.cadcg.watao.R;
import org.zju.cadcg.watao.type.WTDecorator;
import org.zju.cadcg.watao.type.WTObject;
import org.zju.cadcg.watao.type.WTSample;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

public class GeneralGridViewAdapter extends BaseAdapter{
	private LayoutInflater inflater;
	private List<WTObject> data;
	private ImageView choosedView;
	private int choosePosition;

	public final static int SAMPLE = 0;
	public final static int DECORATE = 1;
	public static final int MY_WORK = 2;

	private void initDate(int dataType) {
		data = new ArrayList<WTObject>();
		if (dataType == SAMPLE) {
			for (int i = 0; i < 9; i++) {
				data.add(new WTSample());
			}
		}else if(dataType == DECORATE){
			for (int i = 0; i < 9; i++) {
				data.add(new WTDecorator());
			}
		}else if(dataType == MY_WORK){
			for (int i = 0; i < 9; i++) {
				data.add(new WTDecorator());
			}
		}
	}

	public WTObject getChoosedWTObject() {
		return this.data.get(choosePosition);
	}
	
	public int getChoosePosition(){
		return choosePosition;
	}
	
	public void choose(View view,int choosePosition) {
		if (choosedView != null) {
			choosedView.setImageDrawable(null);
		}
		choosedView = ((ViewHold) view.getTag()).imageView;
		this.choosePosition = choosePosition;
	}

	public final class ViewHold{
		public ImageView imageView;
	}

	public GeneralGridViewAdapter(Context context,int dataType) {
		inflater = LayoutInflater.from(context);
		initDate(dataType);
	}

	@Override
	public int getCount() {
		return data.size();
	}

	@Override
	public Object getItem(int position) {
		return data.get(position);
	}

	@Override
	public long getItemId(int position) {
		return ((WTObject) getItem(position)).getId();
	}

	private static final int[] classicId= {R.drawable.sampler_1,R.drawable.sampler_2,R.drawable.sampler_3,R.drawable.sampler_4,R.drawable.sampler_5,R.drawable.sampler_6,R.drawable.sampler_7,R.drawable.sampler_8,R.drawable.sampler_9};
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHold viewHold;
		if(convertView == null){
			convertView = inflater.inflate(R.layout.item_sample, parent, false);
			viewHold = new ViewHold();
			viewHold.imageView = (ImageView) convertView.findViewById(R.id.sample_img);
			convertView.setTag(viewHold);
		}else{
			viewHold = (ViewHold) convertView.getTag();
		}
		viewHold.imageView.setBackgroundResource(classicId[position]);
		viewHold.imageView.setImageDrawable(null);
		return convertView;
	}

}
