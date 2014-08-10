package org.zju.cadcg.watao.activity;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.app.Activity;
import android.content.Intent;

import org.zju.cadcg.watao.R;
import org.zju.cadcg.watao.Watao;
import org.zju.cadcg.watao.fragment.ChooseFragment;
import org.zju.cadcg.watao.fragment.ShapeFragment;
import org.zju.cadcg.watao.utils.GLManager;

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
		//get view
//		shapeFragment = (ShapeFragment) getFragmentManager().findFragmentById(R.id.shape_fragment);
//		chooseFragment = (ChooseFragment) getFragmentManager().findFragmentById(R.id.choose_fragment);
		splashView = findViewById(R.id.splash_image);
		
		//hide the choose fragment
//		getFragmentManager().beginTransaction().hide(chooseFragment).commit();
		
		//init the gl Object when the splash view is show
		new Thread(new Runnable() {

			public void run() {
				//init fragment
				shapeFragment = new ShapeFragment();
				chooseFragment = new ChooseFragment();
				GLManager.init(getApplicationContext());
				getFragmentManager().beginTransaction().replace(R.id.fragment_container, shapeFragment).commit();
			}
		}).start();
		
		new Thread(new Runnable() {

			public void run() {
				while (!GLManager.isDrawed) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				hideSplash();
			}
		}).start();
	}
	
	public void hideSplash() {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				System.out
						.println("ShapeActivity.hideSplash().new Runnable() {...}.run()");
				splashView.animate().alpha(0).setDuration(1000).start();
				splashView.setVisibility(View.GONE);
			}
		});
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
