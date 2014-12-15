package com.enonic.wem.portal.url;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.module.ModuleKey;
import com.enonic.wem.api.workspace.Workspace;
import com.enonic.wem.portal.RenderMode;

import static com.google.common.base.Strings.emptyToNull;

public abstract class PortalUrlBuilder2<T extends PortalUrlBuilder2>
{
    private static final RenderMode DEFAULT_MODE = RenderMode.LIVE;

    private static final String DEFAULT_WORKSPACE = "stage";

    private String baseUri;

    private String renderMode;

    private String workspace;

    private String contentPath;

    private String module;

    private final Multimap<String, String> params;

    public PortalUrlBuilder2()
    {
        this.params = HashMultimap.create();
        renderMode( DEFAULT_MODE );
        workspace( DEFAULT_WORKSPACE );
    }

    public final T baseUri( final String value )
    {
        this.baseUri = emptyToNull( value );
        return typecastThis();
    }

    public final T renderMode( final String value )
    {
        this.renderMode = emptyToNull( value );
        return null;
    }

    public final T renderMode( final RenderMode value )
    {
        return renderMode( value != null ? value.toString() : null );
    }

    public final T workspace( final String value )
    {
        this.workspace = emptyToNull( value );
        return typecastThis();
    }

    public final T workspace( final Workspace value )
    {
        return workspace( value != null ? value.toString() : null );
    }

    public final T module( final String value )
    {
        this.module = emptyToNull( value );
        return typecastThis();
    }

    public final T module( final ModuleKey value )
    {
        return module( value != null ? value.toString() : null );
    }

    public final T param( final String name, final Object value )
    {
        this.params.put( name, value != null ? value.toString() : null );
        return typecastThis();
    }

    public final T contentPath( final String value )
    {
        this.module = emptyToNull( value );
        return typecastThis();
    }

    public final T contentPath( final ContentPath value )
    {
        return contentPath( value != null ? value.toString() : null );
    }

    @SuppressWarnings("unchecked")
    private T typecastThis()
    {
        return (T) this;
    }

    private String buildUrl()
    {
        return null;
    }

    public final String toString()
    {
        return buildUrl();
    }
}
