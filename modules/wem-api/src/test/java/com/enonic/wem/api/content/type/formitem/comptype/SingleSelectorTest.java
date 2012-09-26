package com.enonic.wem.api.content.type.formitem.comptype;

import org.junit.Test;

import com.enonic.wem.api.content.data.Data;
import com.enonic.wem.api.content.type.formitem.BreaksRequiredContractException;

public class SingleSelectorTest
{
    private SingleSelector singleSelector = new SingleSelector();

    @Test(expected = BreaksRequiredContractException.class)
    public void breaksRequiredContract_textLine_which_is_null_throws_exception()
    {
        singleSelector.checkBreaksRequiredContract( Data.newData().type( singleSelector.getDataType() ).value( null ).build() );
    }

    @Test(expected = BreaksRequiredContractException.class)
    public void breaksRequiredContract_textLine_which_is_empty_throws_exception()
    {
        new SingleSelector().checkBreaksRequiredContract( Data.newData().type( singleSelector.getDataType() ).value( "" ).build() );
    }

    @Test(expected = BreaksRequiredContractException.class)
    public void breaksRequiredContract_textLine_which_is_blank_throws_exception()
    {
        new SingleSelector().checkBreaksRequiredContract( Data.newData().type( singleSelector.getDataType() ).value( " " ).build() );
    }
}
