package com.enonic.wem.thymeleaf.internal;

import com.google.common.collect.Multimap;

import com.enonic.wem.portal.view.ViewFunctions;

public final class MockViewFunctions
    implements ViewFunctions
{
    private String toString( final String name, final Multimap<String, String> params )
    {
        return name + params.toString();
    }

    @Override
    public String url( final Multimap<String, String> params )
    {
        return toString( "url", params );
    }

    @Override
    public String assetUrl( final Multimap<String, String> params )
    {
        return toString( "assetUrl", params );
    }

    @Override
    public String pageUrl( final Multimap<String, String> params )
    {
        return toString( "pageUrl", params );
    }

    @Override
    public String attachmentUrl( final Multimap<String, String> params )
    {
        return toString( "attachmentUrl", params );
    }

    @Override
    public String componentUrl( final Multimap<String, String> params )
    {
        return toString( "componentUrl", params );
    }

    @Override
    public String imageUrl( final Multimap<String, String> params )
    {
        return toString( "imageUrl", params );
    }

    @Override
    public String serviceUrl( final Multimap<String, String> params )
    {
        return toString( "serviceUrl", params );
    }
}
