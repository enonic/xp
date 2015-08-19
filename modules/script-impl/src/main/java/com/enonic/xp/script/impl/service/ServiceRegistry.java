package com.enonic.xp.script.impl.service;

public interface ServiceRegistry
{
    <T> ServiceRef<T> getService( final Class<T> type );
}
