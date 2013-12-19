package com.enonic.wem.portal.script.lib;


import java.util.Collections;
import java.util.List;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Strings.nullToEmpty;

public final class PortalImageUrlBuilder
    extends BasePortalUrlBuilder<PortalImageUrlBuilder>
{
    private static final String IMAGE_RESOURCE_TYPE = "image";

    private String background;

    private Integer quality;

    private List<String> filters;

    private PortalImageUrlBuilder( final String baseUrl )
    {
        super( baseUrl );
        background = "";
        quality = null;
        this.filters = Lists.newArrayList();
    }

    public PortalImageUrlBuilder filter( final List<String> filter )
    {
        this.filters.addAll( filter );
        return this;
    }

    public PortalImageUrlBuilder filter( final String... filters )
    {
        Collections.addAll( this.filters, filters );
        return this;
    }

    public PortalImageUrlBuilder background( final String backgroundColor )
    {
        this.background = nullToEmpty( backgroundColor );
        return this;
    }

    public PortalImageUrlBuilder quality( final int quality )
    {
        checkArgument( quality > 0, "Image Quality must be between 1 and 100. Value: %s", quality );
        checkArgument( quality <= 100, "Image Quality must be between 1 and 100. Value: %s", quality );
        this.quality = quality;
        return this;
    }

    protected void beforeBuildUrl()
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
        if ( quality != null )
        {
            param( "quality", quality );
        }
    }

    public static PortalImageUrlBuilder createImageUrl( final String baseUrl )
    {
        return new PortalImageUrlBuilder( baseUrl );
    }
}
