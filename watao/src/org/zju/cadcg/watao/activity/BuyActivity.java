package org.zju.cadcg.watao.activity;

import org.zju.cadcg.watao.R;
import org.zju.cadcg.watao.utils.AndroidBug5497Workaround;
import org.zju.cadcg.watao.utils.GLManager;
import org.zju.cadcg.watao.utils.NumberUntil;

import com.chillax.mytest.AddressChoose;

import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class BuyActivity extends Activity {


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initUI();
	}

	private void initUI() {
		setContentView(R.layout.activity_buy);
//		AndroidBug5497Workaround.assistActivity(this);
		findViewById(R.id.back).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				BuyActivity.this.finish();
			}
		});
		
		ImageView image = (ImageView) findViewById(R.id.buy_image);
		image.setImageBitmap(GLManager.tempImage);
		
		final View icon = findViewById(R.id.buy_icon);
		icon.setVisibility(View.GONE);
			
			

		Button confirm = (Button) findViewById(R.id.buy_submit);
		confirm.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				icon.setScaleX(2);
				icon.setScaleY(2);
				icon.setVisibility(View.VISIBLE);
				icon.animate().setDuration(700).scaleX(1).scaleY(1).alpha(1).start();
			}
		});
		
		TextView price = (TextView) findViewById(R.id.buy_price);
		price.setText(String.valueOf(GLManager.getPrice()));
		
		findViewById(R.id.buy_address).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent=new Intent(BuyActivity.this, AddressChoose.class);
				startActivity(intent);
			}
		});
		
	}

}