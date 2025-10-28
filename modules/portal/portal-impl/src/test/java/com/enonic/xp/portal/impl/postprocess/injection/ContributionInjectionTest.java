package com.enonic.xp.portal.impl.postprocess.injection;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.portal.postprocess.HtmlTag;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ContributionInjectionTest
{
    private ContributionInjection injection;

    private PortalRequest request;

    private PortalResponse.Builder responseBuilder;

    @BeforeEach
    void setup()
    {
        this.injection = new ContributionInjection();
        this.request = new PortalRequest();
        this.responseBuilder = PortalResponse.create();
    }

    private List<String> doInject( final HtmlTag tag )
    {
        return this.injection.inject( this.request, this.responseBuilder.build(), tag );
    }

    @Test
    void testInject()
    {
        this.responseBuilder.contribution( HtmlTag.BODY_END, "1" );
        this.responseBuilder.contribution( HtmlTag.BODY_END, "2" );

        final List<String> result = doInject( HtmlTag.BODY_END );
        assertNotNull( result );
        assertEquals( 2, result.size() );
        assertEquals( "1,2", String.join( ",", result ) );
    }

    @Test
    void testInject_noContributions()
    {
        final List<String> result = doInject( HtmlTag.BODY_END );
        assertNotNull( result );
        assertEquals( 0, result.size() );
    }
}
