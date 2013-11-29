package com.enonic.wem.core.content.serializer;

import java.io.IOException;

import org.apache.commons.lang.StringUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;

import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.content.data.ContentData;
import com.enonic.wem.api.data.RootDataSet;
import com.enonic.wem.api.data.serializer.DataXmlSerializer;
import com.enonic.wem.api.schema.content.ContentTypeName;
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
        contentEl.addContent(
            new Element( "parentPath" ).setText( content.getParentPath() != null ? content.getParentPath().toString() : "" ) );
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
        dataSerializer.generateRootDataSet( dataEl, content.getContentData().toRootDataSet() );
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
        final String parentPath = contentEl.getChildText( "parentPath" );
        if ( !StringUtils.isBlank( parentPath ) )
        {
            contentBuilder.parentPath( ContentPath.from( parentPath ) );
        }
        final String name = contentEl.getChildText( "name" );
        if ( !StringUtils.isBlank( name ) )
        {
            contentBuilder.name( name );
        }

        final String typeAsString = contentEl.getChildText( "type" );
        if ( typeAsString != null )
        {
            final ContentTypeName qualifiedContentTypeName = ContentTypeName.from( typeAsString );
            contentBuilder.type( qualifiedContentTypeName );
        }

        final RootDataSet rootDataSet = dataSerializer.parse( contentEl.getChild( "data" ) );
        final ContentData contentData = new ContentData( rootDataSet );
        contentBuilder.contentData( contentData );

        return contentBuilder.build();
    }
}
