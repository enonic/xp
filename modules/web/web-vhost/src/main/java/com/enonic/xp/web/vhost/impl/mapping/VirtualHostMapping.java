package com.enonic.xp.web.vhost.impl.mapping;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import org.jspecify.annotations.Nullable;

import com.enonic.xp.security.IdProviderKey;
import com.enonic.xp.security.IdProviderKeys;
import com.enonic.xp.security.PrincipalKeys;
import com.enonic.xp.web.dispatch.DispatchConstants;
import com.enonic.xp.web.vhost.VirtualHost;

import static java.util.Objects.requireNonNull;
import static java.util.Objects.requireNonNullElse;

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

    private final String connector;

    private final PrincipalKeys allowedPrincipals;

    public VirtualHostMapping( final String name, final String host, final String source, final String target,
                               final VirtualHostIdProvidersMapping idProvidersMapping, final int order )
    {
        this( name, host, source, target, idProvidersMapping, order, Map.of() );
    }

    public VirtualHostMapping( final String name, final String host, final String source, final String target,
                               final VirtualHostIdProvidersMapping idProvidersMapping, final int order, final Map<String, String> context )
    {
        this( name, host, source, target, idProvidersMapping, order, context, DispatchConstants.XP_CONNECTOR );
    }

    public VirtualHostMapping( final String name, final String host, final String source, final String target,
                               final VirtualHostIdProvidersMapping idProvidersMapping, final int order, final Map<String, String> context,
                               final String connector )
    {
        this( name, host, source, target, idProvidersMapping, order, context, connector, PrincipalKeys.empty() );
    }

    public VirtualHostMapping( final String name, final String host, final String source, final String target,
                               final VirtualHostIdProvidersMapping idProvidersMapping, final int order,
                               @Nullable final Map<String, String> context, @Nullable final String connector,
                               @Nullable final PrincipalKeys allowedPrincipals )
    {
        requireNonNull( name, "name must be set" );
        requireNonNull( host, "host must be set" );
        requireNonNull( source, "source must be set" );
        requireNonNull( target, "target must be set" );
        requireNonNull( idProvidersMapping, "idProvidersMapping must be set" );

        this.name = name;
        this.host = host;
        this.source = source;
        this.target = target;
        this.idProvidersMapping = idProvidersMapping;
        this.order = order;
        this.context = Collections.unmodifiableMap( requireNonNullElse( context, Map.of() ) );
        this.connector = requireNonNullElse( connector, DispatchConstants.XP_CONNECTOR );
        this.allowedPrincipals = requireNonNullElse( allowedPrincipals, PrincipalKeys.empty() );
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
    public @Nullable Set<String> getIdProviderFlows( final IdProviderKey idProviderKey )
    {
        return idProvidersMapping.getFlows( idProviderKey );
    }

    @Override
    public String getConnector()
    {
        return connector;
    }

    @Override
    public PrincipalKeys getAllowedPrincipals()
    {
        return allowedPrincipals;
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
