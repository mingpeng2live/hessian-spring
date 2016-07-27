package com.jsonfilter.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jsonfilter.ClassUtil;
import com.jsonfilter.Jacksons;

public class FIlterTest {

	public static void main(String[] args) {
		
		List<User> list = new ArrayList<User>();
		User u1 = new User();
		u1.setAge(1);
		u1.setCard("1");
		u1.setName("1");
		u1.setSex("男");
		list.add(u1);
		User u2 = new User();
		u2.setAge(2);
		u2.setCard("2");
		u2.setName("2");
		u2.setSex("女");
		list.add(u2);
		
		
		List<Map<String, Object>> val = new ArrayList<Map<String,Object>>();		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("a", "a");
		map.put("b", "b");
		map.put("u", u1);
		val.add(map);
		map = new HashMap<String, Object>();
		map.put("a", "a1");
		map.put("b", "b1");
		map.put("u", u2); 
		val.add(map);
		
		ClassUtil cu1 = ClassUtil.getAnnotationInstance();
		
		
			
//		String s = Jacksons.me()
//    			.addMixInAnnotations(Object.class, MyIFilter.class)
//    			.filter("myFilter", "viewUserName", "b")
//    			.readAsString(val);
		
		String s = Jacksons.me()
    			.addMixInAnnotations(Object.class, cu1.getClassObj())
    			.filter(cu1.getFilterName(), "name", "b")
    			.readAsString(val);
		
//		String s = Jacksons.me().addMixInAnnotations(Object.class, cu1.getClassObj())
//				.setFilterProvider(cu1.getFilterName(), FiledSerializeExceptFilter.serializeAllExcept("age", "b"))
////				.readAsString(list);
//				.readAsString(val);
		
//		String s = Jacksons.me().addMixInAnnotations(Object.class, cu1.getClassObj())
//				.setFilterProvider(cu1.getFilterName(), FiledSerializeExceptFilter.serializeAllExcept("name", "b"))
////				.readAsString(list);
//				.readAsString(val);
		System.out.println(s);
	}
	
	
	
	
}

class User {
	
	private String name;
	private Integer age;
	private String sex;
	private String card;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getAge() {
		return age;
	}

	public void setAge(Integer age) {
		this.age = age;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public String getCard() {
		return card;
	}

	public void setCard(String card) {
		this.card = card;
	}

}