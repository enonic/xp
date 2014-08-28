package com.enonic.wem.thymeleaf.internal;

import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Maps;

import junit.framework.Assert;

import com.enonic.wem.api.resource.ResourceKey;
import com.enonic.wem.api.resource.ResourceUrlRegistry;
import com.enonic.wem.api.resource.ResourceUrlTestHelper;

public class ThymeleafProcessorImplTest
{
    private ThymeleafProcessorImpl processor;

    @Before
    public void setup()
    {
        final ResourceUrlRegistry urlRegistry = ResourceUrlTestHelper.mockModuleScheme();
        urlRegistry.modulesClassLoader( getClass().getClassLoader() );
        this.processor = new ThymeleafProcessorImpl();
    }

    @Test(expected = RuntimeException.class)
    public void testResourceNotFound()
    {
        final Map<String, Object> params = Maps.newHashMap();
        this.processor.process( ResourceKey.from( "mymodule-1.0.0:/view/unknown.html" ), params );
    }

    @Test
    public void testProcessResource()
    {
        final Map<String, Object> params = Maps.newHashMap();
        final String result = this.processor.process( ResourceKey.from( "mymodule-1.0.0:/view/test.html" ), params );
        Assert.assertEquals( "<div>\n    <div><!--# COMPONENT test --></div>\n</div>", result );
    }
}
