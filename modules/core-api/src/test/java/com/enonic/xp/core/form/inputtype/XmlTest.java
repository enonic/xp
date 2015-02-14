package com.enonic.xp.core.form.inputtype;


import org.junit.Test;

import com.enonic.xp.core.data.PropertyTree;
import com.enonic.xp.core.form.BreaksRequiredContractException;

public class XmlTest
{
    @Test(expected = BreaksRequiredContractException.class)
    public void checkBreaksRequiredContract_throws_exception_when_value_is_empty_string()
    {
        new Xml().checkBreaksRequiredContract( new PropertyTree().setXml( "myXml", "" ) );
    }

    @Test(expected = BreaksRequiredContractException.class)
    public void checkBreaksRequiredContract_throws_exception_when_value_is_blank_string()
    {
        new Xml().checkBreaksRequiredContract( new PropertyTree().setXml( "myXml", "  " ) );
    }
}
