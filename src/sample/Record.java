package sample;

import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;

public class Record extends RecursiveTreeObject<Record> {

    private String input;
    private String result;

    public Record(String input, String result) {
        this.input = input;
        this.result = result;
    }

    public String getInput() {
        return input;
    }

    public void setInput(String input) {
        this.input = input;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}
