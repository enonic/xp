package com.enonic.xp.schema.mixin;

import java.util.List;

import org.junit.Test;

import com.google.common.collect.Lists;

import static org.junit.Assert.*;

public class MixinNamesTest
{
    @Test
    public void test_immutable_MixinNames()
    {
        List<MixinName> names = Lists.newArrayList();
        MixinName mixinName = MixinName.from( "myapplication:my" );
        MixinNames mixinNames = MixinNames.from( names );
        try
        {
            mixinNames.getList().add( MixinName.from( "myapplication:my1" ) );
        }
        catch ( Exception e )
        {
            assertTrue( e instanceof UnsupportedOperationException );
        }
        mixinNames = MixinNames.from( MixinName.from( "myapplication:my1" ) );
        try
        {
            mixinNames.getList().add( mixinName );
        }
        catch ( Exception e )
        {
            assertTrue( e instanceof UnsupportedOperationException );
        }
        mixinNames = MixinNames.from( "myapplication:my1" );
        try
        {
            mixinNames.getList().add( mixinName );
        }
        catch ( Exception e )
        {
            assertTrue( e instanceof UnsupportedOperationException );
        }
    }

    @Test
    public void from()
    {
        MixinNames mixinNames = MixinNames.from( MixinName.from( "myapplication:my1" ), MixinName.from( "myapplication:my2" ),
                                                 MixinName.from( "myapplication:my3" ) );

        List<MixinName> mixinNameList = Lists.newArrayList( MixinName.from( "myapplication:my1" ), MixinName.from( "myapplication:my2" ),
                                                            MixinName.from( "myapplication:my3" ) );

        assertEquals( 3, mixinNames.getSize() );
        assertEquals( 3, MixinNames.from( mixinNames ).getSize() );
        assertEquals( 3, MixinNames.from( mixinNameList ).getSize() );
    }
}
