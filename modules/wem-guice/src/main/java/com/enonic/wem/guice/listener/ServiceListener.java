package com.enonic.wem.guice.listener;

import java.util.Map;

public interface ServiceListener<T>
{
    public String getFilter();

    public Class<? extends T> getFilterClass();

    public void serviceAdded( T service, Map<String, Object> attributes );

    public void serviceModified( T service, Map<String, Object> attributes );

    public void serviceRemoved( T service );
}
