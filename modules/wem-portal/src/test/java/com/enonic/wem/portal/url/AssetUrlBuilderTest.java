package com.enonic.wem.portal.url;

import org.junit.Test;

import static org.junit.Assert.*;

public class AssetUrlBuilderTest
    extends AbstractUrlBuilderTest
{
    @Test
    public void createUrl()
    {
        final AssetUrlBuilder builder = this.builders.assetUrl().
            path( "css/my.css" );

        assertEquals( "/root/portal/live/stage/some/path/_/public/mymodule/css/my.css", builder.toString() );
    }

    @Test
    public void createUrlOverride()
    {
        final AssetUrlBuilder builder = this.builders.assetUrl().
            baseUri( "/other" ).
            module( "othermodule" ).
            renderMode( "edit" ).
            workspace( "prod" ).
            contentPath( "a/b" ).
            path( "css/my.css" );

        assertEquals( "/other/portal/edit/prod/a/b/_/public/othermodule/css/my.css", builder.toString() );
    }
}
