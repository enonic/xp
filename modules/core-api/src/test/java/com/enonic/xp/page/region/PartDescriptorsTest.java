package com.enonic.xp.page.region;

import java.util.ArrayList;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import com.enonic.xp.form.Form;
import com.enonic.xp.page.DescriptorKey;

import static org.junit.Assert.*;

public class PartDescriptorsTest
{

    private static final List<PartDescriptor> partDescriptorsList = new ArrayList<PartDescriptor>();

    @BeforeClass
    public static void initRegionDescriptors()
    {
        final PartDescriptor partDescriptor1 = PartDescriptor.create().
            name( "news-part" ).
            displayName( "News part" ).
            config( Form.newForm().build() ).
            key( DescriptorKey.from( "module:new-part" ) ).
            build();

        final PartDescriptor partDescriptor2 = PartDescriptor.create().
            name( "news-part2" ).
            displayName( "News part2" ).
            config( Form.newForm().build() ).
            key( DescriptorKey.from( "module:new-part2" ) ).
            build();

        final PartDescriptor partDescriptor3 = PartDescriptor.create().
            name( "news-part3" ).
            displayName( "News part3" ).
            config( Form.newForm().build() ).
            key( DescriptorKey.from( "module:new-part3" ) ).
            build();

        partDescriptorsList.add( partDescriptor1 );
        partDescriptorsList.add( partDescriptor2 );
        partDescriptorsList.add( partDescriptor3 );
    }

    @Test
    public void testBuilder()
    {
        final PartDescriptors partDescriptors = PartDescriptors.newPartDescriptors().
            add( partDescriptorsList.get( 0 ) ).
            add( partDescriptorsList.get( 1 ) ).
            build();

        assertEquals( 2, partDescriptors.getSize() );
        assertNotNull( partDescriptors.getDescriptor( partDescriptorsList.get( 0 ).getKey() ) );
        assertNotNull( partDescriptors.getDescriptor( "news-part2" ) );
        assertNull( partDescriptors.getDescriptor( DescriptorKey.from( "module:new-par" ) ) );
    }

    @Test
    public void fromArray()
    {
        final PartDescriptors partDescriptors =
            PartDescriptors.from( partDescriptorsList.get( 0 ), partDescriptorsList.get( 1 ), partDescriptorsList.get( 2 ) );

        assertEquals( 3, partDescriptors.getSize() );
        assertTrue( partDescriptors.contains( partDescriptorsList.get( 0 ) ) );
        assertTrue( partDescriptors.contains( partDescriptorsList.get( 1 ) ) );
        assertTrue( partDescriptors.contains( partDescriptorsList.get( 2 ) ) );
    }

    @Test
    public void fromCollection()
    {
        final PartDescriptors partDescriptors = PartDescriptors.from( partDescriptorsList );

        assertEquals( 3, partDescriptors.getSize() );
        assertTrue( partDescriptors.contains( partDescriptorsList.get( 0 ) ) );
        assertTrue( partDescriptors.contains( partDescriptorsList.get( 1 ) ) );
        assertTrue( partDescriptors.contains( partDescriptorsList.get( 2 ) ) );
    }

    @Test
    public void fromIterable()
    {
        final PartDescriptors partDescriptors = PartDescriptors.from( (Iterable) partDescriptorsList );

        assertEquals( 3, partDescriptors.getSize() );
        assertTrue( partDescriptors.contains( partDescriptorsList.get( 0 ) ) );
        assertTrue( partDescriptors.contains( partDescriptorsList.get( 1 ) ) );
        assertTrue( partDescriptors.contains( partDescriptorsList.get( 2 ) ) );
    }
}
