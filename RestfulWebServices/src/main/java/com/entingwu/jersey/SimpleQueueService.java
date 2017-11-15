package com.entingwu.jersey;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.CreateQueueRequest;
import com.amazonaws.services.sqs.model.DeleteMessageRequest;
import com.amazonaws.services.sqs.model.DeleteQueueRequest;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.amazonaws.services.sqs.model.SendMessageBatchRequest;
import com.amazonaws.services.sqs.model.SendMessageBatchRequestEntry;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;

public class SimpleQueueService {
    
    private static final String QUEUE_NAME = "MyQueue";
    private static String queueUrl;
    private static AmazonSQS sqs;
    public static AtomicInteger id = new AtomicInteger();
    
    /*public static void main(String[] args) {
        sendBatchMsgToSQS();
    }*/
    
    public static boolean sendBatchMsgToSQS(List<String> msgs) {
        if (sqs == null) {
            sqs = getAmazonSQS();
        }
        try {
            if (queueUrl == null) {
                queueUrl = createSqsQueue();
                displayQueue();
            }
            //List<String> msgs = new ArrayList<>();
            //msgs.add("text1.");
            //msgs.add("text2.");
            //msgs.add("text3.");
            sendSqsMessage(msgs, queueUrl);
            
            //List<Message> messages = receiveSqsMessage();
            //deleteMessage(messages, queueUrl);
            //deleteQueue(queueUrl);
        } catch (AmazonServiceException ase) {
            System.out.println("Caught an AmazonServiceException, which means your request made it " +
                    "to Amazon SQS, but was rejected with an error response for some reason.");
            System.out.println("Error Message:    " + ase.getMessage());
            System.out.println("HTTP Status Code: " + ase.getStatusCode());
            System.out.println("AWS Error Code:   " + ase.getErrorCode());
            System.out.println("Error Type:       " + ase.getErrorType());
            System.out.println("Request ID:       " + ase.getRequestId());
        } catch (AmazonClientException ace) {
            System.out.println("Caught an AmazonClientException, which means the client encountered " +
                    "a serious internal problem while trying to communicate with SQS, such as not " +
                    "being able to access the network.");
            System.out.println("Error Message: " + ace.getMessage());
        }
        return true;
    }
    
    /*
     * The ProfileCredentialsProvider will return your [default]
     * credential profile by reading from the credentials file located at
     * (~/.aws/credentials).*/
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
    
    private static String createSqsQueue() {
        CreateQueueRequest createQueueRequest = new CreateQueueRequest(QUEUE_NAME);
        return sqs.createQueue(createQueueRequest).getQueueUrl();
    }
    
    private static void sendSqsMessage(List<String> msgs, String queueUrl) {
        System.out.println("message: " + msgs.toString());
        List<SendMessageBatchRequestEntry> entries = new ArrayList<>();
        for (String msg : msgs) {
            SendMessageBatchRequestEntry entry = 
                    new SendMessageBatchRequestEntry(String.valueOf(id.get()), msg);
            entries.add(entry);
            id.getAndIncrement();
        }
        SendMessageBatchRequest smbr = new SendMessageBatchRequest(queueUrl, entries);
        sqs.sendMessageBatch(smbr);
        //sqs.sendMessage(new SendMessageRequest(queueUrl, "This is my message text."));
    }
    
    private static List<Message> receiveSqsMessage() {
        System.out.println("Receiving messages from MyQueue.\n");
        ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest(queueUrl);
        List<Message> messages = sqs.receiveMessage(receiveMessageRequest).getMessages();
        for (Message message : messages) {
            System.out.println("  Message");
            System.out.println("    MessageId:     " + message.getMessageId());
            System.out.println("    ReceiptHandle: " + message.getReceiptHandle());
            System.out.println("    MD5OfBody:     " + message.getMD5OfBody());
            System.out.println("    Body:          " + message.getBody());
            for (Entry<String, String> entry : message.getAttributes().entrySet()) {
                System.out.println("  Attribute");
                System.out.println("    Name:  " + entry.getKey());
                System.out.println("    Value: " + entry.getValue());
            }
        }
        System.out.println();
        return messages;
    }
    
    private static void deleteMessage(List<Message> messages, String queueUrl) {
        System.out.println("Deleting a message.\n");
        String messageReceiptHandle = messages.get(0).getReceiptHandle();
        sqs.deleteMessage(new DeleteMessageRequest(queueUrl, messageReceiptHandle));
    }

    private static void deleteQueue(String queueUrl) {
        System.out.println("Deleting the test queue.\n");
        sqs.deleteQueue(new DeleteQueueRequest(queueUrl));
    }
    
    private static void displayQueue() {
        for (String queueUrl : sqs.listQueues().getQueueUrls()) {
            System.out.println("  QueueUrl: " + queueUrl);
        }
        System.out.println();
    }
}
