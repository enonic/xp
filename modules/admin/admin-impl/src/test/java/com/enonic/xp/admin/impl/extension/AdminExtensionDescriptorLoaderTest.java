package com.enonic.xp.admin.impl.extension;

import java.util.Objects;

import org.junit.jupiter.api.Test;

import com.enonic.xp.admin.extension.AdminExtensionDescriptor;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.core.impl.app.ApplicationTestSupport;
import com.enonic.xp.descriptor.DescriptorKey;
import com.enonic.xp.descriptor.DescriptorKeys;
import com.enonic.xp.resource.Resource;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.security.PrincipalKey;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AdminExtensionDescriptorLoaderTest
    extends ApplicationTestSupport
{
    private AdminExtensionDescriptorLoader loader;

    @Override
    protected void initialize()
    {
        this.loader = new AdminExtensionDescriptorLoader( this.resourceService );

        final Resource resource = mock( Resource.class );
        when( resource.exists() ).thenReturn( true );
        addApplication( "myapp1", "/apps/myapp1" );
    }

    @Test
    void testGetType()
    {
        assertEquals( AdminExtensionDescriptor.class, this.loader.getType() );
    }

    @Test
    void testPostProcess()
    {
        final AdminExtensionDescriptor descriptor = AdminExtensionDescriptor.create().key( DescriptorKey.from( "myapp:a" ) ).build();
        assertSame( descriptor, this.loader.postProcess( descriptor ) );
    }

    @Test
    void testCreateDefault()
    {
        final DescriptorKey key = DescriptorKey.from( "myapp1:extension1" );
        final AdminExtensionDescriptor descriptor = this.loader.createDefault( key );

        assertEquals( key, descriptor.getKey() );
        assertEquals( "extension1", descriptor.getName() );
    }

    @Test
    void testFind()
    {
        final DescriptorKeys keys = this.loader.find( ApplicationKey.from( "myapp1" ) );
        assertThat( keys ).map( Objects::toString ).containsExactlyInAnyOrder( "myapp1:extension1", "myapp1:extension2" );
    }

    @Test
    void testLoadMax()
    {
        final DescriptorKey descriptorKey = DescriptorKey.from( "myapp1:extension1" );

        final ResourceKey resourceKey = this.loader.toResource( descriptorKey );
        assertEquals( "myapp1:/admin/extensions/extension1/extension1.yml", resourceKey.toString() );

        final Resource resource = this.resourceService.getResource( resourceKey );
        final AdminExtensionDescriptor descriptor = this.loader.load( descriptorKey, resource );

        assertEquals( "My Extension", descriptor.getDisplayName() );
        assertEquals( 1, descriptor.getInterfaces().size() );
        assertTrue( descriptor.getInterfaces().contains( "com.enonic.xp.my-interface" ) );
        assertEquals( 1, descriptor.getAllowedPrincipals().getSize() );
        assertTrue( descriptor.getAllowedPrincipals().contains( PrincipalKey.from( "role:system.user.admin" ) ) );
    }

    @Test
    void testLoadMin()
    {
        final DescriptorKey descriptorKey = DescriptorKey.from( "myapp1:extension2" );
        final ResourceKey resourceKey = this.loader.toResource( descriptorKey );
        final Resource resource = this.resourceService.getResource( resourceKey );
        final AdminExtensionDescriptor descriptor = this.loader.load( descriptorKey, resource );

        assertEquals( "MyExtension2", descriptor.getDisplayName() );
        assertEquals( "MyExtension2 description", descriptor.getDescription() );
        assertEquals( 1, descriptor.getInterfaces().size() );
        assertTrue( descriptor.getInterfaces().contains( "com.enonic.xp.my-interface" ) );
        assertNull( descriptor.getAllowedPrincipals() );
    }
}
