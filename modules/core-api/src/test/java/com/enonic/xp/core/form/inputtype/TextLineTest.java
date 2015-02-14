package com.enonic.xp.core.form.inputtype;


import org.junit.Test;

import com.enonic.xp.core.data.PropertyTree;
import com.enonic.xp.core.form.BreaksRequiredContractException;

import static org.junit.Assert.*;

public class TextLineTest
{

    @Test(expected = BreaksRequiredContractException.class)
    public void breaksRequiredContract_textLine_which_is_empty_throws_exception()
    {
        new TextLine().checkBreaksRequiredContract(
            new PropertyTree( new PropertyTree.PredictivePropertyIdProvider() ).setString( "myText", "" ) );
    }

    @Test(expected = BreaksRequiredContractException.class)
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
