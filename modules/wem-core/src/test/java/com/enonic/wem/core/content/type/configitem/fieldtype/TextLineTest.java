package com.enonic.wem.core.content.type.configitem.fieldtype;


import org.junit.Test;

import com.enonic.wem.core.content.data.Data;
import com.enonic.wem.core.content.type.configitem.BreaksRequiredContractException;
import com.enonic.wem.core.content.type.datatype.DataTypes;

import static org.junit.Assert.*;

public class TextLineTest
{
    @Test(expected = BreaksRequiredContractException.class)
    public void breaksRequiredContract_textLine_which_is_null_throws_exception()
    {
        new TextLine().checkBreaksRequiredContract( Data.newData().value( null ).type( DataTypes.STRING ).build() );
    }

    @Test(expected = BreaksRequiredContractException.class)
    public void breaksRequiredContract_textLine_which_is_empty_throws_exception()
    {
        new TextLine().checkBreaksRequiredContract( Data.newData().type( DataTypes.STRING ).value( "" ).build() );
    }

    @Test(expected = BreaksRequiredContractException.class)
    public void breaksRequiredContract_textLine_which_is_blank_throws_exception()
    {
        new TextLine().checkBreaksRequiredContract( Data.newData().type( DataTypes.STRING ).value( " " ).build() );
    }

    @Test
    public void breaksRequiredContract_textLine_which_is_something_throws_not_exception()
    {
        try
        {
            new TextLine().checkBreaksRequiredContract( Data.newData().type( DataTypes.STRING ).value( "something" ).build() );
        }
        catch ( Exception e )
        {
            fail( "Exception NOT expected" );
        }
    }
}
