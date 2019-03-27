package com.enonic.xp.jaxrs;

public interface JaxRsServiceFactory
{
    JaxRsService newService( String group, String path, String connector );
}
