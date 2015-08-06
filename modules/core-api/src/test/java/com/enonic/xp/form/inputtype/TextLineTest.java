package com.enonic.xp.form.inputtype;


import org.junit.Test;

import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.form.InputValidationException;

import static org.junit.Assert.*;

public class TextLineTest
{

    @Test(expected = InputValidationException.class)
    public void breaksRequiredContract_textLine_which_is_empty_throws_exception()
    {
        new TextLine().checkBreaksRequiredContract(
            new PropertyTree( new PropertyTree.PredictivePropertyIdProvider() ).setString( "myText", "" ) );
    }

    @Test(expected = InputValidationException.class)
    public void breaksRequiredContract_textLine_which_is_blank_throws_exception()
    {
        new TextLine().checkBreaksRequiredContract( new PropertyTree().setString( "myText", " " ) );
    }

    @Test
    public void breaksRequiredContract_textLine_which_is_something_throws_not_exception()
    {
        try
        {
            new TextLine().checkBreaksRequiredContract( new PropertyTree().setString( "myText", "something" ) );
        }
        catch ( Exception e )
        {
            fail( "Exception NOT expected" );
        }
    }
}
