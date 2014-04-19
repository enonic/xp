package com.enonic.wem.api.resource;

import java.net.URL;

public interface ResourceResolver
{
    public abstract URL resolve( ResourceReference ref );
}
