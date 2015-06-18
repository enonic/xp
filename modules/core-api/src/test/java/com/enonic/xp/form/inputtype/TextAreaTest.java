package com.enonic.xp.form.inputtype;


import org.junit.Test;

import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.form.BreaksRequiredContractException;

public class TextAreaTest
{
    @Test(expected = BreaksRequiredContractException.class)
    public void breaksRequiredContract_textLine_which_is_empty_throws_exception()
    {
        new TextArea().checkBreaksRequiredContract( new PropertyTree().setString( "myText", "" ) );
    }

    @Test(expected = BreaksRequiredContractException.class)
    public void breaksRequiredContract_textLine_which_is_blank_throws_exception()
    {
        new TextArea().checkBreaksRequiredContract( new PropertyTree().setString( "myText", " " ) );
    }
}
