package com.bie.lesson02;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

/** 
* @author  Author:������ 
* @date Date:2017��11��19�� ����1:09:45 
* 
* 1:�ֲ�ʽӦ��ϵͳ�����������߶�̬��֪���򿪷�
* 2:�ͻ��˺ͷ�����д�����Ժ������ϴ�������������в��Լ��ɡ�
*   zookeeper�ͻ����̵߳�����--�ػ��߳�
*3:���������ж�̬�����ߵ����:
* 	����:�ͻ�����ʵʱ���쵽�����������ߵı仯
* 		��������:
* 			a:�����������ʱ���ȥzookeeper��Ⱥע����Ϣ(��������ʱ�ڵ�)��
* 		�ͻ���:
* 			b:������ȥgetChildren����ȡ����ǰ���߷������б�
* 				����ע�������
* 			c:�������ڵ��������¼�֪ͨ��
* 				process()������������ȥ��ȡ�������б�����ע������������ѭ����
* 4:�ͻ��˵�����ʱ������޷�����������һ�£����ɵ�����
* 	C:\Users\bhlgo\Desktop>java -jar client.jar
*/
public class DistributedClient {

	//���ӵķ������ĵ�ַ
	private static final String connectString ="master:2181,slaver1:2181,slaver2:2181";
	private static final int sessionTimeout = 2000;//2��
	private static final String parentNode = "/servers";
	// ע��:��volatile��������ڣ�
	//�����ڶ��߳��ж�д��һ��������֡�
	private volatile List<String> serverList;
	private ZooKeeper zk = null;
	
	
	public void getConnection() throws IOException{
		//new Watcher()�����ڲ���
		zk = new ZooKeeper(connectString, sessionTimeout, new Watcher() {
			
			@Override
			public void process(WatchedEvent event) {
				// TODO Auto-generated method stub
				//�յ��¼�֪ͨ��Ļص�������Ӧ���������Լ����¼������߼���
				System.out.println("�¼�����:"+ event.getType() + " �¼���·��:" + event.getPath());
				try {
					//���¸��·������б�����ע���˼���
					getServerList();
				} catch (KeeperException e) {
					e.printStackTrace();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	
	public void getServerList() throws KeeperException, InterruptedException{
		//��ȡ�������ӽڵ���Ϣ�����ҶԸ��ڵ���м���
		List<String> children = zk.getChildren(parentNode, true);
		
		//�ȴ���һ���ֲ���list�����������Ϣ
		List<String> servers = new ArrayList<String>();
		for(String child : children){
			//childֻ���ӽڵ�Ľڵ�����
			byte[] data = zk.getData(parentNode + "/" + child, false, null);
			servers.add(new String(data));
		}
		//��servers��ֵ����Ա����serverList,���ṩ��ҵ���߳�ʹ��
		serverList = servers;
		
		//��ӡ�������б�
		System.out.println(serverList);
		
	}
	
	
	/***
	 * �򵥵�ҵ����
	 * @param hostName
	 * @throws InterruptedException
	 */
	public void handleBussiness() throws InterruptedException{
		System.out.println("client is start working......");
		Thread.sleep(Long.MAX_VALUE);
	}
	
	public static void main(String[] args) throws KeeperException, InterruptedException, IOException {
		//��ȡZookeeper������
		DistributedClient client = new DistributedClient();
		client.getConnection();
		
		//��ȡservers���ӽڵ���Ϣ�����Ҽ��������л�ȡ��������Ϣ�б�
		client.getServerList();
		
		//ҵ���߳�����
		client.handleBussiness();
	}
}
