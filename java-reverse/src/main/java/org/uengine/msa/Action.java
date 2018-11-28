package org.uengine.msa;

/**
 * Created by uengine on 2018. 2. 10..
 */
public class Action {

    String action;

    public String getAction() {
            return action;
        }
        public void setAction(String action) {
            this.action = action;
        }

    String sourceCode;
        public String getSourceCode() {
            return sourceCode;
        }
        public void setSourceCode(String sourceCode) {
            this.sourceCode = sourceCode;
        }

    String fileName;
        public String getFileName() {
            return fileName;
        }
        public void setFileName(String fileName) {
            this.fileName = fileName;
        }

    private String previousFileName;

        public void setPreviousFileName(String previousFileName) {
            this.previousFileName = previousFileName;
        }

        public String getPreviousFileName() {
            return previousFileName;
        }
}
