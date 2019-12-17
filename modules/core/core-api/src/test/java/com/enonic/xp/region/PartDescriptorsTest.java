package com.enonic.xp.region;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.enonic.xp.form.Form;
import com.enonic.xp.page.DescriptorKey;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PartDescriptorsTest
{

    private static final List<PartDescriptor> PART_DESCRIPTORS_LIST = new ArrayList<>();

    @BeforeAll
    public static void initRegionDescriptors()
    {
        final PartDescriptor partDescriptor1 = PartDescriptor.create().
            displayName( "News part" ).
            config( Form.create().build() ).
            key( DescriptorKey.from( "module:new-part" ) ).
            build();

        final PartDescriptor partDescriptor2 = PartDescriptor.create().
            displayName( "News part2" ).
            config( Form.create().build() ).
            key( DescriptorKey.from( "module:new-part2" ) ).
            build();

        final PartDescriptor partDescriptor3 = PartDescriptor.create().
            displayName( "News part3" ).
            config( Form.create().build() ).
            key( DescriptorKey.from( "module:new-part3" ) ).
            build();

        PART_DESCRIPTORS_LIST.add( partDescriptor1 );
        PART_DESCRIPTORS_LIST.add( partDescriptor2 );
        PART_DESCRIPTORS_LIST.add( partDescriptor3 );
    }

    @Test
    public void fromArray()
    {
        final PartDescriptors partDescriptors =
            PartDescriptors.from( PART_DESCRIPTORS_LIST.get( 0 ), PART_DESCRIPTORS_LIST.get( 1 ), PART_DESCRIPTORS_LIST.get( 2 ) );

        assertEquals( 3, partDescriptors.getSize() );
        assertTrue( partDescriptors.contains( PART_DESCRIPTORS_LIST.get( 0 ) ) );
        assertTrue( partDescriptors.contains( PART_DESCRIPTORS_LIST.get( 1 ) ) );
        assertTrue( partDescriptors.contains( PART_DESCRIPTORS_LIST.get( 2 ) ) );
    }

    @Test
    public void fromCollection()
    {
        final PartDescriptors partDescriptors = PartDescriptors.from( PART_DESCRIPTORS_LIST );

        assertEquals( 3, partDescriptors.getSize() );
        assertTrue( partDescriptors.contains( PART_DESCRIPTORS_LIST.get( 0 ) ) );
        assertTrue( partDescriptors.contains( PART_DESCRIPTORS_LIST.get( 1 ) ) );
        assertTrue( partDescriptors.contains( PART_DESCRIPTORS_LIST.get( 2 ) ) );
    }

    @Test
    public void fromIterable()
    {
        final PartDescriptors partDescriptors = PartDescriptors.from( (Iterable) PART_DESCRIPTORS_LIST );

        assertEquals( 3, partDescriptors.getSize() );
        assertTrue( partDescriptors.contains( PART_DESCRIPTORS_LIST.get( 0 ) ) );
        assertTrue( partDescriptors.contains( PART_DESCRIPTORS_LIST.get( 1 ) ) );
        assertTrue( partDescriptors.contains( PART_DESCRIPTORS_LIST.get( 2 ) ) );
    }
}
