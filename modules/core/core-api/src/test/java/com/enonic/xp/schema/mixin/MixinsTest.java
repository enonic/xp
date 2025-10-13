package com.enonic.xp.schema.mixin;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MixinsTest
{
    @Test
    public void test_immutable_mixins()
    {
        FormFragmentName mixinName = FormFragmentName.from( "myapplication:my1" );
        FormFragmentDescriptor mixin = FormFragmentDescriptor.create().name( mixinName ).build();
        Mixins mixins = Mixins.from( mixin );

        assertTrue( mixins.getNames().getSize() == 1 );
        assertNotNull( mixins.getMixin( mixinName ) );

        try
        {
            mixins.getList().add( null );
        }
        catch ( Exception e )
        {
            assertTrue( e instanceof UnsupportedOperationException );
        }
        mixins = Mixins.from( Collections.singleton( mixin ) );
        try
        {
            mixins.getList().add( null );
        }
        catch ( Exception e )
        {
            assertTrue( e instanceof UnsupportedOperationException );
        }

        mixins = Mixins.create().add( mixin ).build();
        assertEquals( 1, mixins.getNames().getSize() );
        assertTrue( Mixins.empty().isEmpty() );
    }

    @Test
    public void add_multiple()
    {
        FormFragmentDescriptor mixin1 = FormFragmentDescriptor.create().name( FormFragmentName.from( "myapplication:my1" ) ).build();
        FormFragmentDescriptor mixin2 = FormFragmentDescriptor.create().name( FormFragmentName.from( "myapplication:my2" ) ).build();

        Mixins mixinsFromList = Mixins.create().addAll( Arrays.asList( mixin1, mixin2 ) ).build();
        Mixins mixinsFromMixins = Mixins.create().addAll( Mixins.create().add( mixin1 ).add( mixin2 ).build() ).build();

        assertEquals( 2, mixinsFromList.getSize() );
        assertEquals( 2, mixinsFromMixins.getSize() );
    }

    @Test
    public void from()
    {
        Mixins mixins = Mixins.from( FormFragmentDescriptor.create().name( FormFragmentName.from( "myapplication:my1" ) ).build(),
                                     FormFragmentDescriptor.create().name( FormFragmentName.from( "myapplication:my2" ) ).build() );

        List<FormFragmentDescriptor> mixinList = List.of( FormFragmentDescriptor.create().name( FormFragmentName.from( "myapplication:my1" ) ).build(),
                                                          FormFragmentDescriptor.create().name( FormFragmentName.from( "myapplication:my2" ) ).build() );

        assertEquals( 2, mixins.getSize() );
        assertEquals( 2, Mixins.from( mixins ).getSize() );
        assertEquals( 2, Mixins.from( mixinList ).getSize() );
    }
}
