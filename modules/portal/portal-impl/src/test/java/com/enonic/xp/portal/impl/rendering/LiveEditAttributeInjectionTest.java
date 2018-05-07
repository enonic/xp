package com.enonic.xp.portal.impl.rendering;

import org.junit.Test;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.region.TextComponentType;

import static org.junit.Assert.*;

public class LiveEditAttributeInjectionTest
{
    @Test
    public void simpleInjection()
        throws Exception
    {
        final String html = readResource( "part1Source.html" );

        final LiveEditAttributeInjection liveEditAttributeInjection = new LiveEditAttributeInjection();
        final PortalResponse.Builder responseBuilder = PortalResponse.create().body( html );

        final PortalResponse portalResponse =
            liveEditAttributeInjection.injectLiveEditAttribute( responseBuilder.build(), TextComponentType.INSTANCE );

        final String outputHtml = portalResponse.getBody().toString();
        final String expectedResult = readResource( "part1Rendered.html" );

        assertEquals( expectedResult, outputHtml );
    }

    @Test
    public void injectWithAttributes()
        throws Exception
    {
        final String html = readResource( "part2Source.html" );

        final LiveEditAttributeInjection liveEditAttributeInjection = new LiveEditAttributeInjection();
        final PortalResponse.Builder responseBuilder = PortalResponse.create().body( html );

        final PortalResponse portalResponse =
            liveEditAttributeInjection.injectLiveEditAttribute( responseBuilder.build(), TextComponentType.INSTANCE );

        final String outputHtml = portalResponse.getBody().toString();
        final String expectedResult = readResource( "part2Rendered.html" );

        assertEquals( expectedResult, outputHtml );
    }

    @Test
    public void injectEmptySingleTag()
        throws Exception
    {
        final String html = readResource( "part3Source.html" );

        final LiveEditAttributeInjection liveEditAttributeInjection = new LiveEditAttributeInjection();
        final PortalResponse.Builder responseBuilder = PortalResponse.create().body( html );

        final PortalResponse portalResponse =
            liveEditAttributeInjection.injectLiveEditAttribute( responseBuilder.build(), TextComponentType.INSTANCE );

        final String outputHtml = portalResponse.getBody().toString();
        final String expectedResult = readResource( "part3Rendered.html" );

        assertEquals( expectedResult, outputHtml );
    }

    @Test
    public void injectWithXmlDeclaration()
        throws Exception
    {
        final String html = readResource( "part4Source.html" );

        final LiveEditAttributeInjection liveEditAttributeInjection = new LiveEditAttributeInjection();
        final PortalResponse.Builder responseBuilder = PortalResponse.create().body( html );

        final PortalResponse portalResponse =
            liveEditAttributeInjection.injectLiveEditAttribute( responseBuilder.build(), TextComponentType.INSTANCE );

        final String outputHtml = portalResponse.getBody().toString();
        final String expectedResult = readResource( "part4Rendered.html" );

        assertEquals( expectedResult, outputHtml );
    }

    @Test
    public void injectWithDocType()
        throws Exception
    {
        final String html = readResource( "part5Source.html" );

        final LiveEditAttributeInjection liveEditAttributeInjection = new LiveEditAttributeInjection();
        final PortalResponse.Builder responseBuilder = PortalResponse.create().body( html );

        final PortalResponse portalResponse =
            liveEditAttributeInjection.injectLiveEditAttribute( responseBuilder.build(), TextComponentType.INSTANCE );

        final String outputHtml = portalResponse.getBody().toString();
        final String expectedResult = readResource( "part5Rendered.html" );

        assertEquals( expectedResult, outputHtml );
    }

    @Test
    public void nonSingleRootTagHtml()
        throws Exception
    {
        final String html = readResource( "part6Source.html" );

        final LiveEditAttributeInjection liveEditAttributeInjection = new LiveEditAttributeInjection();
        final PortalResponse.Builder responseBuilder = PortalResponse.create().body( html );

        final PortalResponse portalResponse =
            liveEditAttributeInjection.injectLiveEditAttribute( responseBuilder.build(), TextComponentType.INSTANCE );

        final String outputHtml = portalResponse.getBody().toString();

        assertEquals( html, outputHtml );
    }

    @Test
    public void injectWithLeadingWhitespace()
        throws Exception
    {
        final String html = readResource( "part7Source.html" );

        final LiveEditAttributeInjection liveEditAttributeInjection = new LiveEditAttributeInjection();
        final PortalResponse.Builder responseBuilder = PortalResponse.create().body( html );

        final PortalResponse portalResponse =
            liveEditAttributeInjection.injectLiveEditAttribute( responseBuilder.build(), TextComponentType.INSTANCE );

        final String outputHtml = portalResponse.getBody().toString();
        final String expectedResult = readResource( "part7Rendered.html" );

        assertEquals( expectedResult, outputHtml );
    }

    @Test
    public void injectWithSingleComment()
        throws Exception
    {
        final String html = readResource( "part8Source.html" );

        final LiveEditAttributeInjection liveEditAttributeInjection = new LiveEditAttributeInjection();
        final PortalResponse.Builder responseBuilder = PortalResponse.create().body( html );

        final PortalResponse portalResponse =
            liveEditAttributeInjection.injectLiveEditAttribute( responseBuilder.build(), TextComponentType.INSTANCE );

        final String outputHtml = portalResponse.getBody().toString();
        final String expectedResult = readResource( "part8Rendered.html" );

        assertEquals( expectedResult, outputHtml );
    }

    @Test
    public void injectWithMultipleComments()
        throws Exception
    {
        final String html = readResource( "part9Source.html" );

        final LiveEditAttributeInjection liveEditAttributeInjection = new LiveEditAttributeInjection();
        final PortalResponse.Builder responseBuilder = PortalResponse.create().body( html );

        final PortalResponse portalResponse =
            liveEditAttributeInjection.injectLiveEditAttribute( responseBuilder.build(), TextComponentType.INSTANCE );

        final String outputHtml = portalResponse.getBody().toString();
        final String expectedResult = readResource( "part9Rendered.html" );

        assertEquals( expectedResult, outputHtml );
    }

    @Test
    public void injectHtmlWithBom()
        throws Exception
    {
        final String html = readResource( "part1SourceBom.html" );

        final LiveEditAttributeInjection liveEditAttributeInjection = new LiveEditAttributeInjection();
        final PortalResponse.Builder responseBuilder = PortalResponse.create().body( html );

        final PortalResponse portalResponse =
            liveEditAttributeInjection.injectLiveEditAttribute( responseBuilder.build(), TextComponentType.INSTANCE );

        final String outputHtml = portalResponse.getBody().toString();
        final String expectedResult = readResource( "part1RenderedBom.html" );

        assertEquals( expectedResult, outputHtml );
    }

    @Test
    public void injectEmptyHtml()
        throws Exception
    {
        final String html = "";

        final LiveEditAttributeInjection liveEditAttributeInjection = new LiveEditAttributeInjection();
        final PortalResponse.Builder responseBuilder = PortalResponse.create().body( html );

        final PortalResponse portalResponse =
            liveEditAttributeInjection.injectLiveEditAttribute( responseBuilder.build(), TextComponentType.INSTANCE );

        final String outputHtml = portalResponse.getBody().toString();
        final String expectedResult = "";

        assertEquals( expectedResult, outputHtml );
    }

    private String readResource( final String resourceName )
        throws Exception
    {
        return Resources.toString( getClass().getResource( resourceName ), Charsets.UTF_8 );
    }
}