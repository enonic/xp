package com.enonic.wem.portal.script.lib;

import java.io.StringReader;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Source;
import javax.xml.transform.SourceLocator;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.mozilla.javascript.xml.XMLObject;
import org.mozilla.javascript.xmlimpl.XMLLibImpl;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import net.sf.saxon.TransformerFactoryImpl;

import com.enonic.wem.api.module.ModuleResourceKey;
import com.enonic.wem.api.module.ResourcePath;
import com.enonic.wem.portal.script.SourceException;

public final class XsltScriptBean
{
    private final DocumentBuilderFactory documentBuilderFactory;

    public XsltScriptBean()
    {
        this.documentBuilderFactory = DocumentBuilderFactory.newInstance();
    }

    public String render( final String name, final Object inputDoc, final Map<Object, Object> params )
        throws Exception
    {
        final ContextScriptBean service = ContextScriptBean.get();
        final Path path = service.resolveFile( name );

        if ( !Files.isRegularFile( path ) )
        {
            throw new IllegalArgumentException( "Could not find XSLT view [" + path.toString() + "]" );
        }

        final Source xsltSource = toSource( path );
        final Source xmlSource = toSource( inputDoc );

        return render( xsltSource, xmlSource, params );
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
        final Document doc = this.documentBuilderFactory.newDocumentBuilder().newDocument();
        doc.appendChild( doc.importNode( node, true ) );
        return new DOMSource( doc );
    }

    private Source toSource( final Path path )
    {
        return new StreamSource( path.toFile() );
    }

    private String render( final Source xslt, final Source xml, final Map<Object, Object> params )
        throws Exception
    {
        final XsltErrorListener listener = new XsltErrorListener();

        try
        {
            final StringWriter writer = new StringWriter();
            final StreamResult result = new StreamResult( writer );

            final TransformerFactoryImpl transformerFactory = new TransformerFactoryImpl();
            transformerFactory.getConfiguration().setLineNumbering( true );

            transformerFactory.setErrorListener( listener );
            final Transformer transformer = transformerFactory.newTransformer( xslt );

            for ( final Map.Entry<Object, Object> param : params.entrySet() )
            {
                transformer.setParameter( param.getKey().toString(), convertParam( param.getValue() ) );
            }

            transformer.transform( xml, result );
            return writer.getBuffer().toString();
        }
        catch ( final TransformerException e )
        {
            throw createError( e, listener );
        }
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

    private Exception createError( final TransformerException e, final XsltErrorListener otherErrors )
    {
        final StringBuilder str = new StringBuilder( e.getMessage() );

        int num = 1;
        for ( final TransformerException entry : otherErrors )
        {
            str.append( "\n" );
            str.append( "  " ).append( num++ ).append( ") " ).append( entry.getMessage() );
        }

        final SourceLocator locator = findLocation( e, otherErrors );

        final SourceException.Builder error = SourceException.newBuilder();
        error.cause( e );
        error.message( str.toString() );
        error.lineNumber( locator.getLineNumber() );

        final String path = locator.getSystemId().substring( "file:".length() );
        final Path pathObject = Paths.get( path );
        error.path( pathObject );
        error.resource( findResourceKey( pathObject ) );

        return error.build();
    }

    private SourceLocator findLocation( final TransformerException e, final XsltErrorListener otherErrors )
    {
        if ( e.getLocator() != null )
        {
            return e.getLocator();
        }

        for ( final TransformerException error : otherErrors )
        {
            if ( error.getLocator() != null )
            {
                return error.getLocator();
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
