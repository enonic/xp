package com.enonic.wem.core.content;


import java.io.IOException;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;

import com.enonic.wem.api.content.type.ContentType;
import com.enonic.wem.api.content.type.ContentTypeFetcher;
import com.enonic.wem.api.content.type.ContentTypeQualifiedName;
import com.enonic.wem.core.content.data.ContentDataSerializerXml;

import com.enonic.cms.framework.util.JDOMUtil;

public class ContentSerializerXml
    implements ContentSerializer
{
    private ContentTypeFetcher contentTypeFetcher;

    private ContentDataSerializerXml contentDataSerializer = new ContentDataSerializerXml();

    private boolean prettyPrint = false;

    public ContentSerializerXml( final ContentTypeFetcher contentTypeFetcher )
    {
        this.contentTypeFetcher = contentTypeFetcher;
    }

    public ContentSerializerXml prettyPrint( boolean value )
    {
        this.prettyPrint = value;
        return this;
    }

    public String toString( Content content )
    {
        if ( prettyPrint )
        {
            return JDOMUtil.prettyPrintDocument( toJDomDocument( content ) );
        }
        else
        {
            return JDOMUtil.printDocument( toJDomDocument( content ) );
        }
    }

    public Document toJDomDocument( Content content )
    {
        final Element contentEl = new Element( "content" );
        generate( content, contentEl );
        return new Document( contentEl );
    }

    public void generate( Content content, Element contentEl )
    {
        contentEl.addContent( new Element( "name" ).setText( content.getName() ) );

        if ( content.getType() != null )
        {
            contentEl.addContent( new Element( "type" ).setText( content.getType().getQualifiedName().toString() ) );
        }
        else
        {
            // no element for type
        }
        contentEl.addContent( contentDataSerializer.generate( content.getData() ) );
    }

    public Content toContent( final String xml )
    {
        try
        {
            Document document = JDOMUtil.parseDocument( xml );

            return parse( document.getRootElement() );
        }
        catch ( JDOMException e )
        {
            throw new RuntimeException( "Failed to read XML", e );
        }
        catch ( IOException e )
        {
            throw new RuntimeException( "Failed to read XML", e );
        }
    }

    public Content parse( final Element contentEl )
    {
        final Content content = new Content();
        content.setName( contentEl.getChildText( "name" ) );

        final String typeAsString = contentEl.getChildText( "type" );
        if ( typeAsString != null )
        {
            final ContentTypeQualifiedName contentTypeQualifiedName = new ContentTypeQualifiedName( typeAsString );
            final ContentType contentType = contentTypeFetcher.getContentType( contentTypeQualifiedName );
            content.setType( contentType );
        }

        content.setData( contentDataSerializer.parse( contentEl.getChild( "data" ) ) );

        return content;
    }
}
