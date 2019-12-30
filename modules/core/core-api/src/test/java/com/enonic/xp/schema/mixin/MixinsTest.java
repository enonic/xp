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
        MixinName mixinName = MixinName.from( "myapplication:my1" );
        Mixin mixin = Mixin.create().name( mixinName ).build();
        Mixins mixins = Mixins.from( mixin );

        assertTrue( mixins.getNames().size() == 1 );
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
        assertTrue( mixins.getNames().size() == 1 );
        mixins = Mixins.empty();
        assertTrue( mixins.getNames().size() == 0 );
    }

    @Test
    public void add_array()
    {
        Mixins mixins = Mixins.empty();

        Mixin mixin1 = Mixin.create().name( MixinName.from( "myapplication:my1" ) ).build();
        Mixin mixin2 = Mixin.create().name( MixinName.from( "myapplication:my2" ) ).build();

        Mixins newMixins = mixins.add( mixin1, mixin2 );

        assertEquals( 0, mixins.getSize() );
        assertEquals( 2, newMixins.getSize() );
    }

    @Test
    public void add_multiple()
    {
        Mixin mixin1 = Mixin.create().name( MixinName.from( "myapplication:my1" ) ).build();
        Mixin mixin2 = Mixin.create().name( MixinName.from( "myapplication:my2" ) ).build();

        Mixins mixinsFromList = Mixins.create().addAll( Arrays.asList( mixin1, mixin2 ) ).build();
        Mixins mixinsFromMixins = Mixins.create().addAll( Mixins.create().add( mixin1 ).add( mixin2 ).build() ).build();

        assertEquals( 2, mixinsFromList.getSize() );
        assertEquals( 2, mixinsFromMixins.getSize() );
    }

    @Test
    public void add_iterable()
    {
        Mixins mixins = Mixins.empty();

        List<Mixin> mixinList = List.of( Mixin.create().name( MixinName.from( "myapplication:my1" ) ).build(),
                                         Mixin.create().name( MixinName.from( "myapplication:my2" ) ).build() );

        Mixins newMixins = mixins.add( mixinList );

        assertEquals( 0, mixins.getSize() );
        assertEquals( 2, newMixins.getSize() );
    }

    @Test
    public void from()
    {
        Mixins mixins = Mixins.from( Mixin.create().name( MixinName.from( "myapplication:my1" ) ).build(),
                                     Mixin.create().name( MixinName.from( "myapplication:my2" ) ).build() );

        List<Mixin> mixinList = List.of( Mixin.create().name( MixinName.from( "myapplication:my1" ) ).build(),
                                         Mixin.create().name( MixinName.from( "myapplication:my2" ) ).build() );

        assertEquals( 2, mixins.getSize() );
        assertEquals( 2, Mixins.from( mixins ).getSize() );
        assertEquals( 2, Mixins.from( mixinList ).getSize() );
    }
}
