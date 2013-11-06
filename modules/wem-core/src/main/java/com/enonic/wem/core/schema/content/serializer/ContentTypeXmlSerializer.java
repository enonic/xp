package com.enonic.wem.core.schema.content.serializer;

import java.io.IOException;

import org.apache.commons.lang.StringUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;

import com.enonic.wem.api.form.FormItem;
import com.enonic.wem.api.schema.content.ContentType;
import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.api.support.serializer.XmlParsingException;
import com.enonic.wem.core.support.util.JdomHelper;

import static com.enonic.wem.api.schema.content.ContentType.newContentType;

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
        typeEl.addContent( new Element( "display-name" ).setText( type.getDisplayName() ) );
        typeEl.addContent( new Element( "content-display-name-script" ).setText( type.getContentDisplayNameScript() ) );
        typeEl.addContent( new Element( "super-type" ).setText( type.getSuperType() != null ? type.getSuperType().toString() : null ) );
        typeEl.addContent( new Element( "is-abstract" ).setText( Boolean.toString( type.isAbstract() ) ) );
        typeEl.addContent( new Element( "is-final" ).setText( Boolean.toString( type.isFinal() ) ) );
        typeEl.addContent( new Element( "is-built-in" ).setText( Boolean.toString( type.isBuiltIn() ) ) );
        typeEl.addContent( new Element( "allow-child-content" ).setText( Boolean.toString( type.allowChildContent() ) ) );

        final Element formEl = new Element( "form" );
        typeEl.addContent( formEl );
        formItemsSerializer.serialize( type.form(), formEl );
    }

    public ContentType toContentType( String xml )
        throws XmlParsingException
    {
        try
        {
            final Document document = this.jdomHelper.parse( xml );
            return parse( document.getRootElement() );
        }
        catch ( JDOMException | IOException e )
        {
            throw new XmlParsingException( "Failed to read XML", e );
        }
    }

    private ContentType parse( final Element contentTypeEl )
        throws IOException
    {
        final String name = contentTypeEl.getChildText( "name" );
        final String displayName = contentTypeEl.getChildText( "display-name" );
        final String displayNameScript = contentTypeEl.getChildText( "content-display-name-script" );
        final String superTypeString = StringUtils.trimToNull( contentTypeEl.getChildText( "super-type" ) );
        final ContentTypeName superType = superTypeString != null ? ContentTypeName.from( superTypeString ) : null;
        final boolean isAbstract = Boolean.parseBoolean( contentTypeEl.getChildText( "is-abstract" ) );
        final boolean isFinal = Boolean.parseBoolean( contentTypeEl.getChildText( "is-final" ) );
        final boolean builtIn = Boolean.parseBoolean( contentTypeEl.getChildText( "is-built-in" ) );
        final String allowChildContentValue = contentTypeEl.getChildText( "allow-child-content" );
        final boolean allowChildContent = StringUtils.isBlank( allowChildContentValue ) || Boolean.parseBoolean( allowChildContentValue );

        final ContentType.Builder contentTypeBuilder = newContentType().
            name( name ).
            displayName( displayName ).
            contentDisplayNameScript( displayNameScript ).
            superType( superType ).
            setAbstract( isAbstract ).
            setFinal( isFinal ).
            builtIn( builtIn ).
            allowChildContent( allowChildContent );

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
            throw new XmlParsingException( "Failed to parse ContentType: " + e.getMessage(), e );
        }

        return contentTypeBuilder.build();
    }
}
