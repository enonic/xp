package com.enonic.xp.schema.formfragment;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

public class FormFragmentNamesTest
{
    @Test
    public void test_immutable_MixinNames()
    {
        List<FormFragmentName> names = new ArrayList<>();
        FormFragmentName mixinName = FormFragmentName.from( "myapplication:my" );
        FormFragmentNames mixinNames = FormFragmentNames.from( names );
        try
        {
            mixinNames.getSet().add( FormFragmentName.from( "myapplication:my1" ) );
        }
        catch ( Exception e )
        {
            assertInstanceOf( UnsupportedOperationException.class, e );
        }
        mixinNames = FormFragmentNames.from( FormFragmentName.from( "myapplication:my1" ) );
        try
        {
            mixinNames.getSet().add( mixinName );
        }
        catch ( Exception e )
        {
            assertInstanceOf( UnsupportedOperationException.class, e );
        }
        mixinNames = FormFragmentNames.from( "myapplication:my1" );
        try
        {
            mixinNames.getSet().add( mixinName );
        }
        catch ( Exception e )
        {
            assertInstanceOf( UnsupportedOperationException.class, e );
        }
    }

    @Test
    public void from()
    {
        FormFragmentNames
            mixinNames = FormFragmentNames.from( FormFragmentName.from( "myapplication:my1" ), FormFragmentName.from( "myapplication:my2" ),
                                                 FormFragmentName.from( "myapplication:my3" ) );

        List<FormFragmentName> mixinNameList =
            List.of( FormFragmentName.from( "myapplication:my1" ), FormFragmentName.from( "myapplication:my2" ), FormFragmentName.from( "myapplication:my3" ) );

        assertEquals( 3, mixinNames.getSize() );
        assertEquals( 3, FormFragmentNames.from( mixinNames ).getSize() );
        assertEquals( 3, FormFragmentNames.from( mixinNameList ).getSize() );
    }
}
