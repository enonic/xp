package com.enonic.xp.core.impl.schema;

import java.util.Set;

import org.junit.jupiter.api.Test;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.resource.Resource;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.resource.ResourceService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CmsResourceKeysTest
{
    private static final ApplicationKey APP = ApplicationKey.from( "myapp" );

    @Test
    void flat_wins_over_legacy()
    {
        final ResourceService service = service( "/cms/content-types/t/t.yaml", "/cms/content-types/t.yml" );

        assertEquals( "myapp:/cms/content-types/t.yml", CmsResourceKeys.descriptorKey( service, APP, "/cms/content-types", "t" ).toString() );
    }

    @Test
    void yaml_wins_over_yml()
    {
        final ResourceService service = service( "/cms/content-types/t.yml", "/cms/content-types/t.yaml" );

        assertEquals( "myapp:/cms/content-types/t.yaml", CmsResourceKeys.descriptorKey( service, APP, "/cms/content-types", "t" ).toString() );
    }

    @Test
    void legacy()
    {
        final ResourceService service = service( "/cms/content-types/t/t.yml" );

        assertEquals( "myapp:/cms/content-types/t/t.yml", CmsResourceKeys.descriptorKey( service, APP, "/cms/content-types", "t" ).toString() );
    }

    @Test
    void missing()
    {
        final ResourceService service = service();

        assertNull( CmsResourceKeys.findDescriptorKey( service, APP, "/cms/content-types", "t" ) );
        assertEquals( "myapp:/cms/content-types/t.yaml", CmsResourceKeys.descriptorKey( service, APP, "/cms/content-types", "t" ).toString() );
    }

    @Test
    void sibling()
    {
        final ApplicationKey system = ApplicationKey.from( "com.enonic.xp.app.system" );

        assertEquals( "myapp:/cms/parts/p.svg",
                      CmsResourceKeys.siblingKey( APP, ResourceKey.from( system, "/cms/parts/p.yaml" ), "svg" ).toString() );
        assertEquals( "myapp:/cms/parts/p/p.png", CmsResourceKeys.siblingKey( APP, ResourceKey.from( APP, "/cms/parts/p/p.yml" ), "png" ).toString() );
    }

    private static ResourceService service( final String... existing )
    {
        final Set<String> paths = Set.of( existing );
        final ResourceService service = mock( ResourceService.class );
        when( service.getResource( any() ) ).thenAnswer( invocation -> {
            final ResourceKey key = invocation.getArgument( 0 );
            final Resource resource = mock( Resource.class );
            when( resource.exists() ).thenReturn( paths.contains( key.getPath() ) );
            return resource;
        } );
        return service;
    }
}
