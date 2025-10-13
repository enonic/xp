package com.enonic.xp.schema.mixin;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MixinNamesTest
{
    @Test
    public void test_immutable_MixinNames()
    {
        List<FormFragmentName> names = new ArrayList<>();
        FormFragmentName mixinName = FormFragmentName.from( "myapplication:my" );
        MixinNames mixinNames = MixinNames.from( names );
        try
        {
            mixinNames.getSet().add( FormFragmentName.from( "myapplication:my1" ) );
        }
        catch ( Exception e )
        {
            assertTrue( e instanceof UnsupportedOperationException );
        }
        mixinNames = MixinNames.from( FormFragmentName.from( "myapplication:my1" ) );
        try
        {
            mixinNames.getSet().add( mixinName );
        }
        catch ( Exception e )
        {
            assertTrue( e instanceof UnsupportedOperationException );
        }
        mixinNames = MixinNames.from( "myapplication:my1" );
        try
        {
            mixinNames.getSet().add( mixinName );
        }
        catch ( Exception e )
        {
            assertTrue( e instanceof UnsupportedOperationException );
        }
    }

    @Test
    public void from()
    {
        MixinNames mixinNames = MixinNames.from( FormFragmentName.from( "myapplication:my1" ), FormFragmentName.from( "myapplication:my2" ),
                                                 FormFragmentName.from( "myapplication:my3" ) );

        List<FormFragmentName> mixinNameList =
            List.of( FormFragmentName.from( "myapplication:my1" ), FormFragmentName.from( "myapplication:my2" ), FormFragmentName.from( "myapplication:my3" ) );

        assertEquals( 3, mixinNames.getSize() );
        assertEquals( 3, MixinNames.from( mixinNames ).getSize() );
        assertEquals( 3, MixinNames.from( mixinNameList ).getSize() );
    }
}
