package com.enonic.xp.schema.formfragment;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.enonic.xp.schema.BaseSchema;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FormFragmentDescriptorsTest
{
    @Test
    public void test_immutable_fragments()
    {
        FormFragmentName fragmentName = FormFragmentName.from( "myapplication:my1" );
        FormFragmentDescriptor fragmentDescriptor = FormFragmentDescriptor.create().name( fragmentName ).build();
        final FormFragmentDescriptors formFragmentDescriptors = FormFragmentDescriptors.from( fragmentDescriptor );

        assertEquals( 1, formFragmentDescriptors.stream().map( BaseSchema::getName ).collect( FormFragmentNames.collector() ).getSize() );
        assertNotNull( formFragmentDescriptors.stream().filter( m -> fragmentName.equals( m.getName() ) ).findFirst().orElse( null ) );
        assertThrows( UnsupportedOperationException.class, () -> formFragmentDescriptors.getList().add( null ) );

        final FormFragmentDescriptors formFragmentDescriptors2 =
            FormFragmentDescriptors.from( Collections.singleton( fragmentDescriptor ) );
        assertThrows( UnsupportedOperationException.class, () -> formFragmentDescriptors2.getList().add( null ) );

        final FormFragmentDescriptors formFragmentDescriptors3 = FormFragmentDescriptors.create().add( fragmentDescriptor ).build();
        assertEquals( 1, formFragmentDescriptors3.stream().map( BaseSchema::getName ).collect( FormFragmentNames.collector() ).getSize() );
        assertTrue( FormFragmentDescriptors.empty().isEmpty() );
    }

    @Test
    public void add_multiple()
    {
        FormFragmentDescriptor fragment1 = FormFragmentDescriptor.create().name( FormFragmentName.from( "myapplication:my1" ) ).build();
        FormFragmentDescriptor fragment2 = FormFragmentDescriptor.create().name( FormFragmentName.from( "myapplication:my2" ) ).build();

        FormFragmentDescriptors fragments1 = FormFragmentDescriptors.create().addAll( Arrays.asList( fragment1, fragment2 ) ).build();
        FormFragmentDescriptors fragments2 =
            FormFragmentDescriptors.create().addAll( FormFragmentDescriptors.create().add( fragment1 ).add( fragment2 ).build() ).build();

        assertEquals( 2, fragments1.getSize() );
        assertEquals( 2, fragments2.getSize() );
    }

    @Test
    public void from()
    {
        FormFragmentDescriptors fragments1 =
            FormFragmentDescriptors.from( FormFragmentDescriptor.create().name( FormFragmentName.from( "myapplication:my1" ) ).build(),
                                          FormFragmentDescriptor.create().name( FormFragmentName.from( "myapplication:my2" ) ).build() );

        List<FormFragmentDescriptor> fragments2 =
            List.of( FormFragmentDescriptor.create().name( FormFragmentName.from( "myapplication:my1" ) ).build(),
                     FormFragmentDescriptor.create().name( FormFragmentName.from( "myapplication:my2" ) ).build() );

        assertEquals( 2, fragments1.getSize() );
        assertEquals( 2, FormFragmentDescriptors.from( fragments1 ).getSize() );
        assertEquals( 2, FormFragmentDescriptors.from( fragments2 ).getSize() );
    }
}
