package com.enonic.wem.core.content.schema.content.serializer;

import java.io.IOException;

import org.apache.commons.lang.StringUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;

import com.enonic.wem.api.content.schema.content.ContentType;
import com.enonic.wem.api.content.schema.content.QualifiedContentTypeName;
import com.enonic.wem.api.content.schema.content.form.FormItem;
import com.enonic.wem.api.module.ModuleName;
import com.enonic.wem.core.support.serializer.XmlParsingException;
import com.enonic.wem.core.support.util.JdomHelper;

import static com.enonic.wem.api.content.schema.content.ContentType.newContentType;

public class ContentTypeXmlSerializer
    implements ContentTypeSerializer
{
    private final FormItemsXmlSerializer formItemsSerializer = new FormItemsXmlSerializer();

    private final JdomHelper jdomHelper = new JdomHelper();

    private boolean prettyPrint = true;

    public ContentTypeXmlSerializer prettyPrint( boolean value )
    {
        this.prettyPrint = value;
        return this;
    }

    public String toString( ContentType type )
    {
        return this.jdomHelper.serialize( toJDomDocument( type ), this.prettyPrint );
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
        typeEl.addContent( new Element( "module" ).setText( type.getModuleName().toString() ) );
        typeEl.addContent( new Element( "display-name" ).setText( type.getDisplayName() ) );
        typeEl.addContent( new Element( "content-display-name-script" ).setText( type.getContentDisplayNameScript() ) );
        typeEl.addContent( new Element( "super-type" ).setText( type.getSuperType() != null ? type.getSuperType().toString() : null ) );
        typeEl.addContent( new Element( "is-abstract" ).setText( Boolean.toString( type.isAbstract() ) ) );
        typeEl.addContent( new Element( "is-final" ).setText( Boolean.toString( type.isFinal() ) ) );

        final Element formEl = new Element( "form" );
        typeEl.addContent( formEl );
        formItemsSerializer.serialize( type.form().formItemIterable(), formEl );
    }

    public ContentType toContentType( String xml )
        throws XmlParsingException
    {
        try
        {
            final Document document = this.jdomHelper.parse( xml );
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
        final String module = contentTypeEl.getChildText( "module" );
        final String name = contentTypeEl.getChildText( "name" );
        final String displayName = contentTypeEl.getChildText( "display-name" );
        final String displayNameScript = contentTypeEl.getChildText( "content-display-name-script" );
        final String superTypeString = StringUtils.trimToNull( contentTypeEl.getChildText( "super-type" ) );
        final QualifiedContentTypeName superType = superTypeString != null ? new QualifiedContentTypeName( superTypeString ) : null;
        final boolean isAbstract = Boolean.parseBoolean( contentTypeEl.getChildText( "is-abstract" ) );
        final boolean isFinal = Boolean.parseBoolean( contentTypeEl.getChildText( "is-final" ) );

        final ContentType.Builder contentTypeBuilder = newContentType().
            name( name ).
            module( ModuleName.from( module ) ).
            displayName( displayName ).
            contentDisplayNameScript( displayNameScript ).
            superType( superType ).
            setAbstract( isAbstract ).
            setFinal( isFinal );

        try
        {
            final Element formEl = contentTypeEl.getChild( "form" );
            for ( FormItem formItem : formItemsSerializer.parse( formEl ) )
            {
                contentTypeBuilder.addFormItem( formItem );
            }
        }
        catch ( Exception e )
        {
            throw new XmlParsingException( "Failed to parse content type: " + this.jdomHelper.serialize( contentTypeEl, this.prettyPrint ),
                                           e );
        }

        return contentTypeBuilder.build();
    }
}
