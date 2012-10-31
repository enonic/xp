package com.enonic.wem.core.content.type;


import java.io.IOException;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;

import com.enonic.wem.api.content.type.ContentType;
import com.enonic.wem.api.content.type.ContentTypeSerializer;
import com.enonic.wem.core.content.JsonParsingException;
import com.enonic.wem.core.content.type.formitem.ComponentsSerializerXml;

import com.enonic.cms.framework.util.JDOMUtil;

public class ContentTypeSerializerXml
    implements ContentTypeSerializer
{
    private ComponentsSerializerXml componentsSerializer = new ComponentsSerializerXml();

    private boolean prettyPrint = false;

    public ContentTypeSerializerXml prettyPrint( boolean value )
    {
        this.prettyPrint = value;
        return this;
    }

    public String toString( ContentType type )
    {
        if ( prettyPrint )
        {
            return JDOMUtil.prettyPrintDocument( toJDomDocument( type ) );
        }
        else
        {
            return JDOMUtil.printDocument( toJDomDocument( type ) );
        }
    }

    public Document toJDomDocument( ContentType type )
    {
        final Element typeEl = new Element( "type" );
        generate( type, typeEl );
        return new Document( typeEl );
    }


    private void generate( final ContentType type, final Element typeEl )
    {
        typeEl.addContent( new Element( "name" ).setText( type.getName() ) );
        typeEl.addContent( new Element( "module" ).setText( type.getModule().getName() ) );
        typeEl.addContent( componentsSerializer.generate( type.getComponents() ) );
    }

    public ContentType toContentType( String xml )
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

    private ContentType parse( final Element contentTypeEl )
        throws IOException
    {
        final ContentType contentType = new ContentType();
        contentType.setName( contentTypeEl.getChildText( "name" ) );

        try
        {
            contentType.setComponents( componentsSerializer.parse( contentTypeEl ) );
        }
        catch ( Exception e )
        {
            throw new JsonParsingException( "Failed to parse content type: " + JDOMUtil.printElement( contentTypeEl ), e );
        }

        return contentType;
    }
}
