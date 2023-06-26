package com.tzp.myTestDemo.webCrawlerTest;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class DuelMasterJson {
	public static void main(String[] args) {
		String filePath = "F:\\steam\\Steam\\steamapps\\common\\Yu-Gi-Oh!  Master Duel\\YgoMaster\\Data\\Players\\Local\\Player.json";

		// 读取JSON文件内容
		String jsonContent = readJSONFile(filePath);

		if (jsonContent != null) {
			// 解析JSON内容
			JSONObject jsonObject = JSON.parseObject(jsonContent);

			// 修改Name值
			jsonObject.put("Name", "\u592a\u5bb0\u76ae");

			// 修改Cards对象下的属性值
			JSONObject cards = jsonObject.getJSONObject("Cards");
			if (cards != null) {
				int i = 0;
				for (String key : cards.keySet()) {
					JSONObject card = cards.getJSONObject(key);
					card.put("p_n", 1);
					card.put("p_p1n", 1);
					card.put("p_p2n", 1);
					card.put("tn", 1);
					card.put("p1n", 2);
					card.put("p2n", 2);
					card.put("n", 2);
					card.put("r", 2);
					i++;
				}
				System.out.println("修改成功了" + i + "张卡");
			}
			// 将修改后的JSON对象转换为字符串
			String modifiedJsonContent = jsonObject.toJSONString();

			// 将修改后的JSON内容写回文件
			writeJSONFile(filePath, modifiedJsonContent);
		}
	}

	private static String readJSONFile(String filePath) {
		try {
			return new String(Files.readAllBytes(Paths.get(filePath)));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	private static void writeJSONFile(String filePath, String jsonContent) {
		try (FileWriter fileWriter = new FileWriter(filePath)) {
			// 格式化输出JSON内容
			JSON.writeJSONString(fileWriter, JSON.parse(jsonContent),
					SerializerFeature.PrettyFormat,
					SerializerFeature.WriteMapNullValue,
					SerializerFeature.WriteDateUseDateFormat);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
