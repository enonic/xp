package com.enonic.wem.api.form.inputtype;


import org.junit.Test;

import com.enonic.wem.api.data.PropertyTree;
import com.enonic.wem.api.form.BreaksRequiredContractException;

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
