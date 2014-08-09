package org.zju.cadcg.watao.activity;

import android.os.Bundle;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.app.Activity;
import android.content.Intent;

import org.zju.cadcg.watao.R;
import org.zju.cadcg.watao.Watao;
import org.zju.cadcg.watao.fragment.ChooseFragment;
import org.zju.cadcg.watao.fragment.ShapeFragment;
import org.zju.cadcg.watao.type.WTMode;
import org.zju.cadcg.watao.utils.GLManager;
import org.zju.cadcg.watao.utils.PotteryTextureManager;

public class ShapeActivity extends Activity {
	private ShapeFragment shapeFragment;
	public ShapeFragment getShapeFragment() {
		return shapeFragment;
	}

	public void setShapeFragment(ShapeFragment shapeFragment) {
		this.shapeFragment = shapeFragment;
	}

	private ChooseFragment chooseFragment;
	
	public boolean isChoose = false;
	private View splashView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_shape);
		shapeFragment = (ShapeFragment) getFragmentManager().findFragmentById(R.id.shape_fragment);
		chooseFragment = (ChooseFragment) getFragmentManager().findFragmentById(R.id.choose_fragment);
		splashView = findViewById(R.id.splash_image);
		
		getFragmentManager().beginTransaction().hide(chooseFragment).commit();
		
		new Thread(new Runnable() {

			public void run() {
				GLManager.init(getApplicationContext());
//				PotteryTextureManager.setBaseTexture(getResources(), R.drawable.clay);
				GLManager.background.setTexture(ShapeActivity.this, R.drawable.main_activity_background);
				GLManager.table.setTexture(ShapeActivity.this, R.drawable.table);
				GLManager.shadow.setTexture(ShapeActivity.this, R.drawable.shadow);
				shapeFragment.getGLView().onResume();
				while(!GLManager.isDrawed || !GLManager.alreadyInitGL){
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				shapeFragment.glManagerResume();
				final Animation animation = new AlphaAnimation(1, 0);
				animation.setDuration(1000);
				ShapeActivity.this.runOnUiThread(new Runnable() {
					public void run() {
						splashView.startAnimation(animation);
						splashView.setVisibility(View.GONE);
					}
				});
			}
		}).start();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(isChoose){
			if(keyCode == KeyEvent.KEYCODE_BACK){
				getFragmentManager().popBackStack();
				getFragmentManager().popBackStack();
				isChoose = false;
				Watao.startBGM(R.raw.bgm_shape);
				return true;
			}else{
				return super.onKeyDown(keyCode, event);
			}
		}else{
			if(shapeFragment.onKeyDown(keyCode, event)){
				return true;
			}else{
				return super.onKeyDown(keyCode, event);
			}
		}
	}

	public void choose() {
		getFragmentManager().beginTransaction()
		.show(chooseFragment)
		.addToBackStack(null)
		.commit();
		chooseFragment.startAnimation();
		isChoose = true;
		Watao.pauseBGM();
	}
	
	@Override
	protected void onNewIntent(Intent intent) {
		setIntent(intent);
		boolean isBackFromFinish = intent.getBooleanExtra("backToMain", false);
		if (isBackFromFinish) {
			getFragmentManager().popBackStack();
			getFragmentManager().popBackStack();
			shapeFragment.returnToHomePage();
			GLManager.pottery.reset();
		}
		super.onNewIntent(intent);
	}

}
