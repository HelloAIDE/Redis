package cn.jedis.test;

import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
/**
 * @author  大牛哥 
 * @E-mail: 201309512@qq.com 
 * @date 创建时间：2017年3月4日 下午6:28:14
 * @version 1.0
 * @parameter
 * @since
 * @return  */
public class SpringAddRedis {
	ApplicationContext context = null;
	@Before
	public void init(){
		 context = new ClassPathXmlApplicationContext("applicationContext.xml"); 
	}
	@Test
	public void test1(){
		JedisPool pool = (JedisPool) context.getBean("jedisPool");
		Jedis jedis = pool.getResource();
		System.out.println("连接结果："+jedis.ping());
		jedis.set("spring", "ok");
		System.out.println("get："+jedis.get("spring"));
	}
	                           
}
