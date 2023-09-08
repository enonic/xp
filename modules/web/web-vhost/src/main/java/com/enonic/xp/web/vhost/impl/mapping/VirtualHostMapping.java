package com.enonic.xp.web.vhost.impl.mapping;

import java.util.Map;
import java.util.Objects;

import com.enonic.xp.security.IdProviderKey;
import com.enonic.xp.security.IdProviderKeys;
import com.enonic.xp.web.vhost.VirtualHost;

public final class VirtualHostMapping
    implements VirtualHost
{
    private final String name;

    private final String host;

    private final String source;

    private final String target;

    private final int order;

    private final Map<String, String> context;

    private final VirtualHostIdProvidersMapping idProvidersMapping;

    public VirtualHostMapping( final String name, final String host, final String source, final String target,
                               final VirtualHostIdProvidersMapping idProvidersMapping, final int order )
    {
        this( name, host, source, target, idProvidersMapping, order, Map.of() );
    }

    public VirtualHostMapping( final String name, final String host, final String source, final String target,
                               final VirtualHostIdProvidersMapping idProvidersMapping, final int order, final Map<String, String> context )
    {
        Objects.requireNonNull( name, "name must be set" );
        Objects.requireNonNull( host, "host must be set" );
        Objects.requireNonNull( source, "source must be set" );
        Objects.requireNonNull( target, "target must be set" );
        Objects.requireNonNull( idProvidersMapping, "idProvidersMapping must be set" );

        this.name = name;
        this.host = host;
        this.source = source;
        this.target = target;
        this.idProvidersMapping = idProvidersMapping;
        this.order = order;
        this.context = Objects.requireNonNullElse( context, Map.of() );
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
        return idProvidersMapping.getDefaultIdProvider();
    }

    @Override
    public IdProviderKeys getIdProviderKeys()
    {
        return idProvidersMapping.getIdProviderKeys();
    }

    @Override
    public int getOrder()
    {
        return order;
    }

    @Override
    public Map<String, String> getContext()
    {
        return context;
    }
}
