package com.essec.microservices.serializer;

import java.io.IOException;
import java.util.Objects;

import javax.ws.rs.core.Link;

import org.springframework.boot.jackson.JsonComponent;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

@JsonComponent
public class LinkSerializer extends JsonSerializer<Link>{

    @Override
    public void serialize(Link link, JsonGenerator jg, SerializerProvider sp) 
            throws IOException, JsonProcessingException {
        jg.writeStartObject();
        jg.writeStringField("uri", Objects.toString(link.getUri(), ""));
        jg.writeStringField("title", Objects.toString(link.getTitle(), ""));
        jg.writeStringField("rel", Objects.toString(link.getRel(), ""));
        jg.writeStringField("type", Objects.toString(link.getType(), ""));
        jg.writeEndObject();
    }
    
}
