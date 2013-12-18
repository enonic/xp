package com.enonic.wem.portal.script.lib;


import java.util.Collections;
import java.util.List;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

import static com.google.common.base.Strings.nullToEmpty;

public class PortalImageUrlBuilder
    extends PortalUrlBuilder<PortalImageUrlBuilder>
{
    private static final String IMAGE_RESOURCE_TYPE = "image";

    private String background;

    private String quality;

    private List<String> filters;

    private PortalImageUrlBuilder( final String baseUrl )
    {
        super( baseUrl );
        background = "";
        quality = "";
        this.filters = Lists.newArrayList();
    }

    public PortalImageUrlBuilder filter( final String filter )
    {
        this.filters.add( filter );
        return this;
    }

    public PortalImageUrlBuilder filters( final List<String> filter )
    {
        this.filters.addAll( filter );
        return this;
    }

    public PortalImageUrlBuilder filters( final String... filters )
    {
        Collections.addAll( this.filters, filters );
        return this;
    }

    public PortalImageUrlBuilder background( final String background )
    {
        this.background = nullToEmpty( background );
        return this;
    }

    public PortalImageUrlBuilder quality( final String quality )
    {
        this.quality = nullToEmpty( quality );
        return this;
    }

    protected void preBuildUrl()
    {
        resourceType( IMAGE_RESOURCE_TYPE );

        if ( !filters.isEmpty() )
        {
            final String filtersStr = Joiner.on( ";" ).skipNulls().join( filters );
            param( "filter", filtersStr );
        }
        if ( !background.isEmpty() )
        {
            param( "background", background );
        }
        if ( !quality.isEmpty() )
        {
            param( "quality", quality );
        }
    }

    public static PortalImageUrlBuilder createImageUrl( final String baseUrl )
    {
        return new PortalImageUrlBuilder( baseUrl );
    }
}
