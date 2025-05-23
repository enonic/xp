package com.enonic.xp.core.impl.content.serializer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.enonic.xp.data.PropertySet;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.form.Form;
import com.enonic.xp.descriptor.DescriptorKey;
import com.enonic.xp.region.LayoutComponent;
import com.enonic.xp.region.LayoutComponentType;
import com.enonic.xp.region.LayoutDescriptor;
import com.enonic.xp.region.LayoutDescriptorService;
import com.enonic.xp.region.RegionDescriptor;
import com.enonic.xp.region.RegionDescriptors;

import static com.enonic.xp.core.impl.content.serializer.DescriptorBasedComponentDataSerializer.DESCRIPTOR;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class FullLayoutComponentDataSerializerTest
{
    private LayoutDescriptorService layoutDescriptorService;

    private FullLayoutComponentDataSerializer componentDataSerializer;

    @BeforeEach
    public void setUp()
    {
        this.layoutDescriptorService = Mockito.mock( LayoutDescriptorService.class );
        this.componentDataSerializer = new FullLayoutComponentDataSerializer( layoutDescriptorService, new RegionDataSerializer(
            new ComponentDataSerializerProvider() ) );
    }

    @Test
    public void testRegionsFetched()
    {
        final DescriptorKey pageDescriptorKey = DescriptorKey.from( "myapplication:my-page" );
        final RegionDescriptors regions = RegionDescriptors.create().add( RegionDescriptor.create().name( "main" ).build() ).build();
        final LayoutDescriptor layoutDescriptor = LayoutDescriptor.create().key( pageDescriptorKey )
            .config( Form.create().build() )
            .regions( regions )
            .build();

        final PropertyTree newPropertyTree = new PropertyTree();
        final PropertySet layoutRootData = newPropertyTree.getRoot();
        final PropertySet layoutData = layoutRootData.addSet( LayoutComponentType.INSTANCE.toString() );

        Mockito.when( layoutDescriptorService.getByKey( pageDescriptorKey ) ).thenReturn( layoutDescriptor );

        layoutData.addString( DESCRIPTOR, "myapplication:my-page" );

        final LayoutComponent layoutComponent = componentDataSerializer.fromData( layoutRootData );

        assertNotNull( layoutComponent );
        assertNotNull( layoutComponent.getRegion( "main" ) );
    }
}
