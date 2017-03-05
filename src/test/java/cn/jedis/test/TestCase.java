package cn.jedis.test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Transaction;

/**
 * @author  大牛哥 
 * @E-mail: 201309512@qq.com 
 * @date 创建时间：2017年3月4日 下午5:14:54
 * @version 1.0
 * @parameter
 * @since
 * @return  */

public class TestCase {
	private static Jedis jedis;
	
	public static void main(String[] args) {
		jedis = new Jedis("localhost");
		System.out.println("正在连接");
		//输入密码
		jedis.auth("123456");
		System.out.println("连接结果:"+jedis.ping());
		teststring();
		testlist();
		testset();
		testsortedSet();
		testhash();
		other();
		getkeys();
		
		
		connectionPool();
	}
	private static void other() {
		// TODO Auto-generated method stub
//		 对key的模糊查询
		Set<String> set = jedis.keys("*");
		Set<String> keys = jedis.keys("user.userid.*");  
		
//		删除key
		jedis.del("test");  
		System.out.println("删除test成功");
		
//		是否存在
		Boolean isExists = jedis.exists("user.userid.14101");  
		
//		失效时间 expire：时间为5s
		jedis.setex("user.userid.14101", 5, "James");  
		
//		存活时间(ttl)：time to live
		Long seconds = jedis.ttl("user.userid.14101");  
		
//		 去掉key的expire设置：不再有失效时间
		jedis.persist("user.userid.14101");  
		
//		自增的整型
//		 int类型采用string类型的方式存储
		jedis.set("amount", 100 + "");  
		
//		递增或递减：incr()/decr()
		jedis.incr("amount");  
		
//		增加或减少：incrBy()/decrBy()
		jedis.incrBy("amount", 20);  
		
//		 数据清空
//		清空当前db：
		jedis.flushDB();
		
//		清空所有db：
		jedis.flushAll();
		
//		事务支持：
//		@ 获取事务：
		Transaction tx = jedis.multi();  
//		@ 批量操作：tx采用和jedis一致的API接口
		for(int i = 0;i < 10;i ++) {  
		     tx.set("key" + i, "value" + i);   
		     System.out.println("--------key" + i);  
		     try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}    
		}  
//		 @ 执行事务：针对每一个操作，返回其执行的结果，成功即为Ok
		List<Object> results = tx.exec(); 
		
		
	}
	/**
	 * 集合的使用hash
	 */
	private static void testhash() {
		// TODO Auto-generated method stub
//		存放数据：使用HashMap
		Map<String, String>  capital = new HashMap<String, String>();  
		capital.put("shannxi", "xi'an");  
		jedis.hmset("captal", capital);
		
//		获取数据
		List<String> cities = jedis.hmget("capital", "shannxi", "shanghai");
		
	}
	/**
	 *	使用sorted set：有序集合在集合的基础上，增加了一个用于排序的参数。
	 *	根据“第二个参数”进行排序。
	 */
	private static void testsortedSet() {
		// TODO Auto-generated method stub
//		向集合添加数据
		jedis.zadd("test", 20 ,"att");
		
//		元素相同时更新为当前的权重
		jedis.zadd("test", 24,"att");
//		zset的范围：找到从0到-1的所有元素。set的数据类型为java.util.LinkedHashSet
		Set<String> set = jedis.zrange("test", 0, -1);
		
	}
	/**
	 * 无序集合
	 */
	private static void testset() {
		// TODO Auto-generated method stub
		jedis.sadd("testset", "sssdf12");
//		 遍历集合
		Set<String> set  = jedis.smembers("testset");
//		移除元素：remove
		jedis.srem("testset", "sssdf1"); 
//		 返回长度：
		Long size = jedis.scard("testset");  
//		 是否包含
		Boolean isMember = jedis.sismember("testset", "sssdf1");  
//		集合的操作：包括集合的交运算(sinter)、差集(sdiff)、并集(sunion)
		jedis.sadd("food", "bread", "milk");   
		Set<String> fruitFood = jedis.sunion("testset", "food");  
		
	}
	private static void connectionPool() {
		// TODO Auto-generated method stub
		Properties properties = new Properties();
		try {
			properties.load(TestCase.class.getClassLoader().getResourceAsStream("jedis.properties"));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		JedisPoolConfig config = new JedisPoolConfig();
		String host = properties.getProperty("redis.host");
		int port = Integer.parseInt(properties.getProperty("redis.port"));
		int timeout = Integer.parseInt(properties.getProperty("redis.timeout"));
		String password = properties.getProperty("redis.password");
		System.out.println(host);
		config.setMaxActive(Integer.parseInt(properties.getProperty("redis.pool.maxActive")));
		config.setTestOnBorrow(Boolean.parseBoolean(properties.getProperty("redis.pool.testOnBorrow"))); 
		JedisPool pool = new JedisPool(config,host,port,timeout,password);
		jedis = pool.getResource();
		System.out.println("连接池获取的对象进行操作：------------------------------start");
		teststring();
		testlist();
		getkeys();
		System.out.println("连接池获取的对象进行操作：------------------------------end");
		System.out.println("归还对象");
		pool.returnResource(jedis);
		
	}
	/**
	 * 测试获取所有的key
	 */
	private static void getkeys() {
		// TODO Auto-generated method stub
		Set<String> keys =  jedis.keys("*");
		
		for(String str : keys)
		{
			System.out.println("key :" +str);
		}
		
	}
	/**
	 * 测试列表
	 */
	private static void testlist() {
		//删除对象
		jedis.del("list");
		//从左边添加进去（入栈）
		for(int i = 0; i< 5 ;i++){
			jedis.lpush("list", "第"+i+"条");
		}
		//从右边进入，添加队列
		for(int i = 5; i< 10 ;i++){
			jedis.rpush("list", "第"+i+"条");
		}
		for(int i = 0;i<=jedis.llen("list");i++){
			String is = jedis.lpop("list");
			String is1 = jedis.rpop("list");
			System.out.println("li:"+is);
			System.out.println("ri:"+is1);
		}
	}
	/**
	 * 测试string 操作
	 */
	private static void teststring() {
		// TODO Auto-generated method stub
		jedis.set("set", "我是string");
		String str = jedis.get("set");
		System.out.println(str);
		
	}
}
