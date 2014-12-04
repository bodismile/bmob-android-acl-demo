package com.bmob.acl;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.CountListener;
import cn.bmob.v3.listener.FindListener;

import com.bmob.acl.adapter.BlogAdapter;
import com.bmob.acl.bean.Blog;
import com.bmob.acl.view.listview.SimpleFooter;
import com.bmob.acl.view.listview.SimpleHeader;
import com.bmob.acl.view.listview.ZrcListView;
import com.bmob.acl.view.listview.ZrcListView.OnItemClickListener;
import com.bmob.acl.view.listview.ZrcListView.OnStartListener;

public class MainActivity extends BaseActivity implements OnClickListener,OnItemClickListener{

	private ZrcListView listView;
	BlogAdapter adapter;
	
	List<Blog> lists = new ArrayList<Blog>();

	private Handler handler = new Handler();
	
	TextView tv_submit,tv_logout;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initView();
	}

	private void initView() {
		tv_logout = (TextView)findViewById(R.id.tv_logout);
		tv_logout.setOnClickListener(this);
		
		tv_submit = (TextView)findViewById(R.id.tv_submit);
		tv_submit.setOnClickListener(this);
		listView = (ZrcListView) findViewById(R.id.zListView);
		// ����Ĭ��ƫ��������Ҫ����ʵ��͸�����������ܡ�����ѡ��
		float density = getResources().getDisplayMetrics().density;
		listView.setFirstTopOffset((int) (50 * density));

		// ��������ˢ�µ���ʽ����ѡ�������û��Header���޷�����ˢ�£�
		SimpleHeader header = new SimpleHeader(this);
		header.setTextColor(0xff0066aa);
		header.setCircleColor(0xff33bbee);
		listView.setHeadable(header);

		// ���ü��ظ������ʽ����ѡ��
		SimpleFooter footer = new SimpleFooter(this);
		footer.setCircleColor(0xff33bbee);
		listView.setFootable(footer);

		// �����б�����ֶ�������ѡ��
		listView.setItemAnimForTopIn(R.anim.topitem_in);
		listView.setItemAnimForBottomIn(R.anim.bottomitem_in);

		// ����ˢ���¼��ص�����ѡ��
		listView.setOnRefreshStartListener(new OnStartListener() {
			@Override
			public void onStart() {
				queryAllBlogs(0);
			}
		});

		// ���ظ����¼��ص�����ѡ��
		listView.setOnLoadMoreStartListener(new OnStartListener() {
			@Override
			public void onStart() {
				queryMoreBlogs();
			}
		});
		
		listView.setOnItemClickListener(this);
		
		adapter = new BlogAdapter(this, lists);
		listView.setAdapter(adapter);
		
		listView.refresh(); // ��������ˢ��
	}

	int curPage = 0;
	int limit = 10;
	
	/**
	 * ��ѯ�ʺϵ�ǰ�û�Ȩ�޺ͽ�ɫ�Ĳ��ͣ�����Ĳ�ѯ�ǽ�ϵ�ǰ�û��������û��飨BmobRole���Ͳ��͵Ķ�дȨ������ѯ���������Ĳ��ͣ�
	 * ���ͣ����ĳƪ���µĶ�дȨ���ǣ�ֻ����android����˲��ܿ���ֻ���ڲ��ͷ����߲���д�����������ô���android�飨ע��ʱ��ѡ��ģ�����ô��ʱ������ܿ�����ƪ�����ˡ�
	 * 
	 * ����Ҫ��д��������Ĳ�ѯ�������ɷ���˸���ACL���û���ɫ���Զ���������
	 * @Title: queryAllBlogs
	 * @Description: TODO
	 * @param
	 * @return void
	 * @throws
	 */
	private void queryAllBlogs(final int page) {
		BmobQuery<Blog> query = new BmobQuery<Blog>();
		query.include("author,ACL");
		query.setLimit(limit);
		query.setSkip(page * limit);
		query.order("-createdAt");
		curPage = page;
		query.findObjects(this, new FindListener<Blog>() {

			@Override
			public void onSuccess(List<Blog> arg0) {
				// TODO Auto-generated method stub
				if (arg0 != null && arg0.size() > 0) {
					if(page==0){
						lists.clear();
					}
					lists.addAll(arg0);
					adapter.notifyDataSetChanged();
					if (arg0.size() < limit) {
						ShowToast("�����Ѽ������");
					} else {
						listView.startLoadMore(); // ����LoadingMore����
					}
                    listView.setRefreshSuccess("���سɹ�"); // ֪ͨ���سɹ�
				}else{
					listView.setRefreshFail("���޲���");
				}
			}
			
			@Override
			public void onError(int arg0, String arg1) {
				// TODO Auto-generated method stub
				handler.postDelayed(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						listView.setRefreshFail("����ʧ��");
					}
				}, 1000);
			}
		});
	}
	
	private void queryMoreBlogs() {
		BmobQuery<Blog> query = new BmobQuery<Blog>();
		query.count(this, Blog.class, new CountListener() {

			@Override
			public void onSuccess(int arg0) {
				// TODO Auto-generated method stub
				if (arg0 > lists.size()) {
					curPage++;
					queryAllBlogs(curPage);
				} else {
					 listView.stopLoadMore();
				}
			}

			@Override
			public void onFailure(int arg0, String arg1) {
				// TODO Auto-generated method stub
				 listView.stopLoadMore();
				 ShowToast("����ʧ�ܣ�"+arg1);
			}
		});

	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		if(arg0==tv_logout){
			BmobUser.logOut(this);
			startAnimActivity(LoginActivity.class);
			finish();
		}else{
			Intent intent = new Intent(this, SubmitActivity.class);
			startActivityForResult(intent, SubmitActivity.REQUEST_CODE_SUBMIT);
		}
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		switch (requestCode) {
		case SubmitActivity.REQUEST_CODE_SUBMIT:
			if (resultCode == SubmitActivity.RESULT_CODE_SUBMIT) {
				listView.refresh();
			}
			break;

		default:
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onItemClick(ZrcListView parent, View view, int position, long id) {
		// TODO Auto-generated method stub
//		Blog blog =lists.get(position);
//		Map<String, Object> map  = blog.getAcl();
//		ShowLog(""+map.toString());
//		String objectId = BmobUser.getCurrentUser(MainActivity.this, User.class).getObjectId();
//		if(map.containsKey(objectId)){
//			ShowLog(""+map.get(objectId));
//		}
	}
	
}
