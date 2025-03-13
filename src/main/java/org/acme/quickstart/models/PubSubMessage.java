package org.acme.quickstart.models;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PubSubMessage {
    public String data;
    public String message_id;
    public String publish_time;
}