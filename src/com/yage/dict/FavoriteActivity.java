package com.yage.dict;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.yage.dict.R.id;
import com.yage.dict.db.DatabaseHelper;
import com.yage.dict.entity.FavoriteWord;
import org.yage.dict.star.WordPosition;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

/**
 * ������ʾ����ʷ���͡��ղء�
 * @author voyage
 * @since 2014-1-11 23:02:12
 */
public class FavoriteActivity extends Activity {

	//��ʾ���ʵ������Ƿ��ǡ��ղء��������ǡ���ʷ��
	public static final String TYPE_IS_FAVORITE="is_type_favorite";
	
	//�Ƿ�Ҫ��ʾ�ղأ�������ʷ
	private boolean isFavorite;
	
	//�б�ؼ�
	private ListView wordList;

	//���б�
	private SimpleAdapter adapter;

	//���ݿ���ʹ���
	private DatabaseHelper databaseHelper;
	
	//�ڵ����ϳ���ʱ��ʾ�ĶԻ���
	private AlertDialog wordActionMenuDialog;
	
	//��һ������ĵ��ʵ����
	private int lastClickedIndex;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_favorite);
		this.isFavorite=this.getIntent().getExtras().getBoolean(FavoriteActivity.TYPE_IS_FAVORITE);
		Log.d("oncreate", "isfavorite:"+this.isFavorite);
		if(!this.isFavorite){
			this.setTitle(R.string.title_activity_history);
		}
		
		Log.d("oncreate", "will initialize databasehelper via:"+this.getApplicationContext());
		this.databaseHelper=new DatabaseHelper(this.getApplicationContext());
		
		List<FavoriteWord> favs=null;
		if(this.isFavorite){
			favs=this.databaseHelper.getAllFavoriteWord();
		}else{
			favs=this.databaseHelper.getAllHistoryWord();
		}
		
		List<Map<String,Object>> data=new ArrayList<Map<String,Object>>();
		for(FavoriteWord fw:favs){
			Log.d("favoriteactivity oncreate", fw.toString());
			Map<String,Object> item=new HashMap<String,Object>();
			item.put("tword", fw.getWord());
			item.put("id", fw.getId());
			item.put("addtime", DatabaseHelper.dateFormat.format(fw.getAddTime()));
			data.add(item);
		}
		this.wordList=(ListView)this.findViewById(R.id.favorite_list_words);
		this.adapter=new SimpleAdapter(this,data,R.layout.word_item,new String[]{"tword","addtime"},new int[]{R.id.tview_word,R.id.tview_length});
		
		this.wordList.setClickable(true);
		this.wordList.setFocusable(true);
		this.wordList.setItemsCanFocus(true);
		this.wordList.setFocusableInTouchMode(true);
		this.wordList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		this.wordList.setAdapter(adapter);
		
		//������б����Ӧ�¼�
		//����б��¼�
		this.wordList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long rowid) {
				handleListItemClick(parent,view,false);
			}
		});
		
		//ʹ�䱾��Ҳ����Ӧ����
		this.wordList.setLongClickable(true);
		
		//�����б��¼�
		this.wordList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long rowid) {
				lastClickedIndex=position;
				handleListItemClick(parent,view,true);
				//���뷵��true��ʹ�����ڴ���itemclick
				return true;
			}
		});
		
		this.wordActionMenuDialog=new AlertDialog.Builder(this).setTitle(R.string.action_long_label).setItems(R.array.favorite_long_press_actions,
				new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch(which){
				case 0:
					//����ɾ����
					@SuppressWarnings("unchecked")
					Map<String,Object> it=(Map<String, Object>) adapter.getItem(lastClickedIndex);
					String xword=it.get("tword").toString();
					long id=(Long) it.get("id");
					int res=-1;
					if(isFavorite){
						res=databaseHelper.deleteFavoriteWord(id);
					}else{
						res=databaseHelper.deleteHistoryRecord(id);
					}
					Log.d("action clicked", "delete"+xword+", id:"+id+",res:"+res);
					Toast.makeText(getApplicationContext(), "ɾ��"+(isFavorite?"�ղ�":"��ʷ")+":"+(res>0?"�ɹ�":"ʧ��")+":"+xword, Toast.LENGTH_SHORT).show();
					if(res>0){
						//�ɹ�֮����ʾ�б�
						fillWordList();
					}
					break;
				}
			}
		}).create();
	}
	
	/**
	 * ��ʾ�б�
	 */
	private void fillWordList(){
		List<FavoriteWord> favs=null;
		if(this.isFavorite){
			favs=this.databaseHelper.getAllFavoriteWord();
		}else{
			favs=this.databaseHelper.getAllHistoryWord();
		}
		
		List<Map<String,Object>> data=new ArrayList<Map<String,Object>>();
		for(FavoriteWord fw:favs){
			Log.d("favoriteactivity oncreate", fw.toString());
			Map<String,Object> item=new HashMap<String,Object>();
			item.put("tword", fw.getWord());
			item.put("id", fw.getId());
			item.put("addtime", DatabaseHelper.dateFormat.format(fw.getAddTime()));
			data.add(item);
		}
		this.adapter=new SimpleAdapter(this,data,R.layout.word_item,new String[]{"tword","addtime"},new int[]{R.id.tview_word,R.id.tview_length});
		this.wordList.setAdapter(adapter);
	}
	/**
	 * �ѳ����Ͷ̰��Ĵ�����д��һ��
	 * @param parent
	 * @param view ��ǰ���������
	 * @param islong �Ƿ��ǳ���
	 */
	private void handleListItemClick(AdapterView<?> parent,View view,boolean islong){
		TextView wordview=null;
		if(view instanceof TextView){
			//��ģ������4.x�汾���¼��Ķ�������TextView֮һ
			wordview=(TextView)view;
			if(wordview.getId()!=id.tview_word){
				View v=parent.findViewById(id.tview_word);
				if(v instanceof TextView){
					wordview=(TextView) v;
				}
				Log.d("list long click", "get by parent:"+v.getId()+","+wordview.getText().toString());
			}else{
				Log.d("list long click", "is tview_word.id "+wordview.getText().toString()+", id:"+wordview.getId());
			}
		}else if(view instanceof LinearLayout){
			//��ʵ���ֻ�2.3�汾�ϣ��¼���������LinearLayout
			wordview=(TextView)view.findViewById(id.tview_word);
		}else{
			Toast.makeText(getApplicationContext(), "unknownview", Toast.LENGTH_SHORT).show();
		}
		
		String tword=wordview.getText().toString();
		WordPosition wp=MainActivity.readDict.getWords().get(tword);
		if(wp==null){
			Toast.makeText(this, "δԤ�ϵ������û���ҵ�����:"+tword, Toast.LENGTH_SHORT).show();
			return;
		}
		String strexp=MainActivity.readDict.getWordExplanation(wp.getStartPos(), wp.getLength());
		if(islong){
			//������ʾ�˵�
			this.wordActionMenuDialog.show();
		}else{
			//��ʾ����
			ShowUtil.showWord(this, false, tword, strexp);
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.favorite, menu);
		return true;
	}
	
	/**
	 * ���˵���Ӧ����
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		switch(item.getItemId()){
		case R.id.action_goback:
			this.finish();
			return true;
		}
		return false;
	}

}
