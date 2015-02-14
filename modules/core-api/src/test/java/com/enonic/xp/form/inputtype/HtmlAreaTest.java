package com.enonic.xp.form.inputtype;


import org.junit.Test;

import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.form.BreaksRequiredContractException;
import com.enonic.xp.form.inputtype.HtmlArea;

public class HtmlAreaTest
{
    @Test(expected = BreaksRequiredContractException.class)
    public void breaksRequiredContract_textLine_which_is_empty_throws_exception()
    {
        new HtmlArea().checkBreaksRequiredContract(
            new PropertyTree( new PropertyTree.PredictivePropertyIdProvider() ).setHtmlPart( "myHtml", "" ) );
    }

    @Test(expected = BreaksRequiredContractException.class)
    public void breaksRequiredContract_textLine_which_is_blank_throws_exception()
    {
        new HtmlArea().checkBreaksRequiredContract(
            new PropertyTree( new PropertyTree.PredictivePropertyIdProvider() ).setHtmlPart( "myHtml", " " ) );
    }
}
