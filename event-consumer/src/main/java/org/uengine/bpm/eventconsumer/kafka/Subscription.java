package org.uengine.bpm.eventconsumer.kafka;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * Created by uengine on 2019. 2. 21..
 */
@Entity
public class Subscription {
    @Id
    @GeneratedValue
    Long id;
    String topic;
    String defId;
    String corrKey;


    public String getCorrKey() {
        return corrKey;
    }

    public void setCorrKey(String corrKey) {
        this.corrKey = corrKey;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getDefId() {
        return defId;
    }

    public void setDefId(String defId) {
        this.defId = defId;
    }
}
