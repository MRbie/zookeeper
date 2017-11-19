package com.bie.lesson02;

import java.io.IOException;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;

/** 
* @author  Author:别先生 
* @date Date:2017年11月19日 下午12:40:01 
*
* 1:分布式应用系统服务器上下线动态感知程序开发
* 2:客户端和服务器写好了以后打包，上传到虚拟机，进行测试即可。
* 	zookeeper客户端线程的属性--守护线程
* 3:服务器会有动态上下线的情况:
* 	需求:客户端能实时洞察到服务器上下线的变化
* 		服务器端:
* 			a:服务端启动的时候就去zookeeper集群注册信息(必须是临时节点)。
* 		客户端:
* 			b:启动就去getChildren，获取到当前在线服务器列表。
* 				并且注册监听
* 			c:服务器节点上下线事件通知。
* 				process()方法，重新再去获取服务器列表，并且注册监听器，如此循环。
* 4:服务器运行:
* 	C:\Users\bhlgo\Desktop>java -jar server.jar master
*   C:\Users\bhlgo\Desktop>java -jar server.jar slaver1
*   C:\Users\bhlgo\Desktop>java -jar server.jar slaver2
*/
public class DistributedServer {

	//连接的服务器的地址
	private static final String connectString ="master:2181,slaver1:2181,slaver2:2181";
	private static final int sessionTimeout = 2000;//2秒
	private static final String parentNode = "/servers";
	
	private ZooKeeper zk = null;
	
	
	public void getConnection() throws IOException{
		//new Watcher()匿名内部类
		zk = new ZooKeeper(connectString, sessionTimeout, new Watcher() {
			
			@Override
			public void process(WatchedEvent event) {
				// TODO Auto-generated method stub
				//收到事件通知后的回调函数，应该是我们自己的事件处理逻辑。
				
				System.out.println("事件类型:"+ event.getType() + " 事件的路径:" + event.getPath());
				//监听只能使用一次，这样新打开一个监听，可以一直监听
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
	 * 向Zookeeper集群注册服务器信息
	 * @param hostName
	 * @throws KeeperException
	 * @throws InterruptedException
	 */
	public void registerServer(String hostName) throws KeeperException, InterruptedException{
		String create = zk.create(parentNode + "/server", hostName.getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
		System.out.println(hostName + " is online.. " + create);
		
	}
	
	
	/***
	 * 简单的业务功能
	 * @param hostName
	 * @throws InterruptedException
	 */
	public void handleBussiness(String hostName) throws InterruptedException{
		System.out.println(hostName + " is start working......");
		Thread.sleep(Long.MAX_VALUE);
	}
	
	public static void main(String[] args) throws IOException, KeeperException, InterruptedException {
		// 获取zk连接
		DistributedServer server = new DistributedServer();
		server.getConnection();
		
		// 利用zk连接注册服务器信息
		//run configurations配置服务器主机的名称参数即可。
		server.registerServer(args[0]);
		
		// 启动业务功能
		server.handleBussiness(args[0]);
	}
	
}
