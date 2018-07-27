package edu.neu.service;

import com.amazonaws.services.sns.*;
import com.amazonaws.services.sns.model.MessageAttributeValue;
import com.amazonaws.services.sns.model.PublishRequest;
import com.amazonaws.services.sns.model.PublishResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class SNSMessage {

    public String message;
    public Map<String, MessageAttributeValue> messageAttributes;

    public SNSMessage(String message) {
        this.message = message;
        messageAttributes = new HashMap<>();
    }

    public void setMessage(String message) { this.message = message; }

    public String getMessage() { return message; }

    public void addAttribute(String attributeName, String attributeValue) {
        MessageAttributeValue messageAttributeValue =  new MessageAttributeValue()
                .withDataType("String")
                .withStringValue(attributeValue);
        messageAttributes.put(attributeName, messageAttributeValue);
    }

    public String publish(AmazonSNS snsClient, String topicArn) {
        PublishRequest request = new PublishRequest(topicArn, message)
                .withMessageAttributes(messageAttributes);
        PublishResult result = snsClient.publish(request);
        return result.getMessageId();
    }
}