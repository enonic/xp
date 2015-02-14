package com.enonic.xp.portal.impl.xslt;

import java.io.StringReader;
import java.net.URL;

import javax.xml.transform.stream.StreamSource;

import org.junit.Before;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

import com.enonic.wem.api.resource.ResourceKey;
import com.enonic.wem.api.resource.ResourceUrlRegistry;
import com.enonic.wem.api.resource.ResourceUrlTestHelper;
import com.enonic.wem.api.xml.DomHelper;
import com.enonic.xp.portal.impl.xslt.XsltProcessor;
import com.enonic.xp.portal.impl.xslt.XsltProcessorFactory;
import com.enonic.xp.portal.url.PortalUrlService;

import static org.junit.Assert.*;

public abstract class AbstractFunctionTest
{
    private XsltProcessor processor;

    @Before
    public final void setup()
    {
        final ResourceUrlRegistry urlRegistry = ResourceUrlTestHelper.mockModuleScheme();
        urlRegistry.modulesClassLoader( getClass().getClassLoader() );

        final XsltProcessorFactory factory = new XsltProcessorFactory();
        factory.urlService = Mockito.mock( PortalUrlService.class, (Answer) this::urlAnswer );
        factory.initialize();

        this.processor = factory.newProcessor();
    }

    private Object urlAnswer( final InvocationOnMock invocation )
        throws Exception
    {
        return invocation.getArguments()[0].toString();
    }

    protected final void processTemplate( final String baseName )
        throws Exception
    {
        final String name = "/" + getClass().getName().replace( '.', '/' ) + "-" + baseName;

        this.processor.view( ResourceKey.from( "mymodule:" + name + ".xsl" ) );
        this.processor.inputSource( new StreamSource( new StringReader( "<dummy/>" ) ) );
        final String actual = cleanupXml( this.processor.process() );

        final URL actualUrl = getClass().getResource( name + "-result.xml" );
        final String expected = cleanupXml( Resources.toString( actualUrl, Charsets.UTF_8 ) );

        assertEquals( expected, actual );
    }

    private String cleanupXml( final String xml )
        throws Exception
    {
        return DomHelper.serialize( DomHelper.parse( xml ) );
    }
}
