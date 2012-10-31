package com.enonic.wem.api.content.type.formitem.inputtype;


import org.junit.Test;

import com.enonic.wem.api.content.datatype.DataTypes;
import com.enonic.wem.api.content.type.formitem.BreaksRequiredContractException;

import static com.enonic.wem.api.content.data.Data.newData;

public class XmlTest
{
    @Test(expected = BreaksRequiredContractException.class)
    public void checkBreaksRequiredContract_throws_exception_when_value_is_null()
    {
        new Xml().checkBreaksRequiredContract( newData().type( DataTypes.XML ).value( null ).build() );
    }

    @Test(expected = BreaksRequiredContractException.class)
    public void checkBreaksRequiredContract_throws_exception_when_value_is_empty_string()
    {
        new Xml().checkBreaksRequiredContract( newData().type( DataTypes.XML ).value( "" ).build() );
    }

    @Test(expected = BreaksRequiredContractException.class)
    public void checkBreaksRequiredContract_throws_exception_when_value_is_blank_string()
    {
        new Xml().checkBreaksRequiredContract( newData().type( DataTypes.XML ).value( "  " ).build() );
    }
}
