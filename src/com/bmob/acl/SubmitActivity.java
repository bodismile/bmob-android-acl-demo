package com.bmob.acl;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import cn.bmob.v3.BmobACL;
import cn.bmob.v3.BmobRole;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.SaveListener;

import com.bmob.acl.bean.Blog;
import com.bmob.acl.bean.User;
import com.bmob.acl.view.HeaderLayout.onRightImageButtonClickListener;

/** ��������
  * @ClassName: SubmitActivity
  * @Description: TODO
  * @author smile
  * @date 2014-9-25 ����5:47:30
  */
public class SubmitActivity extends BaseActivity{

	EditText edit_title,edit_content;
	
	RadioGroup radio;
	
	RadioButton rb_all,rb_other;
	RadioButton rb_android,rb_ios,rb_me;
	
	LinearLayout layout;
	
	public final static int REQUEST_CODE_SUBMIT =1;
	public final static int RESULT_CODE_SUBMIT = 2;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_submit);
		initTopBarForBoth("������", R.drawable.base_action_bar_true_bg_selector, new onRightImageButtonClickListener() {
			
			@Override
			public void onClick() {
				// TODO Auto-generated method stub
				submit();
			}
		});
		initView();
	}
	
	private void initView(){
		layout = (LinearLayout)findViewById(R.id.layout);
		
		edit_title = (EditText)findViewById(R.id.edit_title);
		edit_content = (EditText)findViewById(R.id.edit_content);
		radio = (RadioGroup)findViewById(R.id.radio);
		radio.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(RadioGroup arg0, int arg1) {
				// TODO Auto-generated method stub
				if(arg1==R.id.rb_all){
					layout.setVisibility(View.GONE);
				}else if(arg1==R.id.rb_other){
					layout.setVisibility(View.VISIBLE);
				}
			}
		});
		
		rb_all = (RadioButton)findViewById(R.id.rb_all);
		rb_other = (RadioButton)findViewById(R.id.rb_other);
		
		rb_android = (RadioButton)findViewById(R.id.rb_android);
		rb_ios = (RadioButton)findViewById(R.id.rb_ios);
		rb_me = (RadioButton)findViewById(R.id.rb_me);
	}
	
	/** ��������
	  * @Title: submit
	  * @Description: TODO
	  * @param  
	  * @return void
	  * @throws
	  */
	private void submit(){
		String title = edit_title.getText().toString();
		String content = edit_content.getText().toString();
		
		if (TextUtils.isEmpty(title)) {
			ShowToast(R.string.toast_error_title);
			return;
		}

		if (TextUtils.isEmpty(content)) {
			ShowToast(R.string.toast_error_content);
			return;
		}
		
		Blog blog = new Blog();
		blog.setTitle(title);
		blog.setContent(content);
		
		BmobACL acl = new BmobACL();    //����һ��ACL����
		
		//���ÿɶ���д��Ȩ��--�����
		if(rb_all.isChecked()){//�����˿ɼ�
			acl.setPublicReadAccess(true);    //���������˿ɶ���Ȩ��
		}else if(rb_other.isChecked()){//����
			//���ý�ɫ����Ȩ��-�����
			if(rb_android.isChecked()){//��ƪ����ֻ������android���������������˲ſ��Կ��õ�
				BmobRole androidRole = new BmobRole("android");
				acl.setRoleReadAccess(androidRole, true);
				blog.setCategory("android");
			}else if(rb_ios.isChecked()){//��ƪ����ֻ������ios���������������˲ſ��Կ��õ�
				BmobRole iosRole = new BmobRole("ios");
				acl.setRoleReadAccess(iosRole, true);
				blog.setCategory("ios");
			}else if(rb_me.isChecked()){//���Լ��ɼ�
				acl.setReadAccess(BmobUser.getCurrentUser(this), true); // ���õ�ǰ�û��ɶ���Ȩ��
			}
		}
		acl.setWriteAccess(BmobUser.getCurrentUser(this), true);// ���õ�ǰ�û���д��Ȩ��
		//��������Ϊ��ǰ�û�
		blog.setAuthor(BmobUser.getCurrentUser(this, User.class));
		blog.setACL(acl);    //�����������ݵ�ACL��Ϣ
		blog.save(this, new SaveListener() {
		    @Override
		    public void onSuccess() {
		        //��ӳɹ�
		    	ShowToast("���·����ɹ�");
		    	Intent intent = new Intent(getApplicationContext(),MainActivity.class);
				setResult(RESULT_CODE_SUBMIT, intent);
				finish();
		    }
		    @Override
		    public void onFailure(int code, String msg) {
		        //���ʧ��
		    	ShowToast("���·���ʧ�ܣ�"+msg);
		    }
		});
	}
	
}
