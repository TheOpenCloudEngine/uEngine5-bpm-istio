package org.uengine.msa;

import javax.persistence.*;

/**
 * Created by uengine on 2018. 1. 11..
 */
@Entity
public class SemanticValue{

    @Id
    @GeneratedValue
    Long id;

    @ManyToOne
    @JoinColumn(name="entity")
    SemanticEntity semanticEntity;

    String synonym;
    String value;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public SemanticEntity getSemanticEntity() {
        return semanticEntity;
    }

    public void setSemanticEntity(SemanticEntity semanticEntity) {
        this.semanticEntity = semanticEntity;
    }

    public String getSynonym() {
        return synonym;
    }

    public void setSynonym(String synonym) {
        this.synonym = synonym;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

}
