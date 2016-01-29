package com.enonic.xp.repo.impl.elasticsearch.function;

public interface RangeFunctionArg<T>
{
    T getFrom();

    T getTo();

    boolean includeFrom();

    boolean includeTo();

    String getFieldName();

}
