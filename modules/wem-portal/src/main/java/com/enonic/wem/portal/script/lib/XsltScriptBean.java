package com.enonic.wem.portal.script.lib;

import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import javax.inject.Inject;
import javax.xml.parsers.DocumentBuilderFactory;
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

import com.enonic.wem.api.module.ModuleResourceKey;
import com.enonic.wem.api.module.ResourcePath;
import com.enonic.wem.portal.script.SourceException;
import com.enonic.wem.portal.xslt.XsltProcessor;
import com.enonic.wem.portal.xslt.XsltProcessorException;
import com.enonic.wem.portal.xslt.XsltProcessorSpec;

public final class XsltScriptBean
{
    @Inject
    protected XsltProcessor processor;

    private final DocumentBuilderFactory documentBuilderFactory;

    public XsltScriptBean()
    {
        this.documentBuilderFactory = DocumentBuilderFactory.newInstance();
    }

    public String render( final String name, final Object inputDoc, final Map<String, Object> params )
    {
        final ContextScriptBean service = ContextScriptBean.get();
        final Path path = service.resolveFile( name );

        if ( !Files.isRegularFile( path ) )
        {
            throw new IllegalArgumentException( "Could not find XSLT view [" + path.toString() + "]" );
        }

        final XsltProcessorSpec spec = new XsltProcessorSpec();
        spec.xsl( toSource( path ) );
        spec.source( toSource( inputDoc ) );
        spec.parameters( convertParams( params ) );
        return render( spec );
    }

    private Source toSource( final Object obj )
    {
        if ( obj instanceof XMLObject )
        {
            return toSource( (XMLObject) obj );
        }

        final String xml = obj.toString();
        return new StreamSource( new StringReader( xml ) );
    }

    private Source toSource( final XMLObject xml )
    {
        try
        {
            final Node node = XMLLibImpl.toDomNode( xml );
            final Document doc = this.documentBuilderFactory.newDocumentBuilder().newDocument();
            doc.appendChild( doc.importNode( node, true ) );
            return new DOMSource( doc );
        }
        catch ( final Exception e )
        {
            throw new RuntimeException( e );
        }
    }

    private Source toSource( final Path path )
    {
        return new StreamSource( path.toFile() );
    }

    private String render( final XsltProcessorSpec spec )
    {
        try
        {
            return this.processor.process( spec );
        }
        catch ( final XsltProcessorException e )
        {
            throw createError( e );
        }
    }

    private Object convertParam( final Object value )
    {
        if ( value instanceof XMLObject )
        {
            return toSource( (XMLObject) value );
        }

        return value;
    }

    private Map<String, Object> convertParams( final Map<String, Object> map )
    {
        return Maps.transformEntries( map, new Maps.EntryTransformer<String, Object, Object>()
        {
            @Override
            public Object transformEntry( final String name, final Object value )
            {
                return convertParam( value );
            }
        } );
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
}
