package group.chaoliu.lightchaser.core.test;

import java.util.concurrent.Callable;

public class Task implements Callable<String> {

    private String filename;

    public Task(String filename) {
        this.filename = filename;
    }

    @Override
    public String call() throws Exception {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("完成 " + filename);
        return "返回值" + filename;
    }
}
