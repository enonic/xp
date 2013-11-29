package com.enonic.wem.api.support;


public interface EditBuilder<T>
{
    public boolean isChanges();

    public Changes getChanges();

    public T build();

}
