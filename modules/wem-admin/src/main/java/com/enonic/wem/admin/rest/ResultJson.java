package com.enonic.wem.admin.rest;

import com.enonic.wem.admin.rest.resource.ErrorJson;

public abstract class ResultJson<T> {

    private final T result;

    private final ErrorJson error;

    protected ResultJson(T result, ErrorJson error) {
        this.result = result;
        this.error = error;
    }

    public T getResult() {
        return result;
    }

    public ErrorJson getError() {
        return error;
    }
}
