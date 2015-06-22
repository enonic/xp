package com.enonic.xp.portal.impl.script.service;

public interface ServiceRegistry
{
    <T> ServiceRef<T> getService( final Class<T> type );
}
