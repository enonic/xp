package com.enonic.wem.xslt.internal;

import org.junit.Before;
import org.junit.Test;

import junit.framework.Assert;

import com.enonic.wem.api.resource.ResourceKey;
import com.enonic.wem.api.resource.ResourceNotFoundException;
import com.enonic.wem.api.resource.ResourceProblemException;
import com.enonic.wem.api.resource.ResourceUrlRegistry;
import com.enonic.wem.api.resource.ResourceUrlTestHelper;
import com.enonic.wem.xslt.XsltRenderParams;

public class SaxonXsltProcessorTest
{
    private SaxonXsltProcessor processor;

    @Before
    public void setup()
    {
        final ResourceUrlRegistry urlRegistry = ResourceUrlTestHelper.mockModuleScheme();
        urlRegistry.modulesClassLoader( getClass().getClassLoader() );
        this.processor = new SaxonXsltProcessor();
    }

    @Test(expected = ResourceNotFoundException.class)
    public void testResourceNotFound()
    {
        final XsltRenderParams params = new XsltRenderParams().
            view( ResourceKey.from( "mymodule-1.0.0:/view/unknown.xsl" ) ).
            inputXml( "<input/>" );

        this.processor.render( params );
    }

    @Test
    public void testProcessResource()
    {
        final XsltRenderParams params = new XsltRenderParams().
            view( ResourceKey.from( "mymodule-1.0.0:/view/test.xsl" ) ).
            inputXml( "<input/>" );

        final String result = this.processor.render( params );
        Assert.assertEquals( "<div>Hello</div>", result );
    }

    @Test(expected = ResourceProblemException.class)
    public void testViewError()
    {
        final XsltRenderParams params = new XsltRenderParams().
            view( ResourceKey.from( "mymodule-1.0.0:/view/test-error.xsl" ) ).
            inputXml( "<input/>" );

        this.processor.render( params );
    }
}
