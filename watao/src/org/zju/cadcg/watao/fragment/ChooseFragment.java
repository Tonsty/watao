package org.zju.cadcg.watao.fragment;

import org.zju.cadcg.watao.R;
import org.zju.cadcg.watao.Watao;
import org.zju.cadcg.watao.activity.DecorateActivity;
import org.zju.cadcg.watao.activity.ShapeActivity;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.ViewGroup;

public class ChooseFragment extends Fragment implements OnClickListener{
	
	private View left;
	private View right;
	private AlphaAnimation animation;
	private View view;
	private View backButton;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_choose, null);
		left = view.findViewById(R.id.qinghua);
		left.setOnClickListener(this);
		right = view.findViewById(R.id.youshangcai);
		right.setOnClickListener(this);
		
		backButton = view.findViewById(R.id.choose_back_button);
		backButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				getFragmentManager().popBackStack();
				getFragmentManager().popBackStack();
				((ShapeActivity)getActivity()).isChoose = false;
				Watao.startBGM(R.raw.bgm_shape);			
			}
		});
		animation = new AlphaAnimation(0f, 1f);
		animation.setDuration(1000);
		animation.setAnimationListener(new AnimationListener() {
			
			@Override
			public void onAnimationStart(Animation animation) {
			}
			
			@Override
			public void onAnimationRepeat(Animation animation) {
			}
			
			@Override
			public void onAnimationEnd(Animation animation) {
				getFragmentManager().beginTransaction().hide(((ShapeActivity)getActivity()).getShapeFragment()).addToBackStack(null).commit();
			}
		});
		return view;
	}

	@Override
	public void onClick(View v) {
		Intent intent = new Intent(getActivity(), DecorateActivity.class);
		if(v.getId() == R.id.qinghua){
			intent.putExtra("type", DecorateActivity.QINGHUA);
		}else{
			intent.putExtra("type", DecorateActivity.YOUSHANGCAI);
		}
		((ShapeActivity)getActivity()).getShapeFragment().needSave = false;
		getActivity().startActivity(intent);
		getActivity().overridePendingTransition(R.anim.activity_fade_in, R.anim.activity_fade_out);
	}
	
	public void startAnimation() {
		view.startAnimation(animation);
	}
}
