package com.enonic.wem.xslt.internal.function;

import com.google.common.base.Joiner;

import com.enonic.wem.portal.view.ViewFunctions;

public final class MockViewFunctions
    implements ViewFunctions
{
    @Override
    public String url( final String... params )
    {
        return "url(" + Joiner.on( ',' ).join( params ) + ")";
    }

    @Override
    public String assetUrl( final String... params )
    {
        return "assetUrl(" + Joiner.on( ',' ).join( params ) + ")";
    }

    @Override
    public String pageUrl( final String... params )
    {
        return "pageUrl(" + Joiner.on( ',' ).join( params ) + ")";
    }

    @Override
    public String imageUrl( final String... params )
    {
        return "imageUrl(" + Joiner.on( ',' ).join( params ) + ")";
    }

    @Override
    public String attachmentUrl( final String... params )
    {
        return "attachmentUrl(" + Joiner.on( ',' ).join( params ) + ")";
    }

    @Override
    public String serviceUrl( final String... params )
    {
        return "serviceUrl(" + Joiner.on( ',' ).join( params ) + ")";
    }

    @Override
    public String componentUrl( final String... params )
    {
        return "componentUrl(" + Joiner.on( ',' ).join( params ) + ")";
    }
}
