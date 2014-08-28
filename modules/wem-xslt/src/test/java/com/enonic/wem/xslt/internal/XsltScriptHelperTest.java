package com.enonic.wem.xslt.internal;

import org.junit.Test;
import org.mockito.Mockito;

import junit.framework.Assert;

import com.enonic.wem.xslt.XsltProcessor;

public class XsltScriptHelperTest
{
    @Test
    public void testMethods()
    {
        final XsltProcessor processor = Mockito.mock( XsltProcessor.class );
        final XsltScriptHelper helper = new XsltScriptHelper( processor );

        Assert.assertSame( processor, helper.getProcessor() );
        Assert.assertNotNull( helper.newRenderParams() );
    }
}
