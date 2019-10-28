package app.socket;

import javafx.concurrent.Task;

import java.io.FileOutputStream;

public class FileTask extends Task<Void> {
    private final String path;
    private final String fileName;
    private  final byte[] file;

    public FileTask(byte[] file, String path, String fileName) {
        this.file = file;
        this.path = path;
        this.fileName = fileName;
    }

    @Override
    protected Void call() throws Exception {
        try {
            FileOutputStream os = new FileOutputStream(path + "/" + fileName);
            os.write(file);
            os.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
