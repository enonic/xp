package com.enonic.wem.core.content.type;


import java.io.IOException;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;

import com.enonic.wem.api.content.type.ContentType;
import com.enonic.wem.api.content.type.form.FormItem;
import com.enonic.wem.api.content.type.form.FormItems;
import com.enonic.wem.core.content.XmlParsingException;
import com.enonic.wem.core.content.type.form.FormItemsXmlSerializer;

import com.enonic.cms.framework.util.JDOMUtil;

public class ContentTypeXmlSerializer
    implements ContentTypeSerializer
{
    private FormItemsXmlSerializer formItemsSerializer = new FormItemsXmlSerializer();

    private boolean prettyPrint = false;

    public ContentTypeXmlSerializer prettyPrint( boolean value )
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
        typeEl.addContent( formItemsSerializer.serialize( type.formItemIterable() ) );
    }

    public ContentType toContentType( String xml )
        throws XmlParsingException
    {
        try
        {
            Document document = JDOMUtil.parseDocument( xml );

            return parse( document.getRootElement() );
        }
        catch ( JDOMException e )
        {
            throw new XmlParsingException( "Failed to read XML", e );
        }
        catch ( IOException e )
        {
            throw new XmlParsingException( "Failed to read XML", e );
        }
    }

    private ContentType parse( final Element contentTypeEl )
        throws IOException
    {
        final ContentType contentType = new ContentType();
        contentType.setName( contentTypeEl.getChildText( "name" ) );

        try
        {
            final FormItems formItems = formItemsSerializer.parse( contentTypeEl );
            for ( FormItem formItem : formItems )
            {
                contentType.addFormItem( formItem );
            }
        }
        catch ( Exception e )
        {
            throw new XmlParsingException( "Failed to parse content type: " + JDOMUtil.printElement( contentTypeEl ), e );
        }

        return contentType;
    }
}
