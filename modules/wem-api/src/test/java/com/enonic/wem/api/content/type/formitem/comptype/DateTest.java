package com.enonic.wem.api.content.type.formitem.comptype;

import org.junit.Test;

import com.enonic.wem.api.content.datatype.DataTypes;
import com.enonic.wem.api.content.type.formitem.BreaksRequiredContractException;

import static com.enonic.wem.api.content.data.Data.newData;


public class DateTest
{
    @Test(expected = BreaksRequiredContractException.class)
    public void checkBreaksRequiredContract_throws_exception_when_value_is_null()
    {
        new Date().checkBreaksRequiredContract( newData().value( null ).type( DataTypes.DATE ).build() );
    }
}
