package com.entingwu.logservices;

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.DeleteMessageBatchRequestEntry;
import com.amazonaws.services.sqs.model.DeleteQueueRequest;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SimpleQueueSubscriber {
    
    private static String queueUrl;
    public static AmazonSQS sqs;
    
    public static String prepareSQS() {
        if (sqs == null) {
            sqs = getAmazonSQS();
        }
        if (queueUrl == null) {
            queueUrl = sqs.listQueues().getQueueUrls().get(0);
            System.out.println("  QueueUrl: " + queueUrl);
        }
        return queueUrl;
    }
    
    public static List<Message> receiveSqsMessage() {
        System.out.println("Receiving messages from MyQueue.\n");
        ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest(queueUrl);
        receiveMessageRequest.setMaxNumberOfMessages(10);
        receiveMessageRequest.setWaitTimeSeconds(20);
        return sqs.receiveMessage(receiveMessageRequest).getMessages();
    }
    
    public static void displayMessage(List<Message> messages) {
        System.out.println("num: " + messages.size());
        for (Message message : messages) {
            System.out.println("  Message");
            System.out.println("    MessageId:     " + message.getMessageId());
            System.out.println("    ReceiptHandle: " + message.getReceiptHandle());
            System.out.println("    MD5OfBody:     " + message.getMD5OfBody());
            System.out.println("    Body:          " + message.getBody());
            for (Map.Entry<String, String> entry : message.getAttributes().entrySet()) {
                System.out.println("  Attribute");
                System.out.println("    Name:  " + entry.getKey());
                System.out.println("    Value: " + entry.getValue());
            }
        }
        System.out.println();
    }
    
    public static void deleteSqsMessage(String queueUrl, List<Message> messages) {
        System.out.println("Deleting batch message.\n");
        List<DeleteMessageBatchRequestEntry> msgs = new ArrayList<>();
        for (int i = 0; i < messages.size(); i++) {
            Message message = messages.get(0);
            DeleteMessageBatchRequestEntry dmbre = 
                    new DeleteMessageBatchRequestEntry(
                        message.getMessageId(), 
                        message.getReceiptHandle());
            msgs.add(dmbre);
        }
        sqs.deleteMessageBatch(queueUrl, msgs);
        //sqs.deleteMessage(new DeleteMessageRequest(queueUrl, messageReceiptHandle));
    }
    
    private static AmazonSQS getAmazonSQS() {
        ProfileCredentialsProvider credentialsProvider = new ProfileCredentialsProvider();
        try{
            credentialsProvider.getCredentials();
        } catch (Exception e) {
            throw new AmazonClientException(
                    "Cannot load the credentials from the credential profiles file. " +
                    "Please make sure that your credentials file is at the correct " +
                    "location (~/.aws/credentials), and is in valid format.",
                    e);
        }
        sqs = AmazonSQSClientBuilder.standard()
                .withCredentials(credentialsProvider)
                .withRegion(Regions.US_WEST_2)
                .build();
        
        System.out.println("Getting Started with Amazon SQS");
        return sqs;
    }
    
    private static void deleteQueue(String queueUrl) {
        System.out.println("Deleting the test queue.\n");
        sqs.deleteQueue(new DeleteQueueRequest(queueUrl));
    }
}
