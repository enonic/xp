package com.enonic.xp.form.inputtype;

import org.junit.Test;

import com.enonic.xp.form.InputValidationException;

public class HtmlAreaTypeTest
    extends BaseInputTypeTest
{
    public HtmlAreaTypeTest()
    {
        super( HtmlAreaType.INSTANCE );
    }

    @Test
    public void testContract()
    {
        this.type.checkBreaksRequiredContract( stringProperty( "myHtml" ) );
    }

    @Test(expected = InputValidationException.class)
    public void testContract_breaks()
    {
        this.type.checkBreaksRequiredContract( stringProperty( "" ) );
    }
}
