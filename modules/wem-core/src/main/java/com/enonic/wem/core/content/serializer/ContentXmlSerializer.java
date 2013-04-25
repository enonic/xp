package com.enonic.wem.core.content.serializer;

import java.io.IOException;

import org.apache.commons.lang.StringUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;

import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.data.DataSet;
import com.enonic.wem.api.content.data.RootDataSet;
import com.enonic.wem.api.content.schema.content.QualifiedContentTypeName;
import com.enonic.wem.core.support.util.JdomHelper;

public class ContentXmlSerializer
    implements ContentSerializer
{
    private DataXmlSerializer dataSerializer = new DataXmlSerializer();

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
        final Element dataEl = new Element( "data" );
        contentEl.addContent( dataEl );
        dataSerializer.generateRootDataSet( dataEl, content.getRootDataSet() );
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
        final String name = contentEl.getChildText( "name" );
        if ( !StringUtils.isBlank( name ) )
        {
            contentBuilder.name( name );
        }

        final String typeAsString = contentEl.getChildText( "type" );
        if ( typeAsString != null )
        {
            final QualifiedContentTypeName qualifiedContentTypeName = new QualifiedContentTypeName( typeAsString );
            contentBuilder.type( qualifiedContentTypeName );
        }

        final RootDataSet rootDataSet = DataSet.newRootDataSet();
        dataSerializer.parse( contentEl.getChild( "data" ), rootDataSet );
        contentBuilder.rootDataSet( rootDataSet );

        return contentBuilder.build();
    }
}
