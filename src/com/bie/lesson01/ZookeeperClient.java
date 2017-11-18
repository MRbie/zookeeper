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
* @author  Author:别先生 
* @date Date:2017年11月18日 下午4:13:59 
*
*  1:Zookeeper的客户端操作就是增删改查，监听，对数据的响应。
*  	 org.apache.zookeeper.Zookeeper是客户端入口主类，负责建立与server的会话
*  2:数据的增删改查操作。
*  
*/
public class ZookeeperClient {

	
	private static final String connectString ="master:2181,slaver1:2181,slaver2:2181";//连接的服务器的地址
	private static final int sessionTimeout = 2000;//2秒
	
	ZooKeeper zooKeeperClient = null;
	
	@Before
	public void init() throws IOException{
		//new Watcher()匿名内部类
		zooKeeperClient = new ZooKeeper(connectString, sessionTimeout, new Watcher() {
			
			@Override
			public void process(WatchedEvent event) {
				// TODO Auto-generated method stub
				//收到事件通知后的回调函数，应该是我们自己的事件处理逻辑。
				
				System.out.println("事件类型:"+ event.getType() + " 事件的路径:" + event.getPath());
				//监听只能使用一次，这样新打开一个监听，可以一直监听
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
	
	//zookeeper的创建,
	//参数已:要创建的节点的路径,参数二:节点的数据,参数三:节点的权限,参数四:节点的类型。
	@Test
	public void zookeeperCreate() throws KeeperException, InterruptedException{
		//上传的数据可以是任何类型，但都要转成byte[]
		String createNode = zooKeeperClient.create("/eclipse", "hello".getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
		//System.out.println("返回创建的节点:" + createNode);
	}
	
	
	@Test
	public void zookeeperExists() throws KeeperException, InterruptedException{
		//Stat即封装起来的一些数据
		Stat exists = zooKeeperClient.exists("/eclipse", false);
		if(exists == null){
			System.out.println("子节点不存在......");
		}else{
			System.out.println("子节点存在......");
		}
	}
	
	//获取子节点
	@Test
	public void getChildren() throws KeeperException, InterruptedException{
		List<String> children = zooKeeperClient.getChildren("/", true);
		//循环遍历操作
		for(String child : children){
			System.out.println("此目录下的节点:"+ child);
		}
		//休眠，测试监听事件，是否好使。监听器只能使用一次。
		Thread.sleep(Long.MAX_VALUE);
	}
	
	
	
}
