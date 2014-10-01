package com.enonic.wem.portal.internal.base;

import com.enonic.wem.portal.internal.restlet.ResourceFactory;

public abstract class BaseResourceFactory<T extends BaseResource>
    extends ResourceFactory<T>
{
    public BaseResourceFactory( final Class<T> type )
    {
        super( type );
    }
}
