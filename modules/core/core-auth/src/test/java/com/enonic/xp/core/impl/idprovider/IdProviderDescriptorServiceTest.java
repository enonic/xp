package com.enonic.xp.core.impl.idprovider;

import org.junit.jupiter.api.Test;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.core.impl.app.ApplicationTestSupport;
import com.enonic.xp.idprovider.IdProviderDescriptor;
import com.enonic.xp.idprovider.IdProviderDescriptorMode;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class IdProviderDescriptorServiceTest
    extends ApplicationTestSupport
{

    protected IdProviderDescriptorServiceImpl service;

    @Override
    protected void initialize()
    {
        addApplication( "myapp1", "/apps/myapp1" );
        addApplication( "myapp2", "/apps/myapp2" );
        addApplication( "myapp3", "/apps/myapp3" );
        this.service = new IdProviderDescriptorServiceImpl();
        this.service.setResourceService( this.resourceService );
    }

    @Test
    void testGetDescriptor()
    {
        final IdProviderDescriptor idProviderDescriptor = this.service.getDescriptor( ApplicationKey.from( "myapp1" ) );

        assertNotNull( idProviderDescriptor );
        assertEquals( ApplicationKey.from( "myapp1" ), idProviderDescriptor.getKey() );
        assertEquals( IdProviderDescriptorMode.MIXED, idProviderDescriptor.getMode() );
        assertNotNull( idProviderDescriptor.getConfig().getInput( "title" ) );
        assertNotNull( idProviderDescriptor.getConfig().getInput( "defaultPrincipals" ) );
    }

    @Test
    void testGetDescriptorNotFound()
    {
        final IdProviderDescriptor idProviderDescriptor = this.service.getDescriptor( ApplicationKey.from( "nonexistent" ) );
        assertNull( idProviderDescriptor );
    }

    @Test
    void testGetDescriptorWithFormFragmentThrows()
    {
        final IllegalArgumentException ex =
            assertThrows( IllegalArgumentException.class, () -> this.service.getDescriptor( ApplicationKey.from( "myapp2" ) ) );
        assertEquals( "IdProviderDescriptor form cannot contain FormFragment: my-fragment", ex.getMessage() );
    }

    @Test
    void testGetDescriptorWithNestedFormFragmentThrows()
    {
        final IllegalArgumentException ex =
            assertThrows( IllegalArgumentException.class, () -> this.service.getDescriptor( ApplicationKey.from( "myapp3" ) ) );
        assertEquals( "IdProviderDescriptor form cannot contain FormFragment: my-fragment", ex.getMessage() );
    }
}
