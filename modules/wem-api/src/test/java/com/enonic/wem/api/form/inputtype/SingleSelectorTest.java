package com.enonic.wem.api.form.inputtype;

import org.junit.Test;

import com.enonic.wem.api.data.Property;
import com.enonic.wem.api.form.BreaksRequiredContractException;

public class SingleSelectorTest
{
    @Test(expected = BreaksRequiredContractException.class)
    public void breaksRequiredContract_textLine_which_is_empty_throws_exception()
    {
        new SingleSelector().checkBreaksRequiredContract( new Property.String( "myText", "" ) );
    }

    @Test(expected = BreaksRequiredContractException.class)
    public void breaksRequiredContract_textLine_which_is_blank_throws_exception()
    {
        new SingleSelector().checkBreaksRequiredContract( new Property.String( "myText", " " ) );
    }
}
