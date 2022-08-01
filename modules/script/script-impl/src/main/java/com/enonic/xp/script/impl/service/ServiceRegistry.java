package com.enonic.xp.script.impl.service;

public interface ServiceRegistry
{
    <T> ServiceRef<T> getService( Class<T> type );

    <T> ServiceRef<T> getService( Class<T> type, final String filter );
}
