package com.enonic.xp.script.graaljs.impl.service;

public interface ServiceRegistry
{
    <T> ServiceRef<T> getService( Class<T> type );
}
