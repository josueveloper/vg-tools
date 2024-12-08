package com.vgtools.windowflow.request;

import com.vgtools.windowflow.CobolWindowFlow;

public abstract class ExecutableRequest {
    protected boolean isSyncronized;

    public ExecutableRequest(boolean isSyncronized) {
        this.isSyncronized = isSyncronized;
    }

    public boolean isSyncronized() {
        return isSyncronized;
    }

    public final Object execute(CobolWindowFlow repository) throws Exception {
        return format(output(repository), repository.getConsole());
    }

    abstract protected String output(CobolWindowFlow repository) throws Exception;

    abstract protected Object format(String output, char[][] console) throws Exception;
}
