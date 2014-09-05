package com.enonic.wem.mustache.internal;

import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Maps;

import junit.framework.Assert;

import com.enonic.wem.api.resource.ResourceKey;
import com.enonic.wem.api.resource.ResourceProblemException;
import com.enonic.wem.api.resource.ResourceUrlRegistry;
import com.enonic.wem.api.resource.ResourceUrlTestHelper;
import com.enonic.wem.mustache.MustacheProcessor;
import com.enonic.wem.mustache.MustacheProcessorFactory;

public class MustacheProcessorImplTest
{
    private MustacheProcessorFactory processorFactory;

    @Before
    public void setup()
    {
        final ResourceUrlRegistry urlRegistry = ResourceUrlTestHelper.mockModuleScheme();
        urlRegistry.modulesClassLoader( getClass().getClassLoader() );
        this.processorFactory = new MustacheProcessorFactoryImpl();
    }

    @Test(expected = RuntimeException.class)
    public void testResourceNotFound()
    {
        final MustacheProcessor processor = this.processorFactory.newProcessor().
            view( ResourceKey.from( "mymodule-1.0.0:/view/unknown.html" ) );

        processor.process();
    }

    @Test
    public void testProcessResource()
    {
        final Map<String, Object> params = Maps.newHashMap();
        params.put( "name", "Steve" );

        final MustacheProcessor processor = this.processorFactory.newProcessor().
            view( ResourceKey.from( "mymodule-1.0.0:/view/test.html" ) ).
            parameters( params );

        final String result = processor.process();
        Assert.assertEquals( "<div>Hello Steve!</div>", result );
    }

    @Test(expected = ResourceProblemException.class)
    public void testViewError()
    {
        final MustacheProcessor processor = this.processorFactory.newProcessor().
            view( ResourceKey.from( "mymodule-1.0.0:/view/test.html" ) );

        processor.process();
    }
}
