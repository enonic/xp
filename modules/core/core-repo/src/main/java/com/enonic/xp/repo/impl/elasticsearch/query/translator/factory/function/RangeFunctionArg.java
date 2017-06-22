package com.enonic.xp.repo.impl.elasticsearch.query.translator.factory.function;

public interface RangeFunctionArg<T>
{
    T getFrom();

    T getTo();

    boolean includeFrom();

    boolean includeTo();

    String getFieldName();

}
