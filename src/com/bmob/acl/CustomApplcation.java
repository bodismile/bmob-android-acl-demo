package com.bmob.acl;

import android.app.Application;

/**
 * �Զ���ȫ��Applcation��
 * @ClassName: CustomApplcation
 * @Description: TODO
 * @author smile
 * @date 2014-5-19 ����3:25:00
 */
public class CustomApplcation extends Application {

	public static CustomApplcation mInstance;

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		mInstance = this;
	}

	public static CustomApplcation getInstance() {
		return mInstance;
	}

}
