package com.systers.conference.callback;

public interface ObjectResponseCallback<T> {
    void OnSuccess(T response);

    void OnFailure(Throwable error);
}
