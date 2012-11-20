package com.enonic.wem.core.content;


import java.io.IOException;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;

import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.type.QualifiedContentTypeName;
import com.enonic.wem.core.content.data.ContentDataXmlSerializer;

import com.enonic.cms.framework.util.JDOMUtil;

public class ContentXmlSerializer
    implements ContentSerializer
{
    private ContentDataXmlSerializer contentDataSerializer = new ContentDataXmlSerializer();

    private boolean prettyPrint = false;

    public ContentXmlSerializer()
    {
    }

    public ContentXmlSerializer prettyPrint( boolean value )
    {
        this.prettyPrint = value;
        return this;
    }

    public String toString( Content content )
        throws XmlSerializingException
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
            contentEl.addContent( new Element( "type" ).setText( content.getType().toString() ) );
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
            final QualifiedContentTypeName qualifiedContentTypeName = new QualifiedContentTypeName( typeAsString );
            content.setType( qualifiedContentTypeName );
        }

        content.setData( contentDataSerializer.parse( contentEl.getChild( "data" ) ) );

        return content;
    }
}
