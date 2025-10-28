package com.enonic.xp.portal.impl.rendering;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;

import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.region.TextComponentType;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LiveEditAttributeInjectionTest
{
    @Test
    void simpleInjection()
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
    void injectWithAttributes()
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
    void injectEmptySingleTag()
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
    void injectWithXmlDeclaration()
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
    void injectWithDocType()
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
    void nonSingleRootTagHtml()
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
    void injectWithLeadingWhitespace()
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
    void injectWithSingleComment()
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
    void injectWithMultipleComments()
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
    void injectHtmlWithBom()
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
    void injectEmptyHtml()
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
        try (InputStream stream = getClass().getResourceAsStream( resourceName ))
        {
            return new String( stream.readAllBytes(), StandardCharsets.UTF_8 );
        }
    }
}
