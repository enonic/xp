package com.enonic.wem.api.content.schema.content.form.inputtype;


import org.junit.Test;

import com.enonic.wem.api.content.data.Data;
import com.enonic.wem.api.content.data.datatype.DataTypes;
import com.enonic.wem.api.content.schema.content.form.BreaksRequiredContractException;

import static org.junit.Assert.*;

public class TextLineTest
{

    @Test(expected = BreaksRequiredContractException.class)
    public void breaksRequiredContract_textLine_which_is_empty_throws_exception()
    {
        new TextLine().checkBreaksRequiredContract( Data.newData().name( "myText" ).type( DataTypes.TEXT ).value( "" ).build() );
    }

    @Test(expected = BreaksRequiredContractException.class)
    public void breaksRequiredContract_textLine_which_is_blank_throws_exception()
    {
        new TextLine().checkBreaksRequiredContract( Data.newData().name( "myText" ).type( DataTypes.TEXT ).value( " " ).build() );
    }

    @Test
    public void breaksRequiredContract_textLine_which_is_something_throws_not_exception()
    {
        try
        {
            new TextLine().checkBreaksRequiredContract(
                Data.newData().name( "myText" ).type( DataTypes.TEXT ).value( "something" ).build() );
        }
        catch ( Exception e )
        {
            fail( "Exception NOT expected" );
        }
    }
}
