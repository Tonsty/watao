package org.zju.cadcg.watao.activity;


import java.io.FileNotFoundException;

import org.zju.cadcg.watao.R;
import org.zju.cadcg.watao.gl.GLView;
import org.zju.cadcg.watao.gl200.Pottery200;
import org.zju.cadcg.watao.gl200.Table200;
import org.zju.cadcg.watao.type.WTMode;
import org.zju.cadcg.watao.utils.FileUtils;
import org.zju.cadcg.watao.utils.FileUtils.PotterySaved;
import org.zju.cadcg.watao.utils.GLManager;
import org.zju.cadcg.watao.utils.NumberUntil;
import org.zju.cadcg.watao.utils.PotteryTextureManager;

import com.umeng.socialize.bean.RequestType;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.weixin.controller.UMWXHandler;
import com.umeng.socialize.weixin.media.CircleShareContent;
import com.umeng.socialize.weixin.media.WeiXinShareContent;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ToggleButton;


public class PotteryFinishedActivity extends Activity {
	final UMSocialService mController = UMServiceFactory.getUMSocialService("com.umeng.share", RequestType.SOCIAL);	
	
	private GLView glView;
	private String from;
	private String fileName = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle extras = getIntent().getExtras();
		from = extras.getString("fromcolloct",null);
		if (from != null) {
			fileName = extras.getString("fileName");
			Bitmap bitmap = null;
			try {
				bitmap = BitmapFactory.decodeStream(openFileInput(fileName + "1.png"));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			PotteryTextureManager.setBaseTexture(bitmap);
			float[] vertices = (float[]) FileUtils.getSerializable(this, fileName);
			GLManager.pottery.setVertices(vertices);		
			GLManager.pottery.fastEstimateNormals();
			
		}else{
			PotteryTextureManager.changeBaseTexture(this.getResources(), R.drawable.procelain);
		}
		
		PreferenceManager.getDefaultSharedPreferences(this).edit().putInt("hasUncomplete", 0).commit();
	
		initUI();
		initUMeng();
	}

	private Bitmap image;

	private ToggleButton collect;
	private void initUMeng() {
		
		String appId = "wx15592023243a88b0";
//		String appId = "wx967daebe835fbeac";
		// 添加微信平台
		UMWXHandler wxHandler = new UMWXHandler(this,appId);
		wxHandler.addToSocialSDK();
		
		// 支持微信朋友圈
		UMWXHandler wxCircleHandler = new UMWXHandler(this,appId);
		wxCircleHandler.setToCircle(true);
		wxCircleHandler.addToSocialSDK();
		

		mController.setShareContent("这是我用哇陶新作的瓷器，超级好玩的哦!");
		// 设置分享图片, 参数2为图片的url地址
		mController.setShareMedia(new UMImage(this, 
				"http://www.umeng.com/images/pic/banner_module_social.png"));
		//为了保证人人分享成功且能够在PC上正常显示，请设置website                                      
		mController.setAppWebSite(SHARE_MEDIA.RENREN, "http://www.umeng.com/social");
		
		// 移除平台
		// mController.getConfig().removePlatform( SHARE_MEDIA.RENREN, SHARE_MEDIA.DOUBAN);
		// 设置分享平台面板中显示平台的顺序
		mController.getConfig().setPlatformOrder(SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE, SHARE_MEDIA.SINA);

	}
	
	long time;
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && from == null) {
			if (System.currentTimeMillis() - time > 2500) {
				Toast.makeText(this, "再按一次推出程序", Toast.LENGTH_SHORT).show();
				time = System.currentTimeMillis();
			}else{
				Intent intent = new Intent(Intent.ACTION_MAIN);
				intent.addCategory(Intent.CATEGORY_HOME);
				startActivity(intent);
			}
			return true;
		}else{
			return super.onKeyDown(keyCode, event);
		}
	}

	private void initUI() {
		setContentView(R.layout.activity_pottery_finished);
		
		glView = (GLView) findViewById(R.id.pottery);
		
		findViewById(R.id.back_to_home_page_button).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (!collect.isChecked() && from == null) {
					new AlertDialog.Builder(PotteryFinishedActivity.this).setMessage("作品尚未收藏，是否继续？").setPositiveButton("是", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							startActivity(new Intent(PotteryFinishedActivity.this, ShapeActivity.class).putExtra("backToMain", true));
						}

					}).setNegativeButton("否",null).show();
				}else{
					startActivity(new Intent(PotteryFinishedActivity.this, ShapeActivity.class).putExtra("backToMain", true));
				}
			}
		});
		
		findViewById(R.id.buy_button).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				image = GLManager.getImage(240, 430);
				GLManager.tempImage = image;
				Intent intent = new Intent(PotteryFinishedActivity.this, BuyActivity.class);
				PotteryFinishedActivity.this.startActivity(intent);
			}
		});
		findViewById(R.id.share_button).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				image = GLManager.getImage(90, 160);
				WeiXinShareContent weixinContent = new WeiXinShareContent();
				weixinContent.setShareContent("这是我用哇陶新作的瓷器，超级好玩的哦!");
				weixinContent.setTitle("哇陶分享");
				weixinContent.setTargetUrl("http://www.google.com");
				weixinContent.setShareImage(new UMImage(PotteryFinishedActivity.this, image));
				mController.setShareMedia(weixinContent);
				
				CircleShareContent circleMedia = new CircleShareContent();
				circleMedia.setShareContent("这是我用哇陶新作的瓷器，超级好玩的哦!");
				circleMedia.setTitle("哇陶分享");
				circleMedia.setShareImage(new UMImage(PotteryFinishedActivity.this, image));
				circleMedia.setTargetUrl("http://www.google.com");
				mController.setShareMedia(circleMedia);
				
				mController.openShare(PotteryFinishedActivity.this, false);
			}
		});
		collect = (ToggleButton) findViewById(R.id.take_photo);
		if (fileName != null) {
			collect.setChecked(true);
		}
		collect.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					toast = Toast.makeText(PotteryFinishedActivity.this, "正在收藏...", Toast.LENGTH_LONG);
					toast.show();
					saveCollect();
				}else{
					deleteCollect();
					Toast.makeText(PotteryFinishedActivity.this,"已移除收藏", Toast.LENGTH_SHORT).show();		
				}
			}

			
		});
		
	
		ImageView width = (ImageView) findViewById(R.id.width);
		int widthF = (int) GLManager.pottery.getMaxWidth();
		Bitmap bitmapWidth = NumberUntil.getNumberBitmapI(this, widthF);
		width.setImageBitmap(bitmapWidth);
		
		ImageView height = (ImageView) findViewById(R.id.height);
		int heightF = (int) GLManager.pottery.getHeightReal();
		Bitmap bitmapHeight = NumberUntil.getNumberBitmapI(this, heightF);
		height.setImageBitmap(bitmapHeight);
		
	}
	
	private Toast toast;
	private void saveCollect() {
		new Thread(new Runnable() {


			@Override
			public void run() {
				image = GLManager.getImage(1200, 2100);
				PotterySaved ps = new PotterySaved();
				ps.texture = PotteryTextureManager.getTexture();
				ps.vertices = GLManager.pottery.getVertices();
				ps.image = image;
				fileName = FileUtils.savePottery(PotteryFinishedActivity.this, ps);
				PotteryFinishedActivity.this.runOnUiThread(new Runnable() {

					@Override
					public void run() {
						toast.cancel();
						Toast.makeText(PotteryFinishedActivity.this,"收藏成功,可在首页我的收藏查看", Toast.LENGTH_SHORT).show();		
					}
				});
			}
		}).start();
	}
	
	private void deleteCollect() {
		while(fileName == null){
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		FileUtils.deletePottery(this, fileName);

	}
	
	@Override 
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    super.onActivityResult(requestCode, resultCode, data);
	    /**使用SSO授权必须添加如下代码 */
//	    UMSsoHandler ssoHandler = mController.getConfig().getSsoHandler(requestCode) ;
//	    if(ssoHandler != null){
//	       ssoHandler.authorizeCallBack(requestCode, resultCode, data);
//	    }
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		glView.onResume();
		glView.setMode(WTMode.INTERACT_VIEW);
		GLManager.setEyeOffset(0.0f);
		GLManager.rotateSpeed = 0.0f;
		GLManager.mode = WTMode.INTERACT_VIEW;
		GLManager.pottery.switchShader(Pottery200.CI);
		GLManager.table.switchShader(Table200.COMMON);
		GLManager.background.setTexture(this, R.drawable.pottery_finished_activity_background);
		GLManager.shadow.setTexture(this, R.drawable.shadow);
		GLManager.table.setTexture(this, R.drawable.finish_table);
		GLManager.pottery.setLum(1f);
		GLManager.background.setLum(1f);
		GLManager.table.setLum(1f);
	
	}
	@Override
	protected void onPause() {
		super.onPause();
		glView.onPause();
	}

}
