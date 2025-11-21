package com.enonic.xp.schema.formfragment;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

public class FormFragmentNamesTest
{
    @Test
    public void test_immutable_Names()
    {
        List<FormFragmentName> names = new ArrayList<>();
        FormFragmentName formFragmentName = FormFragmentName.from( "myapplication:my" );
        FormFragmentNames fragmentNames = FormFragmentNames.from( names );
        try
        {
            fragmentNames.getSet().add( FormFragmentName.from( "myapplication:my1" ) );
        }
        catch ( Exception e )
        {
            assertInstanceOf( UnsupportedOperationException.class, e );
        }
        fragmentNames = FormFragmentNames.from( FormFragmentName.from( "myapplication:my1" ) );
        try
        {
            fragmentNames.getSet().add( formFragmentName );
        }
        catch ( Exception e )
        {
            assertInstanceOf( UnsupportedOperationException.class, e );
        }
        fragmentNames = FormFragmentNames.from( "myapplication:my1" );
        try
        {
            fragmentNames.getSet().add( formFragmentName );
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
            formFragmentNames = FormFragmentNames.from( FormFragmentName.from( "myapplication:my1" ), FormFragmentName.from( "myapplication:my2" ),
                                                 FormFragmentName.from( "myapplication:my3" ) );

        List<FormFragmentName> fragmentNameList =
            List.of( FormFragmentName.from( "myapplication:my1" ), FormFragmentName.from( "myapplication:my2" ), FormFragmentName.from( "myapplication:my3" ) );

        assertEquals( 3, formFragmentNames.getSize() );
        assertEquals( 3, FormFragmentNames.from( formFragmentNames ).getSize() );
        assertEquals( 3, FormFragmentNames.from( fragmentNameList ).getSize() );
    }
}
