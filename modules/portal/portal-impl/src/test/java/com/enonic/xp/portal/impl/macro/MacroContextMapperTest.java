package com.enonic.xp.portal.impl.macro;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.impl.ContentFixtures;
import com.enonic.xp.portal.impl.MapSerializableAssert;
import com.enonic.xp.portal.macro.MacroContext;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.web.HttpMethod;

public class MacroContextMapperTest
{
    private final MapSerializableAssert assertHelper = new MapSerializableAssert( MacroContextMapperTest.class );

    private MacroContext macroContext;

    @BeforeEach
    public void setup()
    {
        PortalRequest portalRequest = new PortalRequest();
        portalRequest.setMethod( HttpMethod.GET );
        portalRequest.setScheme( "http" );
        portalRequest.setHost( "localhost" );
        portalRequest.setPort( 80 );
        portalRequest.setRepositoryId( RepositoryId.from( "com.enonic.cms.myproject" ) );
        portalRequest.setBranch( ContentConstants.BRANCH_DRAFT );
        portalRequest.setPath( "/site/live/master/a/b" );
        portalRequest.setRawPath( "/site/live/master/a/b" );
        portalRequest.setContextPath( "/site/live/master/a" );
        portalRequest.setUrl( "http://localhost/site/live/master/a/b?param1=value1" );
        portalRequest.setRemoteAddress( "127.0.0.1" );
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

        this.macroContext = MacroContext.create()
            .name( "macroName" )
            .body( "body" )
            .param( "firstParam", "firstParamValue" )
            .param( "firstParam", "firstParamSecondValue" )
            .param( "secondParam", "secondParamValue" )
            .request( portalRequest )
            .document( "<h1>document</h1>" )
            .build();
    }

    @Test
    void testMapping()
    {
        assertHelper.assertJson( "MacroContextMapperTest-mapping.json", new MacroContextMapper( this.macroContext ) );
    }
}
