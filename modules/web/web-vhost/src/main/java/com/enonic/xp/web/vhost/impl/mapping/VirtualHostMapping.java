package com.enonic.xp.web.vhost.impl.mapping;

import com.enonic.xp.security.IdProviderKey;
import com.enonic.xp.security.IdProviderKeys;
import com.enonic.xp.web.vhost.VirtualHost;
import com.enonic.xp.web.vhost.impl.VirtualHostInternalHelper;

import static com.google.common.base.Strings.isNullOrEmpty;

public final class VirtualHostMapping
    implements VirtualHost
{
    private static final String DEFAULT_HOST = "localhost";

    private static final String DEFAULT_PATH = "/";

    private final String name;

    private String host;

    private String source;

    private String target;

    private VirtualHostIdProvidersMapping virtualHostIdProvidersMapping;

    public VirtualHostMapping( final String name )
    {
        this.name = name;
        this.host = DEFAULT_HOST;
        this.source = DEFAULT_PATH;
        this.target = DEFAULT_PATH;
    }

    @Override
    public String getName()
    {
        return this.name;
    }

    @Override
    public String getHost()
    {
        return this.host;
    }

    @Override
    public String getSource()
    {
        return this.source;
    }

    @Override
    public String getTarget()
    {
        return this.target;
    }

    @Override
    public IdProviderKey getDefaultIdProviderKey()
    {
        return virtualHostIdProvidersMapping == null ? null : virtualHostIdProvidersMapping.getDefaultIdProvider();
    }

    @Override
    public IdProviderKeys getIdProviderKeys()
    {
        return virtualHostIdProvidersMapping == null ? IdProviderKeys.empty() : virtualHostIdProvidersMapping.getIdProviderKeys();
    }

    public void setHost( final String value )
    {
        this.host = isNullOrEmpty( value ) ? DEFAULT_HOST : value;
    }

    public void setSource( final String value )
    {
        this.source = VirtualHostInternalHelper.normalizePath( value );
    }

    public void setTarget( final String value )
    {
        this.target = VirtualHostInternalHelper.normalizePath( value );
    }

    public void setVirtualHostIdProvidersMapping( final VirtualHostIdProvidersMapping virtualHostIdProvidersMapping )
    {
        this.virtualHostIdProvidersMapping = virtualHostIdProvidersMapping;
    }
}
