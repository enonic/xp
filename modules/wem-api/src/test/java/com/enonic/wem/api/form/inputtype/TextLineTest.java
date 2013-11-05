package com.enonic.wem.api.form.inputtype;


import org.junit.Test;

import com.enonic.wem.api.data.Property;
import com.enonic.wem.api.form.BreaksRequiredContractException;

import static org.junit.Assert.*;

public class TextLineTest
{

    @Test(expected = BreaksRequiredContractException.class)
    public void breaksRequiredContract_textLine_which_is_empty_throws_exception()
    {
        new TextLine().checkBreaksRequiredContract( new Property.String( "myText", "" ) );
    }

    @Test(expected = BreaksRequiredContractException.class)
    public void breaksRequiredContract_textLine_which_is_blank_throws_exception()
    {
        new TextLine().checkBreaksRequiredContract( new Property.String( "myText", " " ) );
    }

    @Test
    public void breaksRequiredContract_textLine_which_is_something_throws_not_exception()
    {
        try
        {
            new TextLine().checkBreaksRequiredContract( new Property.String( "myText", "something" ) );
        }
        catch ( Exception e )
        {
            fail( "Exception NOT expected" );
        }
    }
}
