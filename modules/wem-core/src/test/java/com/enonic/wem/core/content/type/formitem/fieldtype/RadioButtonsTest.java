package com.enonic.wem.core.content.type.formitem.fieldtype;

import org.junit.Test;

import com.enonic.wem.core.content.data.Data;
import com.enonic.wem.core.content.type.formitem.BreaksRequiredContractException;

public class RadioButtonsTest
{
    private RadioButtons radioButtons = new RadioButtons();

    @Test(expected = BreaksRequiredContractException.class)
    public void breaksRequiredContract_textLine_which_is_null_throws_exception()
    {
        radioButtons.checkBreaksRequiredContract( Data.newData().type( radioButtons.getDataType() ).value( null ).build() );
    }

    @Test(expected = BreaksRequiredContractException.class)
    public void breaksRequiredContract_textLine_which_is_empty_throws_exception()
    {
        new RadioButtons().checkBreaksRequiredContract( Data.newData().type( radioButtons.getDataType() ).value( "" ).build() );
    }

    @Test(expected = BreaksRequiredContractException.class)
    public void breaksRequiredContract_textLine_which_is_blank_throws_exception()
    {
        new RadioButtons().checkBreaksRequiredContract( Data.newData().type( radioButtons.getDataType() ).value( " " ).build() );
    }
}
