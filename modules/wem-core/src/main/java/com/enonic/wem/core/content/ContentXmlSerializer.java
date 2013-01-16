package com.enonic.wem.core.content;

import java.io.IOException;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;

import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.type.QualifiedContentTypeName;
import com.enonic.wem.core.content.data.ContentDataXmlSerializer;
import com.enonic.wem.core.util.JdomHelper;

public class ContentXmlSerializer
    implements ContentSerializer
{
    private ContentDataXmlSerializer contentDataSerializer = new ContentDataXmlSerializer();

    private final JdomHelper jdomHelper = new JdomHelper();

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
        return this.jdomHelper.serialize( toJDomDocument( content ), this.prettyPrint );
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
            final Document document = this.jdomHelper.parse( xml );
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
        final Content.Builder contentBuilder = Content.newContent();
        contentBuilder.name( contentEl.getChildText( "name" ) );

        final String typeAsString = contentEl.getChildText( "type" );
        if ( typeAsString != null )
        {
            final QualifiedContentTypeName qualifiedContentTypeName = new QualifiedContentTypeName( typeAsString );
            contentBuilder.type( qualifiedContentTypeName );
        }

        contentBuilder.data( contentDataSerializer.parse( contentEl.getChild( "data" ) ) );

        return contentBuilder.build();
    }
}
