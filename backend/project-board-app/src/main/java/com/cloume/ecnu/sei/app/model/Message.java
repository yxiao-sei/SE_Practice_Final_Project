package com.cloume.ecnu.sei.app.model;

import com.alibaba.fastjson.JSONObject;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

//发送消息的实体类
@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Message {
    /**
     * 主键id
     */
    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid2")
    private String id;

    /**
     * 发送消息的内容
     */
    private String content;

    /**
     * 发送人
     */
    private String sender;

    /**
     * 接收者
     */
    private String receiver;

    /**
     * 发送时间
     */
    private Long createdTime = System.currentTimeMillis();

    /**
     * 推送方法（系统推送\企业微信推送）
     */
    private String pushMethod;

    /**
     * 标题
     */
    private String title;

    /**
     * 来源系统(如果为本系统则 )
     */
    private String source = "sca";

    /**
     * 跳转url
     */
    private String directUrl;

    //适配messaage-gateway消息格式
    public String toString(){
        JSONObject jsonTask = new JSONObject();
        jsonTask.put("fromSystem", "sca");
        jsonTask.put("content", this.content);
        jsonTask.put("sender", this.sender);
        jsonTask.put("receiver", this.receiver);
        jsonTask.put("title", this.title);
        jsonTask.put("pushTime", this.createdTime);
        jsonTask.put("pushMethods", this.pushMethod);
        jsonTask.put("messageFromId", this.id);
        jsonTask.put("directUrl", this.directUrl);
        return jsonTask.toJSONString();
    }
}
