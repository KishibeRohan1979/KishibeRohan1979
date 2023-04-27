package com.tzp.myTestDemo.esTest;

import java.io.Serializable;

public class EsTest implements Serializable {

    private String id;

    private String name;

    private String sex;

    private String age;

    private String sign;

    public EsTest(String id, String name, String sex, String age, String sign) {
        this.id = id;
        this.name = name;
        this.sex = sex;
        this.age = age;
        setSign(sign);
    }

    public EsTest() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
//        this.sign = sign.replace("\n", "\\n").replace("\t","\\t");
    }

//    @Override
//    public String toString() {
//        return "{" +
//                "\"id\":\"" + id + "\"" +
//                ",\"name\":\"" + name + "\"" +
//                ",\"sex\":\"" + sex + "\"" +
//                ",\"age\":\"" + age + "\"" +
//                ",\"sign\":\"" + sign + "\"" +
//                "}";
//    }
}
