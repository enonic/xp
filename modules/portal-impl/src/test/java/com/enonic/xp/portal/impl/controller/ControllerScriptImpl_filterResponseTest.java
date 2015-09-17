package com.enonic.xp.portal.impl.controller;

import org.junit.Test;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.portal.impl.postprocess.injection.ContributionInjection;
import com.enonic.xp.web.HttpStatus;

import static org.junit.Assert.*;

public class ControllerScriptImpl_filterResponseTest
    extends AbstractControllerTest
{

    @Test
    public void testFilterResponse()
        throws Exception
    {
        this.postProcessor.addInjection( new ContributionInjection() );

        this.portalRequest.setMethod( "GET" );
        this.portalRequest.setPort( 80 );
        this.portalRequest.setApplicationKey( ApplicationKey.from( "myapplication" ) );

        execute( "myapplication:/site/pages/mypage/mypage.js" );

        assertEquals( HttpStatus.ACCEPTED.value(), this.portalResponse.getStatus() );
        assertJson( "simple", getResponseAsJson() );
        assertHtml( "simple", getResponseAsString() );
    }

    @Test
    public void testFilterResponse_no_filters()
        throws Exception
    {
        this.postProcessor.addInjection( new ContributionInjection() );

        this.portalRequest.setMethod( "GET" );
        this.portalRequest.setPort( 80 );
        this.portalRequest.setApplicationKey( ApplicationKey.from( "myapplication" ) );

        execute( "myapplication:/site/pages/nofilters/nofilters.js" );

        assertEquals( HttpStatus.OK.value(), this.portalResponse.getStatus() );
        assertJson( "nofilters", getResponseAsJson() );
        assertHtml( "nofilters", getResponseAsString() );
    }

}

