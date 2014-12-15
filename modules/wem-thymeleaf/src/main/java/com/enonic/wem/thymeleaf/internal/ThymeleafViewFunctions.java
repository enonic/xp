package com.enonic.wem.thymeleaf.internal;

import java.util.List;

import com.enonic.wem.portal.view.ViewFunctions;

final class ThymeleafViewFunctions
{
    private final ViewFunctions viewFunctions;

    public ThymeleafViewFunctions( final ViewFunctions viewFunctions )
    {
        this.viewFunctions = viewFunctions;
    }

    private String[] toArray( final List<String> list )
    {
        return list.toArray( new String[list.size()] );
    }

    @SuppressWarnings("unused")
    public String url( final List<String> list )
    {
        return this.viewFunctions.url( toArray( list ) );
    }

    @SuppressWarnings("unused")
    public String assetUrl( final List<String> list )
    {
        return this.viewFunctions.assetUrl( toArray( list ) );
    }

    @SuppressWarnings("unused")
    public String pageUrl( final List<String> list )
    {
        return this.viewFunctions.pageUrl( toArray( list ) );
    }

    @SuppressWarnings("unused")
    public String attachmentUrl( final List<String> list )
    {
        return this.viewFunctions.attachmentUrl( toArray( list ) );
    }

    @SuppressWarnings("unused")
    public String componentUrl( final List<String> list )
    {
        return this.viewFunctions.componentUrl( toArray( list ) );
    }

    @SuppressWarnings("unused")
    public String imageUrl( final List<String> list )
    {
        return this.viewFunctions.imageUrl( toArray( list ) );
    }

    @SuppressWarnings("unused")
    public String serviceUrl( final List<String> list )
    {
        return this.viewFunctions.serviceUrl( toArray( list ) );
    }
}
