package com.yage.dict.entity;

import java.util.Date;

/**
 * �����ղص�һ������
 * @since 2014-1-4 23:16:52
 * @author voyage
 */
public class FavoriteWord {
	
	//��
	private long id;
	
	//����
	private String word;
	
	//����ʱ��
	private Date addTime;
	
	//��Ҫ�ȼ�
	private int importantClass;

	public String getWord() {
		return word;
	}

	public void setWord(String word) {
		this.word = word;
	}

	public Date getAddTime() {
		return addTime;
	}

	public void setAddTime(Date addTime) {
		this.addTime = addTime;
	}

	public int getImportantClass() {
		return importantClass;
	}

	public void setImportantClass(int importantClass) {
		this.importantClass = importantClass;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}
	
	public String toString(){
		return id+" "+this.word+" added at:"+this.addTime;
	}
}
