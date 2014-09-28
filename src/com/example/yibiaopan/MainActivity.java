package com.example.yibiaopan;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;

/**
 * @author : 首席 author -- 胡帅冰 此人完善大部分功能
 * @author : 马俊 ---- 呕血完善核心代码
 * @date :2014年9月25日下午4:53:16
 * @version:v1.0+
 * @FileName:MainActivity.java
 * @ProjectName:仪表盘demo
 * @PackageName:com.example.yibiaopan
 * @EnclosingType: {@link https://github.com/flyme2012}
 * @EnclosingType: {@link https://github.com/majunm}
 * @Description:仪表盘用例
 */
public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// DashboardView customView = new DashboardView(this);
		// customView.executeTask(new int[] { 0, 6000, 12000, 24000, 36000,
		// 38000,
		// 40000 }, 1008881, 2899800);
		// setContentView(customView);
		setContentView(R.layout.main);
		DashboardView dashBoardView = (DashboardView) findViewById(R.id.mini_dash_board);

		dashBoardView.executeTask(new int[] { 0, 6000, 12000, 24000, 36000,
				38000, 40000 }, 10001, 39000);
	}

}
