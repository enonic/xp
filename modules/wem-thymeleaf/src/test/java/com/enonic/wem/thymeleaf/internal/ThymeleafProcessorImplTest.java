package com.enonic.wem.thymeleaf.internal;

import org.junit.Before;
import org.junit.Test;

import junit.framework.Assert;

import com.enonic.wem.api.resource.ResourceKey;
import com.enonic.wem.api.resource.ResourceProblemException;
import com.enonic.wem.api.resource.ResourceUrlRegistry;
import com.enonic.wem.api.resource.ResourceUrlTestHelper;
import com.enonic.wem.thymeleaf.ThymeleafRenderParams;

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
        final ThymeleafRenderParams params = new ThymeleafRenderParams().
            view( ResourceKey.from( "mymodule-1.0.0:/view/unknown.html" ) );

        this.processor.render( params );
    }

    @Test
    public void testProcessResource()
    {
        final ThymeleafRenderParams params = new ThymeleafRenderParams().
            view( ResourceKey.from( "mymodule-1.0.0:/view/test.html" ) );

        final String result = this.processor.render( params );
        Assert.assertEquals( "<div>\n    <div><!--# COMPONENT test --></div>\n</div>", result );
    }

    @Test(expected = ResourceProblemException.class)
    public void testViewError()
    {
        final ThymeleafRenderParams params = new ThymeleafRenderParams().
            view( ResourceKey.from( "mymodule-1.0.0:/view/test-error.html" ) );

        this.processor.render( params );
    }
}
