package org.uengine.msa;

import org.uengine.uml.model.ClassDefinition;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by uengine on 2018. 2. 10..
 */
public class GenerationResult {

    ClassDefinition classDefinition;
        public ClassDefinition getClassDefinition() {
            return classDefinition;
        }
        public void setClassDefinition(ClassDefinition classDefinition) {
            this.classDefinition = classDefinition;
        }


    List<Action> actions = new ArrayList<>();
        public List<Action> getActions() {
            return actions;
        }
        public void setActions(List<Action> actions) {
            this.actions = actions;
        }

}
