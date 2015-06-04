package com.enonic.xp.schema.mixin;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.google.common.collect.Lists;

import static com.enonic.xp.schema.mixin.Mixin.newMixin;
import static org.junit.Assert.*;

public class MixinsTest
{
    @Test
    public void test_immutable_mixins()
    {
        MixinName mixinName = MixinName.from( "mymodule:my1" );
        Mixin mixin = newMixin().name( mixinName ).build();
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
        mixins = Mixins.from( new ArrayList<Mixin>()
        {{
                add( mixin );
            }} );
        try
        {
            mixins.getList().add( null );
        }
        catch ( Exception e )
        {
            assertTrue( e instanceof UnsupportedOperationException );
        }

        mixins = Mixins.newMixins().add( mixin ).build();
        assertTrue( mixins.getNames().size() == 1 );
        mixins = Mixins.empty();
        assertTrue( mixins.getNames().size() == 0 );
    }

    @Test
    public void add_array()
    {
        Mixins mixins = Mixins.empty();

        Mixin mixin1 = newMixin().name( MixinName.from( "mymodule:my1" ) ).build();
        Mixin mixin2 = newMixin().name( MixinName.from( "mymodule:my2" ) ).build();

        Mixins newMixins = mixins.add( mixin1, mixin2 );

        assertEquals( 0, mixins.getSize() );
        assertEquals( 2, newMixins.getSize() );
    }

    @Test
    public void add_iterable()
    {
        Mixins mixins = Mixins.empty();

        List<Mixin> mixinList = Lists.newArrayList( newMixin().name( MixinName.from( "mymodule:my1" ) ).build(),
                                                    newMixin().name( MixinName.from( "mymodule:my2" ) ).build() );

        Mixins newMixins = mixins.add( mixinList );

        assertEquals( 0, mixins.getSize() );
        assertEquals( 2, newMixins.getSize() );
    }

    @Test
    public void from()
    {
        Mixins mixins = Mixins.from( newMixin().name( MixinName.from( "mymodule:my1" ) ).build(),
                                     newMixin().name( MixinName.from( "mymodule:my2" ) ).build() );

        List<Mixin> mixinList = Lists.newArrayList( newMixin().name( MixinName.from( "mymodule:my1" ) ).build(),
                                                    newMixin().name( MixinName.from( "mymodule:my2" ) ).build() );

        assertEquals( 2, mixins.getSize() );
        assertEquals( 2, Mixins.from( mixins ).getSize() );
        assertEquals( 2, Mixins.from( mixinList ).getSize() );
    }
}
