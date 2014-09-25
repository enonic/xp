package com.enonic.wem.thymeleaf.internal;

import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Maps;

import junit.framework.Assert;

import com.enonic.wem.api.resource.ResourceKey;
import com.enonic.wem.api.resource.ResourceProblemException;
import com.enonic.wem.api.resource.ResourceUrlRegistry;
import com.enonic.wem.api.resource.ResourceUrlTestHelper;
import com.enonic.wem.thymeleaf.ThymeleafProcessor;
import com.enonic.wem.thymeleaf.ThymeleafProcessorFactory;

public class ThymeleafProcessorImplTest
{
    private ThymeleafProcessorFactory processorFactory;

    @Before
    public void setup()
    {
        final ResourceUrlRegistry urlRegistry = ResourceUrlTestHelper.mockModuleScheme();
        urlRegistry.modulesClassLoader( getClass().getClassLoader() );
        this.processorFactory = new ThymeleafProcessorFactoryImpl();
    }

    @Test(expected = RuntimeException.class)
    public void testResourceNotFound()
    {
        final ThymeleafProcessor processor = this.processorFactory.newProcessor().
            view( ResourceKey.from( "mymodule:/view/unknown.html" ) );

        processor.process();
    }

    @Test
    public void testProcessResource()
    {
        final Map<String, Object> params = Maps.newHashMap();

        final ThymeleafProcessor processor = this.processorFactory.newProcessor().
            view( ResourceKey.from( "mymodule:/view/test.html" ) ).
            parameters( params );

        final String result = processor.process();
        Assert.assertEquals( "<div>\n    <div><!--# COMPONENT test --></div>\n</div>", result );
    }

    @Test(expected = ResourceProblemException.class)
    public void testViewError()
    {
        final ThymeleafProcessor processor = this.processorFactory.newProcessor().
            view( ResourceKey.from( "mymodule:/view/test-error.html" ) );

        processor.process();
    }
}
