package com.enonic.wem.api.content.schema.content.form.inputtype;


import org.junit.Test;

import com.enonic.wem.api.content.datatype.DataTypes;
import com.enonic.wem.api.content.schema.content.form.BreaksRequiredContractException;

import static com.enonic.wem.api.content.data.Data.newData;

public class XmlTest
{
    @Test(expected = BreaksRequiredContractException.class)
    public void checkBreaksRequiredContract_throws_exception_when_value_is_empty_string()
    {
        new Xml().checkBreaksRequiredContract( newData().name( "myXml" ).type( DataTypes.XML ).value( "" ).build() );
    }

    @Test(expected = BreaksRequiredContractException.class)
    public void checkBreaksRequiredContract_throws_exception_when_value_is_blank_string()
    {
        new Xml().checkBreaksRequiredContract( newData().name( "myXml" ).type( DataTypes.XML ).value( "  " ).build() );
    }
}
