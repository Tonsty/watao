package com.chillax.mytest;

import org.zju.cadcg.watao.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.chillax.service.landDivideServeice;

public class MyTest extends Activity {

	private TimeCount time;
	private int next = -1;
	private TextView  nextTextView;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.mytest);
		
		Intent i = new Intent(this, landDivideServeice.class);
		startService(i);
		
		time = new TimeCount(20000, 1000);
		time.start();
		
		nextTextView = (TextView) this.findViewById(R.id.next_id);
		nextTextView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				
				if(next == 0){
					
					Intent intent = new Intent(getBaseContext(),PersonAddress.class);
					startActivity(intent);
					
				} else {
					Toast.makeText(getBaseContext(), "那么快看完啦,再看一会啦", Toast.LENGTH_SHORT).show();
				}
				
			}
		});
	}

	/* 定义一个倒计时的内部类 */
	class TimeCount extends CountDownTimer {
		public TimeCount(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);
		}

		@Override
		public void onFinish() {  //倒计时执行结束时操作
			next = 0;
		}

		@Override
		public void onTick(long millisUntilFinished) {  //倒计执行时操作
		}
	}
	
}
