package org.zju.cadcg.watao.fragment;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.zju.cadcg.watao.R;
import org.zju.cadcg.watao.Watao;
import org.zju.cadcg.watao.activity.ColloctActivity;
import org.zju.cadcg.watao.activity.CourseActivity;
import org.zju.cadcg.watao.activity.DecorateActivity;
import org.zju.cadcg.watao.activity.IdeaMarketActivity;
import org.zju.cadcg.watao.activity.ShapeActivity;
import org.zju.cadcg.watao.adapter.GeneralGridViewAdapter;
import org.zju.cadcg.watao.gl.GLView;
import org.zju.cadcg.watao.gl200.Pottery200;
import org.zju.cadcg.watao.listener.GridViewChooseListener;
import org.zju.cadcg.watao.listener.StartActivityListener;
import org.zju.cadcg.watao.type.WTMode;
import org.zju.cadcg.watao.utils.FileUtils;
import org.zju.cadcg.watao.utils.PotteryTextureManager;
import org.zju.cadcg.watao.utils.GLManager;
import org.zju.cadcg.watao.view.ClassicDialog;


import android.animation.Animator;
import android.content.DialogInterface;
import android.animation.Animator.AnimatorListener;
import android.animation.IntEvaluator;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.RelativeLayout.LayoutParams;

public class ShapeFragment extends Fragment {

	private GLView glView;
	public WTMode mode;
	private View marketButton;
	private View accountButton;
	private View createButton;
	private View infButton;
	private View vLine;
	private View logoButton;
	private View returnToHomePageButton;
	private View resetButton;
	private View classicButton;
	private View nextButton;
	private GridView gridView;
	private View chooseClassicView;
	public boolean classicIsShowed = false;
	private GeneralGridViewAdapter adapter;
	private View shapeMenuBar;
	
	public boolean needSave = true;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		//initialize for home page
		mode = WTMode.VIEW;
		View view = inflater.inflate(R.layout.fragment_shape, null);
		glView = (GLView)view.findViewById(R.id.pottery);
		glView.onPause();
		GLManager.alreadyInitGL = false;
		
		glView.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, final MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {

					if (GLManager.hasUncomplete == 2) {
						new AlertDialog.Builder(getActivity())
						.setTitle("提示")
						.setMessage("您有未完成的作品,是否继续？")
						.setPositiveButton("是", new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog, int which) {
								GLManager.restorePottery(getActivity(), PreferenceManager.getDefaultSharedPreferences(getActivity()));
								Intent intent = new Intent(getActivity(), DecorateActivity.class);
								intent.putExtra("fromUncomplete", true);
								if (GLManager.decoratorTypeUn == DecorateActivity.QINGHUA) {
									intent.putExtra("type", DecorateActivity.QINGHUA);
								}else{

									intent.putExtra("type", DecorateActivity.YOUSHANGCAI);
								}
								Watao.pauseBGM();
								getActivity().startActivity(intent);
							}

						})
						.setNegativeButton("丢弃", new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog, int which) {
								beginShape(null);
								GLManager.hasUncomplete = 0;
							}
						}).create().show();
						return true;
					}else if(GLManager.hasUncomplete == 1){
						new AlertDialog.Builder(getActivity())
						.setTitle("提示")
						.setMessage("您有未完成的作品,是否继续？")
						.setPositiveButton("是", new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog, int which) {
								GLManager.restorePottery(getActivity(), PreferenceManager.getDefaultSharedPreferences(getActivity()));
								beginShape(null);
							}

						})
						.setNegativeButton("丢弃", new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog, int which) {
								GLManager.hasUncomplete = 0;
								beginShape(null);
							}
						}).create().show();
						return true;
					}else{
						beginShape(event);
						return true;
					}
				}else{
					return false;
				}
			}
		});
		marketButton = view.findViewById(R.id.market_button);
		marketButton.setOnClickListener(new StartActivityListener(getActivity(), IdeaMarketActivity.class){
			@Override
			public void onClick(View v) {
				Toast.makeText(getActivity(), "您已进入哇陶创意集市", Toast.LENGTH_SHORT).show();
				super.onClick(v);
			}
		});
		accountButton= view.findViewById(R.id.account_button);
		accountButton.setOnClickListener(new StartActivityListener(getActivity(), ColloctActivity.class));
		createButton = view.findViewById(R.id.create_button);
		createButton.setOnClickListener(new myOnClickListener());
		infButton = view.findViewById(R.id.inf_button);
		infButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(), CourseActivity.class);
				getActivity().startActivity(intent);
			}
		});
		logoButton = view.findViewById(R.id.logo);

		vLine = view.findViewById(R.id.v_line);

		returnToHomePageButton = view.findViewById(R.id.back_to_home_page_button);
		returnToHomePageButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				returnToHomePage();
			}
		});
		resetButton = view.findViewById(R.id.reset_button);
		resetButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				GLManager.pottery.reset();
			}
		});
		classicButton = view.findViewById(R.id.classic_button);
		classicButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (!classicIsShowed) {
					showClassic();
				}else{
					hideClassic();
				}
			}
		});

		nextButton = view.findViewById(R.id.next);
		nextButton.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				((ShapeActivity)getActivity()).choose();
			}
		});

		gridView = (GridView)view.findViewById(R.id.classicSample);
		adapter = new GeneralGridViewAdapter(getActivity(),GeneralGridViewAdapter.SAMPLE);
		gridView.setAdapter(adapter);
		gridView.setOnItemClickListener(new GridViewChooseListener(this));

		chooseClassicView = view.findViewById(R.id.choose_classic);

		chooseClassicView.post(new Runnable() {

			@Override
			public void run() {
				int height = chooseClassicView.getHeight();
				chooseClassicView.setTag(height);
				((RelativeLayout.LayoutParams) chooseClassicView.getLayoutParams()).topMargin = -height;
			}
		});
		
		final GestureDetector gd = new GestureDetector(getActivity(), new GestureDetector.SimpleOnGestureListener(){
			@Override
			public boolean onFling(MotionEvent e1, MotionEvent e2,
					float velocityX, float velocityY) {
				
				float x = e2.getX() - e1.getX();
	            float y = e2.getY() - e1.getY();
	            //限制必须得划过屏幕的1/6才能算划过
	            float y_limit = Watao.screenHeightPixel / 6;
	            float x_abs = Math.abs(x);
	            float y_abs = Math.abs(y);
	            if(x_abs <= y_abs){
	                //gesture down or up
	                if(y > y_limit || y < -y_limit){
	                    if(y<0){
	                    	hideClassic();
	                    }
	                }
	            }
	            return true;
			}
		});
		
		gridView.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return gd.onTouchEvent(event);
			}
		});

		view.findViewById(R.id.choose_classic_cancle_button).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				hideClassic();
			}
		});	
		
		shapeMenuBar = view.findViewById(R.id.menu_bar);

		return view;
	}
	
	public void beginShape(MotionEvent event){
		float x = 0;
		float y = 0;
		int screenWidth = 0;
		int screenHeight = 0; 
		if(event != null){
			screenWidth = Watao.screenWidthPixel;
			screenHeight = Watao.screenHeightPixel;
			x = event.getX();
			y = event.getY();
		}
		if(event == null || x > screenWidth / 2 && y > screenHeight / 3){
			GLManager.mode = WTMode.SHAPE;
			glView.setMode(WTMode.SHAPE);
			if (mode == WTMode.VIEW) {
				new Thread(){
					@Override
					public void run() {
						long startTime = System.currentTimeMillis();
						long elapseTime = 0;
						do{
							float rate = elapseTime / 700.0f;
							float eyeOffset = 0.3f * (1.0f - rate);
							GLManager.setEyeOffset(eyeOffset);
							GLManager.rotateSpeed = 0.36f - eyeOffset;
							elapseTime = System.currentTimeMillis() - startTime;
							try {
								Thread.sleep(40);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}while(elapseTime < 700);
						GLManager.setEyeOffset(0);
					}
				}.start();
				
				this.mode = WTMode.SHAPE;
				int[] location = new int[2];
				logoButton.getLocationOnScreen(location);
				int distance = location[0] + logoButton.getWidth();
				animateOut(marketButton, 0, distance);
				animateOut(accountButton, 33, distance);
				animateOut(logoButton, 50, distance);
				animateOut(infButton, 66, distance);
				animateOut(vLine, 88, distance);
				animateOut(createButton, 100, distance);
				
				//show shape menu
				shapeMenuBar.setVisibility(View.VISIBLE);
				nextButton.setVisibility(View.VISIBLE);
				ValueAnimator shapeMenuBarAnimator = ValueAnimator.ofFloat(0.0f,1.0f);
				shapeMenuBarAnimator.addUpdateListener(new AnimatorUpdateListener() {
					@Override
					public void onAnimationUpdate(ValueAnimator animation) {
						shapeMenuBar.setAlpha((float) animation.getAnimatedValue());
						shapeMenuBar.requestLayout();
						nextButton.setAlpha((float) animation.getAnimatedValue());
						nextButton.requestLayout();
					}
				});
				shapeMenuBarAnimator.setDuration(1000).start();
				Watao.startBGM(R.raw.bgm_shape);
			}
		}
	}

	
	public void loadShapeFromFile(int id) {
		id += 1;
		String fileName = "sampler_data" + id + ".txt";
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(getActivity().getAssets().open(fileName)));
			if (id == 5) {
				float height =	Float.parseFloat(reader.readLine())/1.5f;
				float[] bases = new float[50];
				for (int i = 0; i < 50; ++i) {
					bases[49 - i] = Float.parseFloat(reader.readLine())/1.5f;
					reader.readLine();
				}
				GLManager.pottery.setShape(bases, height);
			}else{
				float height =	Float.parseFloat(reader.readLine());
				float[] bases = new float[50];
				for (int i = 0; i < 50; ++i) {
					bases[49 - i] = Float.parseFloat(reader.readLine());
					reader.readLine();
				}
				GLManager.pottery.setShape(bases, height);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally{
			try {
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		hideClassic();
	}
	
	private void showClassic() {
		boolean isFirst = PreferenceManager.getDefaultSharedPreferences(getActivity()).getBoolean("isClassicFirst", true);
		if (isFirst) {
			ClassicDialog progressDialog = new ClassicDialog(getActivity(), R.layout.shanghua);
			progressDialog.show();
			PreferenceManager.getDefaultSharedPreferences(getActivity()).edit().putBoolean("isClassicFirst", false).commit();
		}
		int height = (Integer) chooseClassicView.getTag();
		Animation animation = new TranslateAnimation(0, 0, -height, 0);
		animation.setDuration(500);
		animation.setInterpolator(new AccelerateDecelerateInterpolator());
		chooseClassicView.startAnimation(animation);
		LayoutParams layoutParams = (RelativeLayout.LayoutParams)chooseClassicView.getLayoutParams();
		layoutParams.topMargin = 0;
		chooseClassicView.setLayoutParams(layoutParams);
		classicIsShowed = true;
	}

	@Override
	public void onResume() {
		super.onResume();
		needSave = true;
		PotteryTextureManager.isBefore = true;
		if (GLManager.alreadyInit && GLManager.alreadyInitGL) {
			((Pottery200)GLManager.pottery).switchShader(Pottery200.CLAY);
			PotteryTextureManager.setBaseTexture(getResources(), R.drawable.clay);
			
			glView.onResume();
			glManagerResume();
		}
		if (mode == WTMode.SHAPE && !((ShapeActivity)getActivity()).isChoose) {
			Watao.startBGM(R.raw.bgm_shape);
		}
	}

	public void glManagerResume() {
		GLManager.popPottery();
		GLManager.mode = mode;
		PotteryTextureManager.setBaseTexture(getResources(), R.drawable.clay);
		((Pottery200)GLManager.pottery).switchShader(Pottery200.CLAY);
		
		GLManager.background.setTexture(getActivity(), R.drawable.main_activity_background);
		GLManager.table.setTexture(getActivity(), R.drawable.table);
		GLManager.shadow.setTexture(getActivity(), R.drawable.shadow);
		
		if (mode == WTMode.SHAPE) {
			GLManager.setEyeOffset(0.0f);
			GLManager.rotateSpeed = 0.36f;
		}else{
			GLManager.setEyeOffset(0.3f);
			GLManager.rotateSpeed = 0.06f;
		}
	}
	@Override
	public void onStop() {
		if (GLManager.alreadyInit && needSave) {
			FileUtils.saveUncomplete(getActivity(), 
					GLManager.pottery.getRadiuses(), GLManager.pottery.getVertices(),
					null, null);
			PreferenceManager.getDefaultSharedPreferences(getActivity())
			.edit()
			.putFloat("height", GLManager.pottery.getCurrentHeight())
			.putFloat("var", GLManager.pottery.getVarUsedForEllipseToRegular())
			.putInt("hasUncomplete", 1)
			.commit();
			needSave = false;
		}
		super.onStop();
	}

	@Override
	public void onPause() {
		super.onPause();
		glView.onPause();
		Watao.pauseBGM();
	
	}

	class myOnClickListener implements OnClickListener {
		@Override
		public void onClick(View arg0) {
			if (GLManager.hasUncomplete == 2) {
				new AlertDialog.Builder(getActivity())
				.setTitle("提示")
				.setMessage("您有未完成的作品,是否继续？")
				.setPositiveButton("是", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						GLManager.restorePottery(getActivity(), PreferenceManager.getDefaultSharedPreferences(getActivity()));
						Intent intent = new Intent(getActivity(), DecorateActivity.class);
						intent.putExtra("fromUncomplete", true);
						if (GLManager.decoratorTypeUn == DecorateActivity.QINGHUA) {
							intent.putExtra("type", DecorateActivity.QINGHUA);
						}else{

							intent.putExtra("type", DecorateActivity.YOUSHANGCAI);
						}
						Watao.pauseBGM();
						getActivity().startActivity(intent);
					}

				})
				.setNegativeButton("丢弃", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						beginShape(null);
						GLManager.hasUncomplete = 0;
					}
				}).create().show();
			}else if(GLManager.hasUncomplete == 1){
				new AlertDialog.Builder(getActivity())
				.setTitle("提示")
				.setMessage("您有未完成的作品,是否继续？")
				.setPositiveButton("是", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						GLManager.restorePottery(getActivity(), PreferenceManager.getDefaultSharedPreferences(getActivity()));
						beginShape(null);
					}

				})
				.setNegativeButton("丢弃", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						GLManager.hasUncomplete = 0;
						beginShape(null);
					}
				}).create().show();
			}else{
				beginShape(null);
			}
//			if (GLManager.hasUncomplete == 2) {
//				
//				new AlertDialog.Builder(getActivity())
//				.setTitle("提示")
//				.setMessage("您有未完成的作品")
//				.setPositiveButton("继续", new DialogInterface.OnClickListener() {
//
//					@Override
//					public void onClick(DialogInterface dialog, int which) {
//						Intent intent = new Intent(getActivity(), DecorateActivity.class);
//						intent.putExtra("fromUncomplete", true);
//						if (GLManager.decoratorTypeUn == DecorateActivity.QINGHUA) {
//							intent.putExtra("type", DecorateActivity.QINGHUA);
//						}else{
//							intent.putExtra("type", DecorateActivity.YOUSHANGCAI);
//						}
//						Watao.pauseBGM();
//						getActivity().startActivity(intent);							
//					}
//
//				})
//				.setNegativeButton("丢弃", new DialogInterface.OnClickListener() {
//
//					@Override
//					public void onClick(DialogInterface dialog, int which) {
//						GLManager.pottery.reset();
//						((Pottery200)GLManager.pottery).switchShader(Pottery200.CLAY);
//						PotteryTextureManager.setBaseTexture(getResources(), R.drawable.clay);
//						beginShape(null);
//					}
//				}).create().show();
//			}else{
//				beginShape(null);
//			}
		}

	}

	private void animateOut(final View view, int startTime, int distance) {
		Animation animation = new TranslateAnimation(0, -distance, 0, 0);
		animation.setInterpolator(new AccelerateInterpolator());
		animation.setDuration(500);
		animation.setStartOffset(startTime);
		view.startAnimation(animation);
		view.setTag(distance);
		view.setVisibility(View.INVISIBLE);
	}

	public void returnToHomePage() {
		if(classicIsShowed){
			hideClassic();
			return;
		}
		new Thread(){
			@Override
			public void run() {
				GLManager.mode = WTMode.VIEW;
				long startTime = System.currentTimeMillis();
				long elapseTime = 0;
				do{
					float eyeOffset = 0.3f * elapseTime / 700.0f;
					GLManager.setEyeOffset(eyeOffset);
					GLManager.rotateSpeed = 0.36f - eyeOffset;
					elapseTime = System.currentTimeMillis() - startTime;
					try {
						Thread.sleep(25);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}while(elapseTime < 700);
			}
		}.start();
		mode = WTMode.VIEW;

		animateIn(marketButton, 0);
		animateIn(accountButton, 33);
		animateIn(logoButton, 50);
		animateIn(infButton, 66);
		animateIn(vLine, 88);
		animateIn(createButton, 100 );
		//hide shape menu
		ValueAnimator shapeMenuBarAnimator = ValueAnimator.ofFloat(1.0f,0.0f);
		shapeMenuBarAnimator.addUpdateListener(new AnimatorUpdateListener() {
			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				shapeMenuBar.setAlpha((float) animation.getAnimatedValue());
				shapeMenuBar.requestLayout();
				nextButton.setAlpha((float) animation.getAnimatedValue());
				nextButton.requestLayout();
			}
		});
		shapeMenuBarAnimator.addListener(new AnimatorListener() {
			
			@Override
			public void onAnimationStart(Animator animation) {
			}
			
			@Override
			public void onAnimationRepeat(Animator animation) {
			}
			
			@Override
			public void onAnimationEnd(Animator animation) {
				shapeMenuBar.setVisibility(View.GONE);
				nextButton.setVisibility(View.GONE);
			}
			
			@Override
			public void onAnimationCancel(Animator animation) {
				
			}
		});
		shapeMenuBarAnimator.setDuration(1000).start();
		Watao.pauseBGM();
	}

	private void animateIn(View view, int startTime) {
		Object tag = view.getTag();
		if (tag == null) {
			return;
		}
		Animation animation = new TranslateAnimation(-(Integer) tag, 0, 0, 0);
		animation.setInterpolator(new AccelerateInterpolator());
		animation.setDuration(500);
		animation.setStartOffset(startTime);
		view.startAnimation(animation);
		view.setVisibility(View.VISIBLE);
	}
	
	public void hideClassic() {
		final int height = (Integer) chooseClassicView.getTag();
		
		ValueAnimator animator = ValueAnimator.ofInt(0,100);
		animator.addUpdateListener(new AnimatorUpdateListener() {
			private IntEvaluator mEvaluator = new IntEvaluator();

			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				int currentValue = (int) animation.getAnimatedValue();
				float fraction = currentValue / 100.0f;
				((LayoutParams)chooseClassicView.getLayoutParams()).topMargin = mEvaluator.evaluate(fraction,0,-height);
				chooseClassicView.requestLayout();
			}
		});
		animator.setDuration(500).start();
		classicIsShowed = false;
	}
	


	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK && classicIsShowed){
			hideClassic();
			return true;
		}else if (keyCode == KeyEvent.KEYCODE_BACK && mode == WTMode.SHAPE) {
			returnToHomePage();
			return true;
		}else{
			return false;
		}
	}

	public GLView getGLView() {
		return glView;
	}
}       
