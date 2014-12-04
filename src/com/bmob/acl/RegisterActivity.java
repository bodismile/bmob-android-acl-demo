package com.bmob.acl;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import cn.bmob.v3.BmobRole;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.SaveListener;

import com.bmob.acl.bean.User;
import com.bmob.acl.util.CommonUtils;

public class RegisterActivity extends BaseActivity {

	Button btn_register;
	EditText et_username, et_password, et_email;

	RadioButton rb_android, rb_ios;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);

		initTopBarForLeft("ע��");

		et_username = (EditText) findViewById(R.id.et_username);
		et_password = (EditText) findViewById(R.id.et_password);
		et_email = (EditText) findViewById(R.id.et_email);

		rb_android = (RadioButton) findViewById(R.id.rb_android);
		rb_ios = (RadioButton) findViewById(R.id.rb_ios);

		btn_register = (Button) findViewById(R.id.btn_register);
		btn_register.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				register();
			}
		});
	}

	private void register() {

		String name = et_username.getText().toString();
		String password = et_password.getText().toString();
		String pwd_again = et_email.getText().toString();

		if (TextUtils.isEmpty(name)) {
			ShowToast(R.string.toast_error_username_null);
			return;
		}

		if (TextUtils.isEmpty(password)) {
			ShowToast(R.string.toast_error_password_null);
			return;
		}
		if (!pwd_again.equals(password)) {
			ShowToast(R.string.toast_error_comfirm_password);
			return;
		}

		boolean isNetConnected = CommonUtils.isNetworkAvailable(this);
		if (!isNetConnected) {
			ShowToast(R.string.network_tips);
			return;
		}

		final ProgressDialog progress = new ProgressDialog(
				RegisterActivity.this);
		progress.setMessage("����ע��...");
		progress.setCanceledOnTouchOutside(false);
		progress.show();

		final User bu = new User();
		bu.setUsername(name);
		bu.setPassword(password);
		bu.signUp(RegisterActivity.this, new SaveListener() {

			@Override
			public void onSuccess() {
				// TODO Auto-generated method stub
				progress.dismiss();
				ShowToast("ע��ɹ�");
				//��ӵ��û�����
				addRole();
				// ������ҳ
				Intent intent = new Intent(RegisterActivity.this,
						MainActivity.class);
				startActivity(intent);
				finish();

			}

			@Override
			public void onFailure(int arg0, String arg1) {
				// TODO Auto-generated method stub
				ShowToast("ע��ʧ��:" + arg1);
				progress.dismiss();
			}
		});
	}

	/** ��ӽ�ɫ
	  * @Title: addRole
	  * @Description: TODO
	  * @param  
	  * @return void
	  * @throws
	  */
	private void addRole() {
		//��android ��ios�����û���
		//ע��BmobRole�Ľ�ɫ����-����ΪӢ��,���Ļᴴ�����ɹ�
		BmobRole androidRole = new BmobRole("android");
		BmobRole iosRole = new BmobRole("ios");
		//����ǰ�û���ӵ���ѡ����û�����
		User user = BmobUser.getCurrentUser(this, User.class);
		
		if (rb_android.isChecked()) {
			androidRole.getUsers().add(user);
			androidRole.save(this,new SaveListener() {
				
				@Override
				public void onSuccess() {
					// TODO Auto-generated method stub
					Log.i("ACL", "success");
				}
				
				@Override
				public void onFailure(int code, String msg) {
					// TODO Auto-generated method stub
					Log.i("ACL", "ʧ�ܣ�"+msg);
				}
			});
		} else if (rb_ios.isChecked()) {
			iosRole.getUsers().add(user);
			iosRole.save(this,new SaveListener() {
				
				@Override
				public void onSuccess() {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void onFailure(int code, String msg) {
					// TODO Auto-generated method stub
					
				}
			});
		}
	}

}
