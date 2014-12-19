package com.enonic.wem.portal.internal.controller;

import java.util.List;
import java.util.Map;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import com.enonic.wem.api.content.ContentConstants;
import com.enonic.wem.api.workspace.Workspace;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.RenderMode;
import com.enonic.xp.web.servlet.ServletRequestUrlHelper;

public final class PortalRequestImpl
    implements PortalRequest
{
    private String method;

    private final Multimap<String, String> params;

    private RenderMode mode;

    private Workspace workspace;

    public final static Workspace DEFAULT_WORKSPACE = ContentConstants.WORKSPACE_STAGE;

    public PortalRequestImpl()
    {
        this.mode = RenderMode.LIVE;
        this.workspace = DEFAULT_WORKSPACE;
        this.params = HashMultimap.create();
    }

    @Override
    public String getMethod()
    {
        return this.method;
    }

    @Override
    public Workspace getWorkspace()
    {
        return workspace;
    }

    @Override
    public Multimap<String, String> getParams()
    {
        return this.params;
    }

    @Override
    public RenderMode getMode()
    {
        return this.mode;
    }

    @Override
    public String getBaseUri()
    {
        return ServletRequestUrlHelper.createUri( "" );
    }

    public void setMethod( final String method )
    {
        this.method = method;
    }

    public void setMode( final RenderMode mode )
    {
        this.mode = mode;
    }

    public void setMode( final String mode )
    {
        setMode( RenderMode.from( mode, RenderMode.LIVE ) );
    }

    public void setWorkspace( final Workspace workspace )
    {
        this.workspace = workspace;
    }

    public void setWorkspace( final String workspace )
    {
        setWorkspace( Workspace.from( workspace ) );
    }

    public void addParam( final String name, final String value )
    {
        this.params.put( name, value );
    }

    public void addParams( final Multimap<String, String> params )
    {
        this.params.putAll( params );
    }

    public void addParams( final Map<String, List<String>> params )
    {
        for ( final Map.Entry<String, List<String>> entry : params.entrySet() )
        {
            this.params.putAll( entry.getKey(), entry.getValue() );
        }
    }
}
