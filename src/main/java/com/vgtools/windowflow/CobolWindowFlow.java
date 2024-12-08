package com.vgtools.windowflow;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.Instant;

import com.jcraft.jsch.Channel;
import com.vgtools.utils.ConsoleManager;
import com.vgtools.windowflow.gotoroot.DoNothingGoToRoot;
import com.vgtools.windowflow.gotoroot.GoToRootBehavior;
import com.vgtools.windowflow.request.ExecutableRequest;

public abstract class CobolWindowFlow {
    protected GoToRootBehavior goToRootBehavior;
    protected ExecutableRequest request;
    protected char[][] console;
    protected int[] consoleCursor;
    protected String logsFileName;
    protected InputStream consoleOutput;
    protected OutputStream consoleInput;

    public ExecutableRequest getRequest() {
        return request;
    }

    public char[][] getConsole() {
        return console;
    }

    public int[] getConsoleCursor() {
        return consoleCursor;
    }

    public String getLogsFileName() {
        return logsFileName;
    }

    public InputStream getConsoleOutput() {
        return consoleOutput;
    }

    public OutputStream getConsoleInput() {
        return consoleInput;
    }

    public boolean hasSyncronizedRequest() {
        return request.isSyncronized();
    }

    public CobolWindowFlow(String requestType, Channel channel) throws IOException {
        consoleOutput = channel.getInputStream();
        consoleInput = channel.getOutputStream();
        console = ConsoleManager.create();
        consoleCursor = new int[] { 0, 0 };
        logsFileName = createUniqueFileName(requestType);
        goToRootBehavior = new DoNothingGoToRoot();
        request = createRequest(requestType);
    }

    public void goToRoot() throws Exception {
        goToRootBehavior.goToRoot(this);
    }

    public Object executeRequest() throws Exception {
        return request.execute(this);
    }

    protected abstract ExecutableRequest createRequest(String request);

    // TODO validate that the filename is really unique
    private String createUniqueFileName(String requestType) {
        String[] classSegments = getClass().getName().split("\\.");
        return Instant.now().toEpochMilli() + "-" + Thread.currentThread().getName() + "-"
                + classSegments[classSegments.length - 1] + "-" + requestType;
    }
}
