package com.enonic.xp.core.impl.content.serializer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.enonic.xp.core.impl.content.page.AbstractDataSerializerTest;
import com.enonic.xp.data.PropertySet;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.form.Form;
import com.enonic.xp.descriptor.DescriptorKey;
import com.enonic.xp.page.Page;
import com.enonic.xp.page.PageDescriptor;
import com.enonic.xp.page.PageDescriptorService;
import com.enonic.xp.region.ComponentPath;
import com.enonic.xp.region.LayoutDescriptorService;
import com.enonic.xp.region.RegionDescriptor;
import com.enonic.xp.region.RegionDescriptors;

import static com.enonic.xp.content.ContentPropertyNames.PAGE;
import static com.enonic.xp.core.impl.content.serializer.ComponentDataSerializer.COMPONENTS;
import static com.enonic.xp.core.impl.content.serializer.ComponentDataSerializer.PATH;
import static com.enonic.xp.core.impl.content.serializer.ComponentDataSerializer.TYPE;
import static com.enonic.xp.core.impl.content.serializer.DescriptorBasedComponentDataSerializer.DESCRIPTOR;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FullPageDataSerializerTest
    extends AbstractDataSerializerTest
{

    private FullContentDataSerializer contentDataSerializer;

    private PageDescriptorService pageDescriptorService;

    @BeforeEach
    public void setUp()
    {
        this.pageDescriptorService = Mockito.mock( PageDescriptorService.class );
        this.contentDataSerializer = FullContentDataSerializer.create()
            .layoutDescriptorService( Mockito.mock( LayoutDescriptorService.class ) )
            .pageDescriptorService( pageDescriptorService )
            .build();
    }

    @Test
    public void testRegionsFetched()
    {
        final DescriptorKey pageDescriptorKey = DescriptorKey.from( "myapplication:my-page" );
        final RegionDescriptors regions = RegionDescriptors.create().add( RegionDescriptor.create().name( "main" ).build() ).build();
        final PageDescriptor pageDescriptor = PageDescriptor.create()
            .key( pageDescriptorKey )
            .regions( regions )
            .config( Form.create().build() )
            .build();

        Mockito.when( pageDescriptorService.getByKey( pageDescriptorKey ) ).thenReturn( pageDescriptor );

        final PropertyTree newPropertyTree = new PropertyTree();
        final PropertySet contentAsData = newPropertyTree.getRoot();
        final PropertySet asSet = contentAsData.addSet( COMPONENTS );

        asSet.setString( TYPE, PAGE );
        asSet.setString( PATH, ComponentPath.DIVIDER );

        final PropertySet specialBlockSet = asSet.addSet( PAGE );
        specialBlockSet.addString( DESCRIPTOR, pageDescriptorKey.toString() );

        final Page parsedPage = contentDataSerializer.fromPageData( contentAsData );

        assertTrue( parsedPage.hasRegions() );
        assertNotNull( parsedPage.getRegions().getRegion( "main" ) );
    }
}
