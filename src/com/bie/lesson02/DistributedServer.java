package com.bie.lesson02;

import java.io.IOException;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;

/** 
* @author  Author:������ 
* @date Date:2017��11��19�� ����12:40:01 
*
* 1:�ֲ�ʽӦ��ϵͳ�����������߶�̬��֪���򿪷�
* 2:�ͻ��˺ͷ�����д�����Ժ������ϴ�������������в��Լ��ɡ�
* 	zookeeper�ͻ����̵߳�����--�ػ��߳�
* 3:���������ж�̬�����ߵ����:
* 	����:�ͻ�����ʵʱ���쵽�����������ߵı仯
* 		��������:
* 			a:�����������ʱ���ȥzookeeper��Ⱥע����Ϣ(��������ʱ�ڵ�)��
* 		�ͻ���:
* 			b:������ȥgetChildren����ȡ����ǰ���߷������б�
* 				����ע�����
* 			c:�������ڵ��������¼�֪ͨ��
* 				process()������������ȥ��ȡ�������б�����ע������������ѭ����
* 4:����������:
* 	C:\Users\bhlgo\Desktop>java -jar server.jar master
*   C:\Users\bhlgo\Desktop>java -jar server.jar slaver1
*   C:\Users\bhlgo\Desktop>java -jar server.jar slaver2
*/
public class DistributedServer {

	//���ӵķ������ĵ�ַ
	private static final String connectString ="master:2181,slaver1:2181,slaver2:2181";
	private static final int sessionTimeout = 2000;//2��
	private static final String parentNode = "/servers";
	
	private ZooKeeper zk = null;
	
	
	public void getConnection() throws IOException{
		//new Watcher()�����ڲ���
		zk = new ZooKeeper(connectString, sessionTimeout, new Watcher() {
			
			@Override
			public void process(WatchedEvent event) {
				// TODO Auto-generated method stub
				//�յ��¼�֪ͨ��Ļص�������Ӧ���������Լ����¼������߼���
				
				System.out.println("�¼�����:"+ event.getType() + " �¼���·��:" + event.getPath());
				//����ֻ��ʹ��һ�Σ������´�һ������������һֱ����
				try {
					zk.getChildren("/", true);
				} catch (KeeperException e) {
					e.printStackTrace();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	
	/**
	 * ��Zookeeper��Ⱥע���������Ϣ
	 * @param hostName
	 * @throws KeeperException
	 * @throws InterruptedException
	 */
	public void registerServer(String hostName) throws KeeperException, InterruptedException{
		String create = zk.create(parentNode + "/server", hostName.getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
		System.out.println(hostName + " is online.. " + create);
		
	}
	
	
	/***
	 * �򵥵�ҵ����
	 * @param hostName
	 * @throws InterruptedException
	 */
	public void handleBussiness(String hostName) throws InterruptedException{
		System.out.println(hostName + " is start working......");
		Thread.sleep(Long.MAX_VALUE);
	}
	
	public static void main(String[] args) throws IOException, KeeperException, InterruptedException {
		// ��ȡzk����
		DistributedServer server = new DistributedServer();
		server.getConnection();
		
		// ����zk����ע���������Ϣ
		//run configurations���÷��������������Ʋ������ɡ�
		server.registerServer(args[0]);
		
		// ����ҵ����
		server.handleBussiness(args[0]);
	}
	
}
