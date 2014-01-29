package com.enonic.wem.portal.script.lib;

import java.io.StringReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import javax.inject.Inject;
import javax.xml.transform.Source;
import javax.xml.transform.SourceLocator;
import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;

import org.mozilla.javascript.xml.XMLObject;
import org.mozilla.javascript.xmlimpl.XMLLibImpl;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import com.google.common.collect.Maps;

import com.enonic.wem.api.content.page.PageComponent;
import com.enonic.wem.api.content.page.PageRegions;
import com.enonic.wem.api.content.page.region.Region;
import com.enonic.wem.api.module.ModuleResourceKey;
import com.enonic.wem.api.module.ResourcePath;
import com.enonic.wem.portal.controller.JsContext;
import com.enonic.wem.portal.script.SourceException;
import com.enonic.wem.portal.xml.DomBuilder;
import com.enonic.wem.portal.xml.DomHelper;
import com.enonic.wem.portal.xslt.XsltProcessor;
import com.enonic.wem.portal.xslt.XsltProcessorException;
import com.enonic.wem.portal.xslt.XsltProcessorSpec;

public final class XsltScriptBean
{
    @Inject
    protected XsltProcessor processor;


    public String render( final String name, final Object inputDoc, final Map<String, Object> params )
    {
        try
        {
            return doRender( name, inputDoc, params );
        }
        catch ( final XsltProcessorException e )
        {
            throw createError( e );
        }
        catch ( final Exception e )
        {
            throw new RuntimeException( e );
        }
    }

    private Source toSource( final Object obj )
        throws Exception
    {
        if ( obj instanceof XMLObject )
        {
            return toSource( (XMLObject) obj );
        }

        final String xml = obj.toString();
        return new StreamSource( new StringReader( xml ) );
    }

    private Source toSource( final XMLObject xml )
        throws Exception
    {
        final Node node = XMLLibImpl.toDomNode( xml );
        final Document doc = DomHelper.newDocument();
        doc.appendChild( doc.importNode( node, true ) );
        return new DOMSource( doc );
    }

    private Source toSource( final Path path )
    {
        return new StreamSource( path.toFile() );
    }

    private String doRender( final String name, final Object inputDoc, final Map<String, Object> params )
    throws Exception
    {
        final ContextScriptBean service = ContextScriptBean.get();
        final Path path = service.resolveFile( name );

        final XsltProcessorSpec spec = new XsltProcessorSpec();
        spec.xsl( toSource( path ) );
        spec.source( toSource( inputDoc ) );
        spec.parameters( convertParams( params ) );

        return this.processor.process( spec );
    }

    private Object convertParam( final Object value )
        throws Exception
    {
        if ( value instanceof XMLObject )
        {
            return toSource( (XMLObject) value );
        }

        return value;
    }

    private Map<String, Object> convertParams( final Map<String, Object> map )
        throws Exception
    {
        final Map<String, Object> result = Maps.newHashMap();
        for ( final Map.Entry<String, Object> entry : map.entrySet() )
        {
            result.put( entry.getKey(), convertParam( entry.getValue() ) );
        }

        result.put( "_", createContextDoc() );
        return result;
    }

    private SourceException createError( final XsltProcessorException e )
    {
        final SourceLocator locator = findLocation( e );

        final SourceException.Builder error = SourceException.newBuilder();
        error.cause( e );
        error.message( e.getMessage() );
        error.lineNumber( locator.getLineNumber() );

        final String path = locator.getSystemId().substring( "file:".length() );
        final Path pathObject = Paths.get( path );
        error.path( pathObject );
        error.resource( findResourceKey( pathObject ) );

        return error.build();
    }

    private SourceLocator findLocation( final XsltProcessorException e )
    {
        for ( final TransformerException entry : e.getErrors() )
        {
            if ( entry.getLocator() != null )
            {
                return entry.getLocator();
            }
        }

        return null;
    }

    private ModuleResourceKey findResourceKey( final Path path )
    {
        final ContextScriptBean service = ContextScriptBean.get();

        final Path filePath = path.toAbsolutePath();
        final Path modulePath = service.getModulePath().toAbsolutePath();

        final String name = filePath.toString().substring( modulePath.toString().length() + 1 );
        return new ModuleResourceKey( service.getModule(), ResourcePath.from( name ) );
    }

    private Document createContextDoc()
        throws Exception
    {
        final ContextScriptBean service = ContextScriptBean.get();
        final JsContext jsContext = service.getJsContext();
        final String baseUrl = jsContext.getUrl().createResourceUrl( "" ).toString();

        final DomBuilder builder = DomBuilder.create( "context" );
        builder.start( "baseUrl" ).text( baseUrl ).end();
        createRegionElements( builder, jsContext.getPageRegions() );

        return builder.getDocument();
    }

    private void createRegionElements( final DomBuilder builder, final PageRegions pageRegions )
    {
        builder.start( "regions" );
        for ( Region region : pageRegions )
        {
            builder.start( "region" );
            final String regionName = region.getName();
            builder.attribute( "name", regionName );
            builder.start( "components" );
            for ( PageComponent component : region.getComponents() )
            {
                builder.start( "component" );
                final String componentName = component.getName().toString();
                builder.attribute( "name", componentName );
                builder.end();
            }
            builder.end();
            builder.end();
        }
        builder.end();
    }
}
