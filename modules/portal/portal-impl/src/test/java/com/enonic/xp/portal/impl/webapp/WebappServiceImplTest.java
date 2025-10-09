package com.enonic.xp.portal.impl.webapp;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.core.impl.app.ApplicationTestSupport;
import com.enonic.xp.descriptor.DescriptorKey;
import com.enonic.xp.webapp.WebappDescriptor;

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
        final List<DescriptorKey> apiMounts = webappDescriptor.getApiMounts().stream().toList();
        assertEquals( 2, apiMounts.size() );

        final DescriptorKey apiMountDescriptor1 = apiMounts.get( 0 );
        assertEquals( ApplicationKey.from( "com.enonic.app.myapp" ), apiMountDescriptor1.getApplicationKey() );
        assertEquals( "api-key1", apiMountDescriptor1.getName() );

        final DescriptorKey apiMountDescriptor2 = apiMounts.get( 1 );
        assertEquals( ApplicationKey.from( "com.enonic.app.myapp" ), apiMountDescriptor2.getApplicationKey() );
        assertEquals( "api-key2", apiMountDescriptor2.getName() );
    }

    @Test
    void testGetDescriptorInvalid()
    {
        final ApplicationKey applicationKey = ApplicationKey.from( "mywebapp2" );

        final Exception ex = assertThrows( Exception.class, () -> this.service.getDescriptor( applicationKey ) );
        assertTrue( ex.getMessage().contains( "Unrecognized field \"unsupported-apis\"" ) );
    }

    @Test
    void testGetDescriptorDorUnknownApplication()
    {
        final ApplicationKey applicationKey = ApplicationKey.from( "unknown" );
        final WebappDescriptor webappDescriptor = this.service.getDescriptor( applicationKey );
        assertNull( webappDescriptor );
    }
}
