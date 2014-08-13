package org.zju.cadcg.watao.activity;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.zju.cadcg.watao.R;
import org.zju.cadcg.watao.Watao;
import org.zju.cadcg.watao.gl.GLView;
import org.zju.cadcg.watao.gl200.Pottery200;
import org.zju.cadcg.watao.gl200.Table200;
import org.zju.cadcg.watao.type.WTDecorateTypeEnum;
import org.zju.cadcg.watao.type.WTDecorator;
import org.zju.cadcg.watao.type.WTMode;
import org.zju.cadcg.watao.utils.CommonUtil;
import org.zju.cadcg.watao.utils.FileUtils;
import org.zju.cadcg.watao.utils.PotteryTextureManager;
import org.zju.cadcg.watao.utils.GLManager;
import org.zju.cadcg.watao.utils.PotteryTextureManager.Pattern;
import org.zju.cadcg.watao.view.CustomProgressDialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Toast;
import android.widget.ToggleButton;

public class DecorateActivity extends Activity {
	
	public static final int QINGHUA = 0;
	public static final int YOUSHANGCAI = 1;
	
	private GLView glView;
	private LinearLayout decoratorDisplayView;
	private View decoratorTypes;
	private View decoratorDisplayScrollView;
	private View nextButton;
	private View backButton;
	private ToggleButton eraseButton;
	
	private int potteryType;
	private long time;
	
	public static boolean isModify = false;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		isModify = false;
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_decorate);
		potteryType = getIntent().getIntExtra("type", -1);
		if (getIntent().getBooleanExtra("fromUncomplete", false)) {
			isFirst = false;
			youshangFlag = false;
			GLManager.hasUncomplete = 0;
		}
		glView = (GLView) findViewById(R.id.pottery);

		decoratorDisplayView = (LinearLayout) findViewById(R.id.decorator);
		setDecorator(WTDecorateTypeEnum.DANDU);

		nextButton = findViewById(R.id.next);
		nextButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				next();
			}
		});

		backButton = findViewById(R.id.back_to_home_page_button);
		backButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (isModify) {
					needSave = false;
					new AlertDialog.Builder(DecorateActivity.this).setMessage("您的作品即将丢失,是否继续?").setNegativeButton("否", null).setPositiveButton("是", new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							startActivity(new Intent(DecorateActivity.this, ShapeActivity.class).putExtra("backToMain", true));
						}
					}).show();
				}else{
					startActivity(new Intent(DecorateActivity.this, ShapeActivity.class).putExtra("backToMain", true));
				}
			}
		});
		
		
		eraseButton = (ToggleButton)findViewById(R.id.erase);
		eraseButton.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					currentView.setBackgroundResource(R.drawable.decorate_block);
					oldWidth = PotteryTextureManager.currentDecorater.getWidth();
					PotteryTextureManager.currentDecorater.setWidth(0.05f);
				}else{
					currentView.setBackgroundResource(R.drawable.decorate_block_choosed);
					PotteryTextureManager.currentDecorater.setWidth(oldWidth);
				}
				PotteryTextureManager.isEraseMode = isChecked;
			}
		});
		RadioButton type1 = (RadioButton) findViewById(R.id.decorate_type_1);
		type1.setOnClickListener(new ChooseDecoratorType(0));
		type1.setChecked(true);
		findViewById(R.id.decorate_type_2).setOnClickListener(new ChooseDecoratorType(1));
		findViewById(R.id.decorate_type_3).setOnClickListener(new ChooseDecoratorType(2));
		View type4 = findViewById(R.id.decorate_type_4);
		type4.setOnClickListener(new ChooseDecoratorType(3));

		decoratorTypes = findViewById(R.id.decorate_type);
		decoratorDisplayScrollView = findViewById(R.id.decorator_scrollView);
		if (GLManager.pottery.getCurrentHeight() < 0.75) {
			type4.setEnabled(false);
			new AlertDialog.Builder(this).setMessage("您的胚体高度不足6cm，无法进行自拍贴图").setNeutralButton("确定", null).show();
		}
	}
	private float oldWidth;
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (System.currentTimeMillis() - time > 2000) {
				Toast.makeText(this, "再按一次推出程序", Toast.LENGTH_SHORT).show();
				time = System.currentTimeMillis();
			}else{
				Intent intent = new Intent(Intent.ACTION_MAIN);
				intent.addCategory(Intent.CATEGORY_HOME);
				startActivity(intent);
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	

	public void next(){
		if (potteryType == QINGHUA) {
			startFire(1280);
		}else{
			startFire(780);
		}
	}
	private boolean isFirst = true;
	private boolean needSave = true;
	private String fileNameForCamera;
	private boolean youshangFlag = true;
	
	public void startFire(final int wendu){
		new AsyncTask<URL, Integer, String>(){
			CustomProgressDialog progressDialog = new CustomProgressDialog(DecorateActivity.this, wendu);
			
			@Override
			protected void onPreExecute() {
				GLManager.mode = WTMode.FIRE;
				
				GLManager.pottery.setLum(getFactor(0));
				GLManager.background.setLum(getFactor(0));
				GLManager.table.setLum(getFactor(0));
				//set pregressdialog position
				android.view.WindowManager.LayoutParams params = progressDialog.getWindow().getAttributes();  
				params.y = 100;  
				params.gravity = Gravity.TOP;  
				progressDialog.getWindow().setAttributes(params); 

				
				//set progressdialog content
				progressDialog.setTitle("正在烧制...");
				progressDialog.setCancelable(false);
				progressDialog.show();
				
				//hide the menu;
				setMenuVisibility(false);
				Watao.startBGM(R.raw.bgm_fire);
				GLManager.background.setTexture(DecorateActivity.this, R.drawable.fire_bgk);
				((Table200)GLManager.table).switchShader(Table200.FIRE);
				((Pottery200)GLManager.pottery).switchShader(Pottery200.FIRE);
				if (potteryType == QINGHUA) {
					GLManager.table.setTexture(DecorateActivity.this, R.drawable.table);
				}
			}

			@Override
			protected String doInBackground(URL... params) {
				int j = 9;
				for (int i = 1; i < 26; ++i) {
					int id = CommonUtil.getResId("fire" + Integer.toString(i), R.raw.class);
					GLManager.fire.changeTexture(DecorateActivity.this, id);
					publishProgress((i * 100 / 104));
					GLManager.pottery.setLum(getFactor(i * j));
					GLManager.background.setLum(getFactor(i * j));
					GLManager.table.setLum(getFactor(i * j));
					try {
						Thread.sleep(40);
					} catch (Exception e) {
						e.printStackTrace();
					}

				}
				for (int i = 26; i < 78; ++i) {
					int id = CommonUtil.getResId("fire" + Integer.toString(i), R.raw.class);
					GLManager.fire.changeTexture(DecorateActivity.this, id);
					publishProgress((i * 100 / 104));
					try {
						Thread.sleep(40);
					} catch (Exception e) {
						e.printStackTrace();
					}

				}
				for (int i = 78; i < 105; ++i) {
					int id = CommonUtil.getResId("fire" + Integer.toString(i), R.raw.class);
					GLManager.fire.changeTexture(DecorateActivity.this, id);
					publishProgress((i * 100 / 104));
					GLManager.pottery.setLum(getFactor((105 - i) * j));
					GLManager.background.setLum(getFactor((105 - i) * j));
					GLManager.table.setLum(getFactor((105 - i) * j));
					try {
						Thread.sleep(40);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				return "ok";
			}

			private float getFactor(int i) {
				return i / 150.0f + 0.28f;
			}

			protected void onProgressUpdate(Integer... values) {
				progressDialog.setProgress(values[0]);
			}

			protected void onPostExecute(String result) {
				progressDialog.dismiss();
				if (potteryType == QINGHUA) {
					GLManager.pottery.setLum(getFactor(-42));
					GLManager.background.setLum(getFactor(-42));
					GLManager.table.setLum(getFactor(-42));
					Intent intent = new Intent(DecorateActivity.this, PotteryFinishedActivity.class);
					intent.putExtra("type", potteryType);
					GLManager.mode = WTMode.INTERACT_VIEW;
					PotteryTextureManager.isBefore = false;
					PotteryTextureManager.reloadPattern();
					needSave = false;
					glView.onPause();
					startActivity(intent);
					DecorateActivity.this.overridePendingTransition(R.anim.activity_fade_in, R.anim.activity_fade_out);
				}else{
					if (youshangFlag) {
						setMenuVisibility(true);
						GLManager.pottery.setLum(getFactor(-42));
						GLManager.background.setLum(getFactor(-42));
						GLManager.table.setLum(getFactor(-42));
						GLManager.mode = WTMode.DEROCATE;
						glView.setMode(WTMode.DEROCATE);
						GLManager.table.setTexture(DecorateActivity.this, R.drawable.shangyoumuwen);
						GLManager.background.setTexture(DecorateActivity.this, R.drawable.decorate_background);
						GLManager.pottery.switchShader(Pottery200.CI);
						GLManager.table.switchShader(Table200.COMMON);	
						PotteryTextureManager.setBaseTexture(DecorateActivity.this.getResources(), R.drawable.procelain);
						youshangFlag = false;
						GLManager.pottery.setLum(1f);
						GLManager.background.setLum(1f);
						GLManager.table.setLum(1f);
					}else{
						GLManager.pottery.setLum(getFactor(-42));
						GLManager.background.setLum(getFactor(-84));
						GLManager.table.setLum(getFactor(-42));
						GLManager.mode = WTMode.INTERACT_VIEW;
						Intent intent = new Intent(DecorateActivity.this, PotteryFinishedActivity.class);
						intent.putExtra("type", potteryType);
						glView.onPause();
						startActivity(intent);
						DecorateActivity.this.overridePendingTransition(R.anim.activity_fade_in, R.anim.activity_fade_out);
					}
				}
				Watao.pauseBGM();
			};
		}.execute();
	}
	
	
	@Override
	protected void onStop() {
		if (GLManager.alreadyInit && needSave) {
			Object[] pattern = PotteryTextureManager.getPatterns().values().toArray();
			FileUtils.saveUncomplete(this, GLManager.pottery.getRadiuses()
					, GLManager.pottery.getVertices(), PotteryTextureManager.getOccupied()
					,Arrays.copyOf(pattern , pattern.length, Pattern[].class));
			
			PreferenceManager.getDefaultSharedPreferences(this)
			.edit()
			.putFloat("height", GLManager.pottery.getCurrentHeight())
			.putFloat("var", GLManager.pottery.getVarUsedForEllipseToRegular())
			.putInt("potteryType", potteryType)
			.putInt("hasUncomplete", 2)
			.commit();
		}
		needSave = true;
		super.onStop();
	}
	@Override
	protected void onResume() {
		super.onResume();
		GLManager.shadow.setTexture(this, R.drawable.shadow);
		GLManager.rotateSpeed = 0.06f;
		GLManager.setEyeOffset(0);
		GLManager.fire.setTexture(DecorateActivity.this, R.raw.fire0);
		if (isFirst) {
			PotteryTextureManager.changeBaseTexture(getResources(), R.drawable.clay);
			((Pottery200)GLManager.pottery).switchShader(Pottery200.DRY_CLAY);
			if (QINGHUA == potteryType) {
				setMenuVisibility(true);
				GLManager.mode = WTMode.DEROCATE;
				glView.setMode(WTMode.DEROCATE);
				GLManager.background.setTexture(this, R.drawable.decorate_background);
				GLManager.table.setTexture(this, R.drawable.shangyoumuwen);
				PotteryTextureManager.isBefore = true;
			}else{
				GLManager.table.setTexture(this, R.drawable.table);
				GLManager.background.setTexture(this, R.drawable.decorate_background);
				startFire(1280);
			}
			isFirst = false;
		}else{
			if (QINGHUA == potteryType) {
				PotteryTextureManager.isBefore = true;
				PotteryTextureManager.changeBaseTexture(getResources(), R.drawable.clay);
				((Pottery200)GLManager.pottery).switchShader(Pottery200.DRY_CLAY);
				setMenuVisibility(true);
				GLManager.mode = WTMode.DEROCATE;
				glView.setMode(WTMode.DEROCATE);
				GLManager.background.setTexture(this, R.drawable.decorate_background);
				GLManager.table.setTexture(this, R.drawable.shangyoumuwen);
				PotteryTextureManager.isBefore = true;
			}else{
				GLManager.table.setTexture(DecorateActivity.this, R.drawable.shangyoumuwen);
				GLManager.background.setTexture(DecorateActivity.this, R.drawable.decorate_background);
				PotteryTextureManager.changeBaseTexture(DecorateActivity.this.getResources(), R.drawable.procelain);
				((Pottery200)GLManager.pottery).switchShader(Pottery200.CI);			
			}
		}
		glView.onResume();
	}

	public static final int RESULT_LOAD_IMAGE = 10;
	public static final int CAMERA_TAKE = 20;
	
	public static final String KEY_IS_SHOW_PWD_TIP = "custom_decorate";
	private static final int RESULT_REQUEST_CODE = 10;
	private static final int IMAGE_REQUEST_CODE = 20;
	private static final int CAMERA_REQUEST_CODE = 30;
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//		if(requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && data != null){
//			Uri selectedImage = data.getData();
//			String[] filePathColumn = {MediaStore.Images.Media.DATA};
//			Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
//			cursor.moveToFirst();
//			
//			int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
//			String imagePath = cursor.getString(columnIndex);
//			cursor.close();
//			
//			addToCustomerImage(imagePath);
//		}else if(requestCode == CAMERA_TAKE && resultCode == RESULT_OK){
//			addToCustomerImage(new File(getExternalCacheDir(), fileNameForCamera).toString());
//		}
		
		// 结果码不等于取消时候
		if (resultCode != RESULT_CANCELED) {
			switch (requestCode) {
			case IMAGE_REQUEST_CODE:
				startPhotoZoom(data.getData());
				break;
			case CAMERA_REQUEST_CODE:
				File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);  
				File tempFile = new File(path,TEMP_PHOTO_FILE);
				startPhotoZoom(Uri.fromFile(tempFile));  
				break;
			case RESULT_REQUEST_CODE: // 图片缩放完成后
				if (data != null) {
					getImageToView(data);
				}
				break;
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	
	private class ChooseDecoratorType implements OnClickListener{

		private int index;
		public ChooseDecoratorType(int i) {
			index = i;
		}

		@Override
		public void onClick(View v) {
			eraseButton.setChecked(false);
			switch (index) {
			case 0:
				setDecorator(WTDecorateTypeEnum.DANDU);
				break;
			case 1:
				setDecorator(WTDecorateTypeEnum.HUANRAO);
				break;
			case 2:
				setDecorator(WTDecorateTypeEnum.CHONGFU);
				break;
			case 3:
				if (isShowPwdTip()) {
					final View view = getLayoutInflater().inflate(R.layout.custom_dialog, null);
					new AlertDialog.Builder(DecorateActivity.this).setView(view)
					.setTitle("自定义提示")
					.setPositiveButton("确定", new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							CheckBox cb = (CheckBox) view.findViewById(R.id.cb_isShow);
							if (cb.isChecked()) {
								closeShowPwdTip();
							}
						}

					}).show();
				}
				setDecorator(WTDecorateTypeEnum.CUSTOM);
				break;
			default:
				break;
			}
		}

	}
	
	private void getCustomPicture() {
		new AlertDialog.Builder(this).setTitle("从哪里获取图片").setItems(
				new CharSequence[] { "相册", "相机" }, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						if (which == 0) {
							Intent photoPickerIntent = new Intent( Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
							photoPickerIntent.setType("image/*");
							photoPickerIntent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
							photoPickerIntent.putExtra(MediaStore.EXTRA_OUTPUT, getTempUri());
							startActivityForResult(photoPickerIntent, IMAGE_REQUEST_CODE);
						}else{
							Intent intentFromCapture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
							intentFromCapture.putExtra("outputFormat",Bitmap.CompressFormat.JPEG.toString());
							intentFromCapture.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0);
							File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);  
							File tempFile = new File(path,TEMP_PHOTO_FILE);
							// 判断存储卡是否可以用，可用进行存储
							intentFromCapture.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(tempFile));
							startActivityForResult(intentFromCapture, CAMERA_REQUEST_CODE);
						}
						dialog.dismiss();
					}
				}).show();
	}
	private static SharedPreferences sharedPreferences;
	public boolean closeShowPwdTip() {
		if (sharedPreferences == null){
			sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
		}
		Editor editor = sharedPreferences.edit();
		editor.putBoolean(KEY_IS_SHOW_PWD_TIP, false);
		return editor.commit();
	}

	public boolean isShowPwdTip() {
		if (sharedPreferences == null){
			sharedPreferences = PreferenceManager
			.getDefaultSharedPreferences(this);
		}
		return sharedPreferences.getBoolean(KEY_IS_SHOW_PWD_TIP, true);
	}

	
	public static Integer[] qinghuaBeforeId = new Integer[]{R.drawable.q13, R.drawable.q21, R.drawable.q345, R.drawable.q44, R.drawable.q535, R.drawable.q645, R.drawable.q74};
	public static Integer[] qinghuaAfterId = new Integer[]{R.drawable.q13a, R.drawable.q21a, R.drawable.q345a, R.drawable.q44a, R.drawable.q535a, R.drawable.q645a, R.drawable.q74a};
	private float[] qinghuaWidthId = new float[]{0.375f,0.125f,0.5625f,0.5f,0.4375f,0.5625f,0.5f};
	
	public static Integer[] youshang = new Integer[]{R.drawable.y1, R.drawable.y2, R.drawable.y3, R.drawable.y4, R.drawable.y543, R.drawable.y626};
	private float[] youshangWidthId = new float[]{0.2f,0.3f,0.1f,0.4f, 0.5375f, 0.325f};
	
	private static List<String> customerId = new ArrayList<String>();
	
	List<ImageView> decoratorViews = new ArrayList<ImageView>(); 
	ImageView currentView;
	


	public void setDecorator(WTDecorateTypeEnum bw) {
		if (bw == WTDecorateTypeEnum.CUSTOM) {
			PotteryTextureManager.needP = true;
		}else{
			PotteryTextureManager.needP = false;
		}
		if (bw == WTDecorateTypeEnum.CUSTOM) {
			
			int needImageView = customerId.size() + 1;
			int avialableImageView = decoratorViews.size();
			adapterImageViewNumber(needImageView, avialableImageView);
			
			int i = 0;
			for (; i < customerId.size(); ++i) {
				String id = customerId.get(i);
				
				WTDecorator decorator = new WTDecorator();
				decorator.idAfter = id;
				decorator.idBefore = id;
				decorator.setWidth(0.6f);

				ImageView decoratorImageView = decoratorViews.get(i);
				changeImageView(decoratorImageView, decorator);

			}
			
			if (customerId.size() > 1) {
				PotteryTextureManager.currentDecorater = (WTDecorator) decoratorViews.get(0).getTag();
			}
			
			ImageView view = decoratorViews.get(i++);
			view.setImageResource(R.drawable.plus);
			view.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					getCustomPicture();
				}
			});
			view.setVisibility(View.VISIBLE);
			view.setBackgroundResource(R.drawable.decorate_block);

			for (; i < decoratorViews.size(); ++i) {
				decoratorViews.get(i).setVisibility(View.GONE);
			}
			
			return;		
		}
		
		float[] decorateWidth = null;
		Object[] decorateResourceBeforeId = null;
		Object[] decorateResourceAfterId = null;
		if(bw == WTDecorateTypeEnum.DANDU){
			if (potteryType == DecorateActivity.QINGHUA) {
				decorateResourceBeforeId = qinghuaBeforeId;
				decorateResourceAfterId = qinghuaAfterId;
				decorateWidth = qinghuaWidthId;
			}else{
				decorateResourceBeforeId = youshang;
				decorateResourceAfterId = youshang;
				decorateWidth = youshangWidthId;
			}
		}else if(bw == WTDecorateTypeEnum.HUANRAO){
			if (potteryType == DecorateActivity.QINGHUA) {
				decorateResourceBeforeId = qinghuaBeforeId;
				decorateResourceAfterId = qinghuaAfterId;
				decorateWidth = qinghuaWidthId;
			}else{
				decorateResourceBeforeId = youshang;
				decorateResourceAfterId = youshang;
				decorateWidth = youshangWidthId;
			}
		}else if(bw == WTDecorateTypeEnum.CHONGFU){
			if (potteryType == DecorateActivity.QINGHUA) {
				decorateResourceBeforeId = qinghuaBeforeId;
				decorateResourceAfterId = qinghuaAfterId;
				decorateWidth = qinghuaWidthId;
			}else{
				decorateResourceBeforeId = youshang;
				decorateResourceAfterId = youshang;
				decorateWidth = youshangWidthId;
			}
		}
		
		int needImageView = decorateResourceBeforeId.length;
		int avialableImageView = decoratorViews.size();
		adapterImageViewNumber(needImageView, avialableImageView);
		int i;
		for(i = 0; i < decorateResourceBeforeId.length; ++i){
			WTDecorator decorator = new WTDecorator();
			decorator.setWidth(decorateWidth[i]);
			decorator.idAfter = decorateResourceAfterId[i].toString();
			decorator.idBefore = decorateResourceBeforeId[i].toString();
			changeImageView(decoratorViews.get(i),decorator);
		}	

		for (; i < decoratorViews.size(); ++i) {
			decoratorViews.get(i).setVisibility(View.GONE);
		}
		
		switchDecorate(decoratorViews.get(0));
	}

	@Override
	public void onPause() {
		glView.onPause();
		super.onPause();
	}

	public void addToCustomerImage(Bitmap bitmap) {
		int needImageView = customerId.size() + 2;
		int avialableImageView = decoratorViews.size();
		adapterImageViewNumber(needImageView, avialableImageView);
//		Options opts = new Options();
		try {
//			opts.inSampleSize = 1;
//			opts.inJustDecodeBounds = true;
//			byte[] data = fileToByteArray(new File(imagePath));
//			BitmapFactory.decodeByteArray(data, 0, data.length, opts);
//			int outWidth = opts.outWidth;
//			int wFactor = outWidth / 100;
//			int outHeight = opts.outHeight;
//			int hFactor = outHeight / 100;
//			int factor = wFactor > hFactor ? wFactor : hFactor;
//			
//			opts.inSampleSize = factor;
//			opts.inJustDecodeBounds = false;
//			Bitmap bitmap =	BitmapFactory.decodeByteArray(data, 0, data.length, opts);
			Bitmap bitmap2 = Bitmap.createBitmap(bitmap.getWidth() * 8, bitmap.getHeight(), Config.ARGB_8888);
			bitmap2.eraseColor(Color.TRANSPARENT);
			Canvas c = new Canvas(bitmap2);
			c.drawBitmap(bitmap, bitmap.getWidth()*3.5f, 0, null);
			bitmap.recycle();
			c.save(Canvas.ALL_SAVE_FLAG);
			c.restore();

			String id =  "up" + Long.valueOf(System.currentTimeMillis()).toString();
			OutputStream os = openFileOutput(id, Context.MODE_PRIVATE);
			bitmap2.compress(CompressFormat.PNG, 90, os);
			bitmap2.recycle();
			customerId.add(id);

			ImageView decoratorImageView = decoratorViews.get(customerId.size() - 1);
			WTDecorator decorator = new WTDecorator();
			decorator.idAfter = decorator.idBefore = id;
			decorator.setWidth(0.725f);
			changeImageView(decoratorImageView, decorator);
			switchDecorate(decoratorViews.get(0));

			PotteryTextureManager.currentDecorater = decorator;

			ImageView view = decoratorViews.get(customerId.size());
			view.setImageResource(R.drawable.plus);
			view.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					getCustomPicture();
				}
			});
			view.setVisibility(View.VISIBLE);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void adapterImageViewNumber(int needImageView,
			int avialableImageView) {
		if (needImageView > avialableImageView) {
			for (int i = 0; i < needImageView - avialableImageView; ++i) {
				ImageView view = (ImageView) getLayoutInflater().inflate(R.layout.decorate_image, decoratorDisplayView, false);
				decoratorDisplayView.addView(view);
				decoratorViews.add(view);
			}
		}
	}


	private void changeImageView(ImageView decoratorImageView,
			WTDecorator decorator) {
		decoratorImageView.setVisibility(View.VISIBLE);
		if (potteryType == QINGHUA) {
			decoratorImageView.setImageBitmap(PotteryTextureManager.getPatternTexture(decorator.idBefore));
		}else{
			decoratorImageView.setImageBitmap(PotteryTextureManager.getPatternTexture(decorator.idAfter));
		}
		decoratorImageView.setTag(decorator);
		decoratorImageView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				switchDecorate(v);
			}
			
		});
	}
	
	private void switchDecorate(View v) {
		if (eraseButton != null) {
			eraseButton.setChecked(false);
		}
		PotteryTextureManager.currentDecorater = (WTDecorator)v.getTag();
		if (currentView != null) {
			currentView.setBackgroundResource(R.drawable.decorate_block);
		}
		currentView = (ImageView) v;
		currentView.setBackgroundResource(R.drawable.decorate_block_choosed);
		PotteryTextureManager.isEraseMode = false;
	}
	
	private byte[] fileToByteArray(File file) throws IOException {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		FileInputStream fis = new FileInputStream(file);
		byte[] buff = new byte[1024];
		int len = 0;
		while ( (len = fis.read(buff)) != -1) {
			outputStream.write(buff, 0, len);
		}
		fis.close();
		outputStream.close();
		return outputStream.toByteArray();
	}


	public void setMenuVisibility(boolean isShow) {
		if(isShow){
			showMenu();
			decoratorDisplayScrollView.setVisibility(View.VISIBLE);
			decoratorTypes.setVisibility(View.VISIBLE);
		}else{
			hideMenu();
			decoratorDisplayScrollView.setVisibility(View.GONE);
			decoratorTypes.setVisibility(View.GONE);
		}
	}

	private void hideMenu() {
		nextButton.setVisibility(View.GONE);
		eraseButton.setVisibility(View.GONE);
		backButton.setVisibility(View.GONE);
	}


	private void showMenu() {
		nextButton.setVisibility(View.VISIBLE);
		eraseButton.setVisibility(View.VISIBLE);
		backButton.setVisibility(View.VISIBLE);
	}


	public GLView getGLView() {
		return glView;
	}


	public void setPotteryType(int potteryType) {
		this.potteryType = potteryType;
		setDecorator(WTDecorateTypeEnum.DANDU);
	}
	
	
	/**
	 * 裁剪图片方法实现
	 * 
	 * @param uri
	 */
	public void startPhotoZoom(Uri uri) {
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		// 设置裁剪
		// aspectX aspectY 是宽高的比例
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);

		// outputX outputY 是裁剪图片宽高
		intent.putExtra("outputX", 100);
		intent.putExtra("outputY", 100);
		intent.putExtra("scale", true);
		intent.putExtra("return-data", false);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
		intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
		intent.putExtra("noFaceDetection", true); // no face detection
		startActivityForResult(intent, RESULT_REQUEST_CODE);
	}

	/**
	 * 保存裁剪之后的图片数据
	 * 
	 * @param picdata
	 */
	private void getImageToView(Intent data) {
		Bundle extras = data.getExtras();
		if (extras != null) {
			System.out.println("ok");
			Bitmap photo = decodeUriAsBitmap(imageUri);// decode bitmap
			addToCustomerImage(photo);
//			imageView.setImageBitmap(photo);
		}
	}

	private Uri getTempUri() {
		return Uri.fromFile(getTempFile());
	}

	private static final String TEMP_PHOTO_FILE = "temporary_holder.jpg";

	private File getTempFile() {
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			File file = new File(Environment.getExternalStorageDirectory(),
					TEMP_PHOTO_FILE);
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return file;
		} else {
			return null;
		}
	}

	Uri imageUri = getTempUri(); // The Uri to store the big bitmap

	private Bitmap decodeUriAsBitmap(Uri uri) {
		Bitmap bitmap = null;
		try {
			bitmap = BitmapFactory.decodeStream(getContentResolver()
					.openInputStream(uri));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}
		return bitmap;
	}

}
