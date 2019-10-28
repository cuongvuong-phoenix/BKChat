package app.socket;

import app.models.User;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;

public class UpdateUserListWorker extends Task<Void> {
    private final ObservableList<User> userObservableList;
    private final Client client;

    public UpdateUserListWorker(Client client, ObservableList<User> userObservableList) {
        this.client = client;
        this.userObservableList = userObservableList;
    }
    @Override
    protected Void call() throws Exception {
        Platform.runLater(() -> {
            client.setUserObservableList(userObservableList);});
        return null;
    }
}
