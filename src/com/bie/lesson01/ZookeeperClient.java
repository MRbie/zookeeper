package com.bie.lesson01;

import java.io.IOException;
import java.util.List;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.junit.Before;
import org.junit.Test;

/** 
* @author  Author:������ 
* @date Date:2017��11��18�� ����4:13:59 
*
*  1:Zookeeper�Ŀͻ��˲���������ɾ�Ĳ飬�����������ݵ���Ӧ��
*  	 org.apache.zookeeper.Zookeeper�ǿͻ���������࣬��������server�ĻỰ
*  2:���ݵ���ɾ�Ĳ������
*  
*/
public class ZookeeperClient {

	
	private static final String connectString ="master:2181,slaver1:2181,slaver2:2181";//���ӵķ������ĵ�ַ
	private static final int sessionTimeout = 2000;//2��
	
	ZooKeeper zooKeeperClient = null;
	
	@Before
	public void init() throws IOException{
		//new Watcher()�����ڲ���
		zooKeeperClient = new ZooKeeper(connectString, sessionTimeout, new Watcher() {
			
			@Override
			public void process(WatchedEvent event) {
				// TODO Auto-generated method stub
				//�յ��¼�֪ͨ��Ļص�������Ӧ���������Լ����¼������߼���
				
				System.out.println("�¼�����:"+ event.getType() + " �¼���·��:" + event.getPath());
				//����ֻ��ʹ��һ�Σ������´�һ������������һֱ����
				try {
					zooKeeperClient.getChildren("/", true);
				} catch (KeeperException e) {
					e.printStackTrace();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	//zookeeper�Ĵ���,
	//������:Ҫ�����Ľڵ��·��,������:�ڵ������,������:�ڵ��Ȩ��,������:�ڵ�����͡�
	@Test
	public void zookeeperCreate() throws KeeperException, InterruptedException{
		//�ϴ������ݿ������κ����ͣ�����Ҫת��byte[]
		String createNode = zooKeeperClient.create("/eclipse04", "hello".getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
		//String nodeCreated = zooKeeperClient.create("/eclipse03", "hellozk".getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
		
		//System.out.println("���ش����Ľڵ�:" + createNode);
	}
	
	
	@Test
	public void zookeeperExists() throws KeeperException, InterruptedException{
		//Stat����װ������һЩ����
		Stat stat = zooKeeperClient.exists("/eclipse04", false);
		System.out.println(stat==null?"not exist":"exist");
		
	}
	
	//��ȡ�ӽڵ�
	@Test
	public void getChildren() throws KeeperException, InterruptedException{
		List<String> children = zooKeeperClient.getChildren("/", true);
		//ѭ����������
		for(String child : children){
			System.out.println("��Ŀ¼�µĽڵ�:"+ child);
		}
		//���ߣ����Լ����¼����Ƿ��ʹ��������ֻ��ʹ��һ�Ρ�
		Thread.sleep(Long.MAX_VALUE);
	}
	
	
	//��ȡ��zNode������
	@Test
	public void getData() throws KeeperException, InterruptedException{
		byte[] data = zooKeeperClient.getData("/eclipse04", false, null);
		System.out.println(new String(data));
	}
	
	//ɾ��zNode
	@Test
	public void deleteZnode() throws InterruptedException, KeeperException{
		//����2:ָ��Ҫɾ���İ汾��-1��ʾɾ�����а汾��
		zooKeeperClient.delete("/eclipse04", -1);
	}
	
	
	//ΪzNode���ֵ
	@Test
	public void setDate() throws KeeperException, InterruptedException{
		zooKeeperClient.setData("/eclipse04", "I am so happy".getBytes(), -1);
		byte[] data = zooKeeperClient.getData("/eclipse04", false, null);
		System.out.println(new String(data));
	}
	
}
