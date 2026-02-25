package com.enonic.xp.admin.impl.tool;

import java.util.Objects;

import org.junit.jupiter.api.Test;

import com.enonic.xp.admin.tool.AdminToolDescriptor;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.core.impl.app.ApplicationTestSupport;
import com.enonic.xp.descriptor.DescriptorKey;
import com.enonic.xp.descriptor.DescriptorKeys;
import com.enonic.xp.resource.Resource;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.security.PrincipalKey;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

class AdminToolDescriptorLoaderTest
    extends ApplicationTestSupport
{
    private AdminToolDescriptorLoader loader;

    @Override
    protected void initialize()
    {
        this.loader = new AdminToolDescriptorLoader( this.resourceService );

        addApplication( "myapp1", "/apps/myapp1" );
        addApplication( "myapp2", "/apps/myapp2" );
    }

    @Test
    void testGetType()
    {
        assertEquals( AdminToolDescriptor.class, this.loader.getType() );
    }

    @Test
    void testPostProcess()
    {
        final AdminToolDescriptor descriptor =
            AdminToolDescriptor.create().key( DescriptorKey.from( "myapp1:myadmintool" ) ).build();
        assertSame( descriptor, this.loader.postProcess( descriptor ) );
    }

    @Test
    void testCreateDefault()
    {
        final DescriptorKey key = DescriptorKey.from( "myapp1:myadmintool" );
        final AdminToolDescriptor descriptor = this.loader.createDefault( key );

        assertEquals( key, descriptor.getKey() );
        assertEquals( "myadmintool", descriptor.getDisplayName() );
    }

    @Test
    void testFind()
    {
        final DescriptorKeys keys = this.loader.find( ApplicationKey.from( "myapp1" ) );
        assertThat( keys ).map( Objects::toString ).containsExactlyInAnyOrder( "myapp1:myadmintool" );
    }

    @Test
    void testLoad()
    {
        final DescriptorKey descriptorKey = DescriptorKey.from( "myapp1:myadmintool" );

        final ResourceKey resourceKey = this.loader.toResource( descriptorKey );
        assertEquals( "myapp1:/admin/tools/myadmintool/myadmintool.yml", resourceKey.toString() );

        final Resource resource = this.resourceService.getResource( resourceKey );
        final AdminToolDescriptor descriptor = this.loader.load( descriptorKey, resource );

        assertEquals( "My admin tool", descriptor.getDisplayName() );
        assertEquals( "My admin tool description", descriptor.getDescription() );
        assertEquals( 1, descriptor.getAllowedPrincipals().getSize() );
        assertThat( descriptor.getAllowedPrincipals() ).contains( PrincipalKey.from( "role:system.user.admin" ) );
        assertNull( descriptor.getIcon() );
    }

    @Test
    void testLoadWithIcon()
    {
        final DescriptorKey descriptorKey = DescriptorKey.from( "myapp2:myadmintool" );

        final ResourceKey resourceKey = this.loader.toResource( descriptorKey );
        final Resource resource = this.resourceService.getResource( resourceKey );
        final AdminToolDescriptor descriptor = this.loader.load( descriptorKey, resource );

        assertEquals( "My admin tool2", descriptor.getDisplayName() );
        assertNotNull( descriptor.getIcon() );
    }
}
