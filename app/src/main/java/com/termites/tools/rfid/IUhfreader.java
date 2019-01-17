package com.termites.tools.rfid;

import java.util.List;

public interface IUhfreader {

	void open(String serial, int port);//打开
	
	void close();//关闭
	
	List<String> inventoryTag();//盘存标签
	
	boolean selectEPC(byte[] epc);//选择标签
	
	boolean clearSelect();//取消选择
	
	byte[] readData(int memBank, int start, int length, byte[] password);//读数据区数据

	boolean writeData(int memBank, int start, byte[] password, byte[] wData) ;//写数据
	
	boolean writeEPC(byte[] newEPC, byte[] password);//修改EPC
	
	boolean writeAccess(byte[] newAccess, byte[] oldAccess);//修改访问密码
	
	boolean writeKillPsd(byte[] newKill, byte[] access) ;//修改销毁密码
	
	boolean lockTag(int lockType, int lockMem, byte[] access);
	
	boolean setOutPower(short value);

}
