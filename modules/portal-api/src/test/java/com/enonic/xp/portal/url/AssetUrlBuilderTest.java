package com.enonic.xp.portal.url;

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

        assertEquals( "/portal/stage/some/path/_/asset/mymodule/css/my.css", builder.toString() );
    }

    @Test
    public void createUrlOverride()
    {
        final AssetUrlBuilder builder = this.builders.assetUrl().
            baseUri( "/other" ).
            module( "othermodule" ).
            workspace( "prod" ).
            contentPath( "a/b" ).
            path( "css/my.css" );

        assertEquals( "/other/prod/a/b/_/asset/othermodule/css/my.css", builder.toString() );
    }
}
