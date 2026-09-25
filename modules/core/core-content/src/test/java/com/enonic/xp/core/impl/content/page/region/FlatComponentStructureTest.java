package com.enonic.xp.core.impl.content.page.region;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.core.impl.app.ApplicationTestSupport;
import com.enonic.xp.core.impl.content.page.PageDescriptorLoader;
import com.enonic.xp.descriptor.DescriptorKey;
import com.enonic.xp.region.PartDescriptor;
import com.enonic.xp.resource.Resource;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.schema.content.CmsFormFragmentService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Component descriptors are resolved in the flat structure {@code cms/<kind>/<name>.yaml} as well as in the legacy folder structure;
 * the flat structure wins. Controllers stay in the folder of the component.
 */
class FlatComponentStructureTest
    extends ApplicationTestSupport
{
    private static final ApplicationKey APP = ApplicationKey.from( "flatapp" );

    private PartDescriptorLoader partLoader;

    private LayoutDescriptorLoader layoutLoader;

    private PageDescriptorLoader pageLoader;

    @Override
    protected void initialize()
    {
        addApplication( "flatapp", "/apps/flatapp" );

        final CmsFormFragmentService formFragmentService = Mockito.mock( CmsFormFragmentService.class );
        this.partLoader = new PartDescriptorLoader( this.resourceService, formFragmentService );
        this.layoutLoader = new LayoutDescriptorLoader( this.resourceService, formFragmentService );
        this.pageLoader = new PageDescriptorLoader( this.resourceService, formFragmentService );
    }

    @Test
    void parts_flat_and_legacy()
        throws Exception
    {
        assertEquals( List.of( "flatapp:flatpart", "flatapp:mixed" ),
                      partLoader.find( APP ).stream().map( DescriptorKey::toString ).sorted().toList() );

        final PartDescriptor flat = loadPart( "flatpart" );
        assertEquals( "Flat part", flat.getTitle() );
        assertNotNull( flat.getIcon() );
        assertEquals( "image/png", flat.getIcon().getMimeType() );
        // the controller stays in the folder of the part
        assertEquals( "flatapp:/cms/parts/flatpart", flat.getComponentPath().toString() );

        final PartDescriptor mixed = loadPart( "mixed" );
        assertEquals( "Flat", mixed.getTitle() );
        assertNull( mixed.getIcon() );
    }

    @Test
    void layouts_and_pages_flat()
        throws Exception
    {
        final DescriptorKey layoutKey = DescriptorKey.from( APP, "flatlayout" );
        assertEquals( List.of( layoutKey ), layoutLoader.find( APP ).stream().toList() );
        assertEquals( "Flat layout", layoutLoader.load( layoutKey, resource( layoutLoader.toResource( layoutKey ) ) ).getTitle() );

        final DescriptorKey pageKey = DescriptorKey.from( APP, "flatpage" );
        assertEquals( List.of( pageKey ), pageLoader.find( APP ).stream().toList() );
        assertEquals( "Flat page", pageLoader.load( pageKey, resource( pageLoader.toResource( pageKey ) ) ).getTitle() );
    }

    private PartDescriptor loadPart( final String name )
        throws Exception
    {
        final DescriptorKey key = DescriptorKey.from( APP, name );
        return partLoader.load( key, resource( partLoader.toResource( key ) ) );
    }

    private Resource resource( final ResourceKey key )
    {
        final Resource resource = this.resourceService.getResource( key );
        assertNotNull( resource );
        return resource;
    }
}
