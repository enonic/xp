package com.enonic.xp.schema.formfragment;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FormFragmentDescriptorsTest
{
    @Test
    public void test_immutable_mixins()
    {
        FormFragmentName fragmentName = FormFragmentName.from( "myapplication:my1" );
        FormFragmentDescriptor fragmentDescriptor = FormFragmentDescriptor.create().name( fragmentName ).build();
        FormFragmentDescriptors formFragmentDescriptors = FormFragmentDescriptors.from( fragmentDescriptor );

        assertTrue( formFragmentDescriptors.getNames().getSize() == 1 );
        assertNotNull( formFragmentDescriptors.getFormFragmentDescriptor( fragmentName ) );

        try
        {
            formFragmentDescriptors.getList().add( null );
        }
        catch ( Exception e )
        {
            assertTrue( e instanceof UnsupportedOperationException );
        }
        formFragmentDescriptors = FormFragmentDescriptors.from( Collections.singleton( fragmentDescriptor ) );
        try
        {
            formFragmentDescriptors.getList().add( null );
        }
        catch ( Exception e )
        {
            assertTrue( e instanceof UnsupportedOperationException );
        }

        formFragmentDescriptors = FormFragmentDescriptors.create().add( fragmentDescriptor ).build();
        assertEquals( 1, formFragmentDescriptors.getNames().getSize() );
        assertTrue( FormFragmentDescriptors.empty().isEmpty() );
    }

    @Test
    public void add_multiple()
    {
        FormFragmentDescriptor mixin1 = FormFragmentDescriptor.create().name( FormFragmentName.from( "myapplication:my1" ) ).build();
        FormFragmentDescriptor mixin2 = FormFragmentDescriptor.create().name( FormFragmentName.from( "myapplication:my2" ) ).build();

        FormFragmentDescriptors mixinsFromList = FormFragmentDescriptors.create().addAll( Arrays.asList( mixin1, mixin2 ) ).build();
        FormFragmentDescriptors mixinsFromMixins = FormFragmentDescriptors.create().addAll( FormFragmentDescriptors.create().add( mixin1 ).add( mixin2 ).build() ).build();

        assertEquals( 2, mixinsFromList.getSize() );
        assertEquals( 2, mixinsFromMixins.getSize() );
    }

    @Test
    public void from()
    {
        FormFragmentDescriptors
            mixins = FormFragmentDescriptors.from( FormFragmentDescriptor.create().name( FormFragmentName.from( "myapplication:my1" ) ).build(),
                                                   FormFragmentDescriptor.create().name( FormFragmentName.from( "myapplication:my2" ) ).build() );

        List<FormFragmentDescriptor> mixinList = List.of( FormFragmentDescriptor.create().name( FormFragmentName.from( "myapplication:my1" ) ).build(),
                                                          FormFragmentDescriptor.create().name( FormFragmentName.from( "myapplication:my2" ) ).build() );

        assertEquals( 2, mixins.getSize() );
        assertEquals( 2, FormFragmentDescriptors.from( mixins ).getSize() );
        assertEquals( 2, FormFragmentDescriptors.from( mixinList ).getSize() );
    }
}
