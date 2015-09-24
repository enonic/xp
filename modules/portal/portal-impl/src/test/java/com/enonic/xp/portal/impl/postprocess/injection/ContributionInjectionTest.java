package com.enonic.xp.portal.impl.postprocess.injection;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.google.common.base.Joiner;

import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.portal.postprocess.HtmlTag;

import static org.junit.Assert.*;

public class ContributionInjectionTest
{
    private ContributionInjection injection;

    private PortalRequest request;

    private PortalResponse.Builder responseBuilder;

    @Before
    public void setup()
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
    public void testInject()
    {
        this.responseBuilder.contribution( HtmlTag.BODY_END, "1" );
        this.responseBuilder.contribution( HtmlTag.BODY_END, "2" );

        final List<String> result = doInject( HtmlTag.BODY_END );
        assertNotNull( result );
        assertEquals( 2, result.size() );
        assertEquals( "1,2", Joiner.on( "," ).join( result ) );
    }

    @Test
    public void testInject_noContributions()
    {
        final List<String> result = doInject( HtmlTag.BODY_END );
        assertNotNull( result );
        assertEquals( 0, result.size() );
    }
}
