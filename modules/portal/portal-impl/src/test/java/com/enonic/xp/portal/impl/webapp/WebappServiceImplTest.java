package com.enonic.xp.portal.impl.webapp;

import org.junit.jupiter.api.Test;

import com.enonic.xp.api.ApiMountDescriptor;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.core.impl.app.ApplicationTestSupport;
import com.enonic.xp.webapp.WebappDescriptor;
import com.enonic.xp.xml.XmlException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class WebappServiceImplTest
    extends ApplicationTestSupport
{

    private WebappServiceImpl service;

    @Override
    protected void initialize()
        throws Exception
    {
        addApplication( "mywebapp", "/apps/mywebapp" );
        addApplication( "mywebapp2", "/apps/mywebapp2" );

        service = new WebappServiceImpl( this.resourceService );
    }

    @Test
    void testGetDescriptor()
    {
        final ApplicationKey applicationKey = ApplicationKey.from( "mywebapp" );
        final WebappDescriptor webappDescriptor = this.service.getDescriptor( applicationKey );
        assertEquals( 2, webappDescriptor.getApiMounts().getSize() );

        final ApiMountDescriptor apiMountDescriptor1 = webappDescriptor.getApiMounts().get( 0 );
        assertEquals( ApplicationKey.from( "com.enonic.app.myapp" ), apiMountDescriptor1.getApplicationKey() );
        assertEquals( "api-key1", apiMountDescriptor1.getApiKey() );

        final ApiMountDescriptor apiMountDescriptor2 = webappDescriptor.getApiMounts().get( 1 );
        assertEquals( ApplicationKey.from( "com.enonic.app.myapp" ), apiMountDescriptor2.getApplicationKey() );
        assertEquals( "api-key2", apiMountDescriptor2.getApiKey() );
    }

    @Test
    void testGetDescriptorInvalid()
    {
        final ApplicationKey applicationKey = ApplicationKey.from( "mywebapp2" );

        final XmlException ex = assertThrows( XmlException.class, () -> this.service.getDescriptor( applicationKey ) );
        assertTrue( ex.getMessage().contains( "Could not load webapp descriptor" ) );
    }

    @Test
    void testGetDescriptorDorUnknownApplication()
    {
        final ApplicationKey applicationKey = ApplicationKey.from( "unknown" );
        final WebappDescriptor webappDescriptor = this.service.getDescriptor( applicationKey );
        assertNull( webappDescriptor );
    }
}
