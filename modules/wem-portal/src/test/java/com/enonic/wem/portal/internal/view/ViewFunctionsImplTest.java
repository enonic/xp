package com.enonic.wem.portal.internal.view;

import org.junit.Before;
import org.junit.Test;

import com.enonic.wem.portal.view.ViewFunctions;

public class ViewFunctionsImplTest
{
    private ViewFunctions functions;

    @Before
    public void setup()
    {
        this.functions = new ViewFunctionsImpl();
    }

    @Test
    public void testAssetUrl()
    {
        final String url = this.functions.assetUrl( "_path=/to/my.css", "a=b", "a=c" );

    }
}
