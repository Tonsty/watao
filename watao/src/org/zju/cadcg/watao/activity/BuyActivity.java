package org.zju.cadcg.watao.activity;

import org.zju.cadcg.watao.R;
import org.zju.cadcg.watao.utils.GLManager;

import com.chillax.mytest.AddressChoose;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class BuyActivity extends Activity {


	private EditText et;

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
		
		findViewById(R.id.buy_address).setOnTouchListener(new OnTouchListener() {
			int touch_flag = 0;
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				++ touch_flag;
				if (touch_flag == 2) {
					Intent intent=new Intent(BuyActivity.this, AddressChoose.class);
					startActivityForResult(intent, 10086);
					touch_flag = 0;
				}
				return false;
			}
		});
		
		et = (EditText) findViewById(R.id.buy_address);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			String address = data.getStringExtra("address");
			et.setText(address);
		}
	}

}