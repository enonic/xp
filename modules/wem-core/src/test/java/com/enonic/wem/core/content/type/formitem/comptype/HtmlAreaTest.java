package com.enonic.wem.core.content.type.formitem.comptype;


import org.junit.Test;

import com.enonic.wem.core.content.data.Data;
import com.enonic.wem.core.content.datatype.DataTypes;
import com.enonic.wem.core.content.type.formitem.BreaksRequiredContractException;

public class HtmlAreaTest
{
    @Test(expected = BreaksRequiredContractException.class)
    public void breaksRequiredContract_textLine_which_is_null_throws_exception()
    {
        new HtmlArea().checkBreaksRequiredContract( Data.newData().type( DataTypes.HTML_PART ).value( null ).build() );
    }

    @Test(expected = BreaksRequiredContractException.class)
    public void breaksRequiredContract_textLine_which_is_empty_throws_exception()
    {
        new HtmlArea().checkBreaksRequiredContract( Data.newData().type( DataTypes.HTML_PART ).value( "" ).build() );
    }

    @Test(expected = BreaksRequiredContractException.class)
    public void breaksRequiredContract_textLine_which_is_blank_throws_exception()
    {
        new HtmlArea().checkBreaksRequiredContract( Data.newData().type( DataTypes.HTML_PART ).value( " " ).build() );
    }
}
