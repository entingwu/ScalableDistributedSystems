package com.entingwu.jersey;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.CreateQueueRequest;
import com.amazonaws.services.sqs.model.SendMessageBatchRequest;
import com.amazonaws.services.sqs.model.SendMessageBatchRequestEntry;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class SimpleQueuePublisher {
    
    private static final String QUEUE_NAME = "MyQueue";
    private static String queueUrl;
    public static AmazonSQS sqs;
    public static AtomicInteger id = new AtomicInteger();
    
    public static void prepareSQS() {
        if (sqs == null) {
            sqs = getAmazonSQS();
        }
        try {
            if (queueUrl == null) {
                queueUrl = createSqsQueue();
            }
        } catch (AmazonClientException ex) {
            System.out.println("Error Message: " + ex.getMessage());
        }
    }
    
    public static void sendBatchMsgToSQS(List<String> msgs) {
        try {
            List<SendMessageBatchRequestEntry> entries = new ArrayList<>();
            for (String msg : msgs) {
                SendMessageBatchRequestEntry entry = 
                        new SendMessageBatchRequestEntry(String.valueOf(id.get()), msg);
                entries.add(entry);
                id.getAndIncrement();
            }
            SendMessageBatchRequest smbr = new SendMessageBatchRequest(queueUrl, entries);
            sqs.sendMessageBatch(smbr);
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
    
    private static String createSqsQueue() {
        CreateQueueRequest createQueueRequest = new CreateQueueRequest(QUEUE_NAME);
        return sqs.createQueue(createQueueRequest).getQueueUrl();
    }
}
