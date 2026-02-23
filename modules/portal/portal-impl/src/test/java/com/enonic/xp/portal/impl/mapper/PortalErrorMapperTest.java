package com.enonic.xp.portal.impl.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.branch.Branch;
import com.enonic.xp.exception.NotFoundException;
import com.enonic.xp.node.NodeNotFoundException;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.RenderMode;
import com.enonic.xp.portal.impl.ContentFixtures;
import com.enonic.xp.portal.impl.MapSerializableAssert;
import com.enonic.xp.portal.impl.error.PortalError;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.web.HttpMethod;
import com.enonic.xp.web.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;

class PortalErrorMapperTest
{
    private PortalError error;

    private final MapSerializableAssert assertHelper = new MapSerializableAssert( PortalErrorMapperTest.class );

    @BeforeEach
    void setup()
    {
        final PortalRequest portalRequest = new PortalRequest();
        portalRequest.setMode( RenderMode.LIVE );
        portalRequest.setMethod( HttpMethod.GET );
        portalRequest.setScheme( "http" );
        portalRequest.setHost( "localhost" );
        portalRequest.setPort( 80 );
        portalRequest.setRepositoryId( RepositoryId.from( "com.enonic.cms.myproject" ) );
        portalRequest.setBranch( Branch.from( "master" ) );
        portalRequest.setPath( "/site/live/master/a/b" );
        portalRequest.setRawPath( "/site/live/master/a/b" );
        portalRequest.setContextPath( "/site/live/master/a" );
        portalRequest.setUrl( "http://localhost/site/live/master/a/b?param1=value1" );
        portalRequest.setRemoteAddress( "10.0.0.1" );
        portalRequest.getParams().put( "param1", "value1" );
        portalRequest.getParams().put( "param2", "value2" );
        portalRequest.getParams().put( "param3", "value3-A" );
        portalRequest.getParams().put( "param3", "value3-B" );

        portalRequest.getHeaders().put( "header1", "value1" );
        portalRequest.getHeaders().put( "header2", "value2" );
        portalRequest.getHeaders().put( "header3", "value3" );

        portalRequest.setApplicationKey( ApplicationKey.from( "myapplication" ) );
        portalRequest.setContent( ContentFixtures.newContent() );
        portalRequest.setSite( ContentFixtures.newSite() );
        portalRequest.setPageDescriptor( ContentFixtures.newPageDescriptor() );

        final NotFoundException exception = new NodeNotFoundException( "Not found." );

        final PortalError.Builder errorBuilder = PortalError.create();
        errorBuilder.status( HttpStatus.NOT_FOUND ).message( "Message: Not found." ).exception( exception ).request( portalRequest );

        this.error = errorBuilder.build();
    }

    @Test
    void testSimple()
    {
        final PortalErrorMapper value = new PortalErrorMapper( this.error );
        assertHelper.assertJson( "error-simple.json", value );
        final Exception exception = MapSerializableAssert.serializeJs( value ).getMember( "exception" ).getValue( Exception.class );
        assertThat( exception ).isInstanceOf( NotFoundException.class ).message().isEqualTo( "Not found." );
    }
}
