package com.enonic.wem.core.content.type.configitem.fieldtype;


import org.junit.Test;

import com.enonic.wem.core.content.data.Data;
import com.enonic.wem.core.content.type.configitem.BreaksRequiredContractException;
import com.enonic.wem.core.content.type.datatype.DataTypes;

public class PhoneTest
{
    @Test(expected = BreaksRequiredContractException.class)
    public void breaksRequiredContract_textLine_which_is_null_throws_exception()
    {
        new Phone().checkBreaksRequiredContract( Data.newData().type( DataTypes.STRING ).value( null ).build() );
    }

    @Test(expected = BreaksRequiredContractException.class)
    public void breaksRequiredContract_textLine_which_is_empty_throws_exception()
    {
        new Phone().checkBreaksRequiredContract( Data.newData().type( DataTypes.STRING ).value( "" ).build() );
    }

    @Test(expected = BreaksRequiredContractException.class)
    public void breaksRequiredContract_textLine_which_is_blank_throws_exception()
    {
        new Phone().checkBreaksRequiredContract( Data.newData().type( DataTypes.STRING ).value( " " ).build() );
    }
}
