package com.enonic.wem.thymeleaf.internal;

import org.junit.Test;
import org.mockito.Mockito;

import junit.framework.Assert;

import com.enonic.wem.thymeleaf.ThymeleafProcessor;

public class ThymeleafScriptHelperTest
{
    @Test
    public void testMethods()
    {
        final ThymeleafProcessor processor = Mockito.mock( ThymeleafProcessor.class );
        final ThymeleafScriptHelper helper = new ThymeleafScriptHelper( processor );

        Assert.assertSame( processor, helper.getProcessor() );
        Assert.assertNotNull( helper.newRenderParams() );
    }
}
