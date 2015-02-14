package com.enonic.xp.core.form.inputtype;

import org.junit.Test;

import com.enonic.xp.core.data.PropertyTree;
import com.enonic.xp.core.form.BreaksRequiredContractException;

public class SingleSelectorTest
{
    @Test(expected = BreaksRequiredContractException.class)
    public void breaksRequiredContract_textLine_which_is_empty_throws_exception()
    {
        new SingleSelector().checkBreaksRequiredContract( new PropertyTree().setString( "myText", "" ) );
    }

    @Test(expected = BreaksRequiredContractException.class)
    public void breaksRequiredContract_textLine_which_is_blank_throws_exception()
    {
        new SingleSelector().checkBreaksRequiredContract( new PropertyTree().setString( "myText", " " ) );
    }
}
