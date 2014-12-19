package com.enonic.wem.portal.internal.mapper;

import javax.servlet.http.HttpServletRequest;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.api.module.ModuleKey;
import com.enonic.wem.portal.internal.controller.PortalContextImpl;
import com.enonic.wem.portal.internal.controller.PortalRequestImpl;
import com.enonic.wem.script.AbstractMapSerializableTest;
import com.enonic.wem.script.mapper.ContentFixtures;
import com.enonic.xp.web.servlet.ServletRequestHolder;

public class PortalContextMapperTest
    extends AbstractMapSerializableTest
{

    @Before
    public void setUp()
    {
        final HttpServletRequest req = Mockito.mock( HttpServletRequest.class );
        Mockito.when( req.getScheme() ).thenReturn( "http" );
        Mockito.when( req.getServerName() ).thenReturn( "localhost" );
        Mockito.when( req.getLocalPort() ).thenReturn( 80 );
        Mockito.when( req.getContextPath() ).thenReturn( "/" );
        ServletRequestHolder.setRequest( req );
    }

    @Test
    public void testSimple()
        throws Exception
    {

        final PortalRequestImpl request = new PortalRequestImpl();
        request.setMethod( "GET" );
        request.addParam( "param1", "value1" );
        request.addParam( "param2", "value2" );
        request.addParam( "param3", "value3-A" );
        request.addParam( "param3", "value3-B" );

        final PortalContextImpl context = new PortalContextImpl();
        context.setModule( ModuleKey.from( "mymodule" ) );
        context.setRequest( request );
        context.setContent( ContentFixtures.newContent() );
        context.setSite( ContentFixtures.newSite() );
        context.setPageDescriptor( ContentFixtures.newPageDescriptor() );

        assertJson( "simple", new PortalContextMapper( context ) );
    }
}
