package com.enonic.wem.core.content.type.formitem.comptype;

import org.junit.Test;

import com.enonic.wem.core.content.datatype.DataTypes;
import com.enonic.wem.core.content.type.formitem.BreaksRequiredContractException;

import static com.enonic.wem.core.content.data.Data.newData;


public class DateTest
{
    @Test(expected = BreaksRequiredContractException.class)
    public void checkBreaksRequiredContract_throws_exception_when_value_is_null()
    {
        new Date().checkBreaksRequiredContract( newData().value( null ).type( DataTypes.DATE ).build() );
    }
}
