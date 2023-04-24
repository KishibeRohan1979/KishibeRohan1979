package com.tzp.myTestDemo.webCrawlerTest;

import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

public class StringTest {

	public static void main(String[] args) {
//		daoJiShi("2023-3-15 18:30:00");
//		String fileName = "calc-rule.json";
//		Map<String, Object> map = analyzeJSONDifferentKeys("E:/test/test/json/botJson/myJson/" + fileName, "E:/test/test/json/botJson/otherJson/" + fileName);
//		writerJsonFile(map, "E:/test/test/json/botJson/resultJson/" + fileName);
	}

	public static void daoJiShi(String daoJiShiDate) {
		Date date = new Date();
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String transformDate = simpleDateFormat.format(date);
		System.out.println(transformDate);
		DateFormat dataFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			//格式："2022-9-30 18:00:00"
			Date goHome = dataFormat.parse(daoJiShiDate);
			String goHomeDate = simpleDateFormat.format(goHome);
			System.out.println(goHomeDate);
			long nd = 1000 * 24 * 60 * 60;
	        long nh = 1000 * 60 * 60;
	        long nm = 1000 * 60;
	        long ns = 1000;
	        boolean notZero = true;
	        long diff;
	        long day;
	        long hour;
	        long min;
	        long sec;
	        do {
	        	// 获得两个时间的毫秒时间差异
	        	diff = goHome.getTime() - System.currentTimeMillis();
	        	// 计算差多少天
	        	day = diff / nd;
	        	// 计算差多少小时
	        	hour = diff % nd / nh;
				// 计算差多少分钟
	        	min = diff % nd % nh / nm;
				// 计算差多少秒//输出结果
	        	sec = diff % nd % nh % nm / ns;
	        	if ( diff<=0 ) notZero = false;
	        	for (int i=0; i<8; i++) {
	        		System.out.println();
	        	}
				System.out.println(day + "天" + hour + "小时" + min + "分钟" + sec + "秒");
	        	Thread.sleep(1000);
	        } while (notZero);
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	//对比json文件的不同，然后将多的写入少的。主要是对比key
	public static Map<String, Object> analyzeJSONDifferentKeys(String myJsonFilePath, String otherJsonFilePath) {
		String myJsonStr = getJsonString(myJsonFilePath);
		LinkedHashMap<String, Object> myJsonMap = JSON.parseObject(myJsonStr, LinkedHashMap.class);
		String otherJsonStr = getJsonString(otherJsonFilePath);
		LinkedHashMap<String, Object> otherJsonMap = JSON.parseObject(otherJsonStr, LinkedHashMap.class);
		Set<String> set = otherJsonMap.keySet();
		int flag = 0;
		HashMap<Integer, String> positionMap = new HashMap<>();
		for (String str : set) {
			positionMap.put(flag++, str);
			if ( !myJsonMap.containsKey(str) ) {
				System.out.println("发现原文件不存在项：{key：" + str + "; value：" + otherJsonMap.get(str) + "}");
				myJsonMap.put(str, otherJsonMap.get(str));
			}
		}
		return sortMapByAscendingOrder(myJsonMap, positionMap);
	}

	// 获取json文件的内容，并转出为String
	public static String getJsonString(String filePath) {
		String jsonStr = "";
		FileReader fileReader = null;
		Reader reader = null;
        try {
            fileReader = new FileReader(filePath);
            reader = new InputStreamReader(new FileInputStream(filePath), "utf-8");
            int ch = 0;
            StringBuffer sb = new StringBuffer();
            while ((ch = reader.read()) != -1) {
                sb.append((char) ch);
            }
            jsonStr = sb.toString();
            return jsonStr;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
        	try {
        		if (fileReader != null) {
					fileReader.close();
        		}
        		if (reader != null) {
            		reader.close();
            	}
        	} catch (IOException e) {
				e.printStackTrace();
				return null;
			}
		}
	}

	// 把Map排个序
	public static Map<String, Object> sortMapByAscendingOrder(Map<String, Object> map, Map<Integer, String> standardMap) {
		List<Integer> positionList = new ArrayList<>(standardMap.keySet());
		
		
		
//		Collections.sort(keyList, new Comparator<String>() {
//
//			@Override
//			public int compare(String o1, String o2) {
//				try {
//					return Long.valueOf(o1).compareTo(Long.valueOf(o2));
//				} catch (NumberFormatException e) {
//					return 0;
//				}
//				
//			}
//			
//		});
		
		Map<String, Object> newMap = new LinkedHashMap<>();
		for (Integer position : positionList) {
			newMap.put(standardMap.get(position), map.get(standardMap.get(position)));
		}
		
		return newMap;
	}

	// json对象，写入文件
	public static void writerJsonFile(Map<String, Object> map, String filePath) {
		JSONObject json = new JSONObject(map);
		String jsonStrFormat = jsonFormat(json);
		BufferedWriter bw = null;
		try {
			bw = new BufferedWriter (new OutputStreamWriter (new FileOutputStream (filePath,true),"UTF-8"));
			bw.write(jsonStrFormat);//转化成字符串再写
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("文件未找到");
		} finally {
			try {
				if (bw != null) {
					bw.flush();
					bw.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	// json文件的格式化
	public static String jsonFormat(Object jsonObject){
        String jsonFormatString = JSON.toJSONString(jsonObject,
        		SerializerFeature.PrettyFormat,
                SerializerFeature.WriteMapNullValue,
                SerializerFeature.WriteDateUseDateFormat);
        jsonFormatString = jsonFormatString.replaceAll("\t\t", "    ");
        jsonFormatString = jsonFormatString.replaceAll("\t", "  ");
		return jsonFormatString;
    }
	
}
