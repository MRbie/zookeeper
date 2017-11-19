package com.bie.lesson02;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

/** 
* @author  Author:别先生 
* @date Date:2017年11月19日 下午1:09:45 
* 
* 1:分布式应用系统服务器上下线动态感知程序开发
* 2:客户端和服务器写好了以后打包，上传到虚拟机，进行测试即可。
*   zookeeper客户端线程的属性--守护线程
*3:服务器会有动态上下线的情况:
* 	需求:客户端能实时洞察到服务器上下线的变化
* 		服务器端:
* 			a:服务端启动的时候就去zookeeper集群注册信息(必须是临时节点)。
* 		客户端:
* 			b:启动就去getChildren，获取到当前在线服务器列表。
* 				并且注册监听。
* 			c:服务器节点上下线事件通知。
* 				process()方法，重新再去获取服务器列表，并且注册监听器，如此循环。
* 4:客户端导出的时候，如果无法导出，运行一下，即可导出。
* 	C:\Users\bhlgo\Desktop>java -jar client.jar
*/
public class DistributedClient {

	//连接的服务器的地址
	private static final String connectString ="master:2181,slaver1:2181,slaver2:2181";
	private static final int sessionTimeout = 2000;//2秒
	private static final String parentNode = "/servers";
	// 注意:加volatile的意义何在？
	//避免在多线程中读写不一致现象出现。
	private volatile List<String> serverList;
	private ZooKeeper zk = null;
	
	
	public void getConnection() throws IOException{
		//new Watcher()匿名内部类
		zk = new ZooKeeper(connectString, sessionTimeout, new Watcher() {
			
			@Override
			public void process(WatchedEvent event) {
				// TODO Auto-generated method stub
				//收到事件通知后的回调函数，应该是我们自己的事件处理逻辑。
				System.out.println("事件类型:"+ event.getType() + " 事件的路径:" + event.getPath());
				try {
					//重新更新服务器列表，并且注册了监听
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
		//获取服务器子节点信息，并且对父节点进行监听
		List<String> children = zk.getChildren(parentNode, true);
		
		//先创建一个局部的list来存服务器信息
		List<String> servers = new ArrayList<String>();
		for(String child : children){
			//child只是子节点的节点名称
			byte[] data = zk.getData(parentNode + "/" + child, false, null);
			servers.add(new String(data));
		}
		//把servers赋值给成员变量serverList,已提供给业务线程使用
		serverList = servers;
		
		//打印服务器列表
		System.out.println(serverList);
		
	}
	
	
	/***
	 * 简单的业务功能
	 * @param hostName
	 * @throws InterruptedException
	 */
	public void handleBussiness() throws InterruptedException{
		System.out.println("client is start working......");
		Thread.sleep(Long.MAX_VALUE);
	}
	
	public static void main(String[] args) throws KeeperException, InterruptedException, IOException {
		//获取Zookeeper的连接
		DistributedClient client = new DistributedClient();
		client.getConnection();
		
		//获取servers的子节点信息，并且监听，从中获取服务器信息列表
		client.getServerList();
		
		//业务线程启动
		client.handleBussiness();
	}
}
