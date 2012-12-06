package com.enonic.wem.core.content.type;


import java.io.IOException;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;

import com.enonic.wem.api.content.type.form.FormItemSet;
import com.enonic.wem.api.content.type.form.FormItemSetSubType;
import com.enonic.wem.api.content.type.form.Input;
import com.enonic.wem.api.content.type.form.InputSubType;
import com.enonic.wem.api.content.type.form.SubType;
import com.enonic.wem.api.module.ModuleName;
import com.enonic.wem.core.content.ParsingException;
import com.enonic.wem.core.content.SerializingException;
import com.enonic.wem.core.content.XmlParsingException;
import com.enonic.wem.core.content.type.form.FormItemXmlSerializer;
import com.enonic.wem.core.content.type.form.FormItemsXmlSerializer;

import com.enonic.cms.framework.util.JDOMUtil;

public class SubTypeXmlSerializer
    implements SubTypeSerializer
{
    private FormItemsXmlSerializer formItemsSerializer = new FormItemsXmlSerializer();

    private FormItemXmlSerializer formItemSerializer = formItemsSerializer.getFormItemXmlSerializer();

    private boolean prettyPrint = false;

    public SubTypeXmlSerializer prettyPrint( boolean value )
    {
        this.prettyPrint = value;
        return this;
    }

    @Override
    public String toString( final SubType subType )
        throws SerializingException
    {
        if ( prettyPrint )
        {
            return JDOMUtil.prettyPrintDocument( toJDomDocument( subType ) );
        }
        else
        {
            return JDOMUtil.printDocument( toJDomDocument( subType ) );
        }
    }

    public Document toJDomDocument( SubType type )
    {
        final Element typeEl = new Element( "subtype" );
        generate( type, typeEl );
        return new Document( typeEl );
    }

    private void generate( final SubType subType, final Element typeEl )
    {
        typeEl.addContent( new Element( "name" ).setText( subType.getName() ) );
        typeEl.addContent( new Element( "module" ).setText( subType.getModuleName().toString() ) );
        typeEl.addContent( new Element( "qualified-name" ).setText( subType.getQualifiedName().toString() ) );
        typeEl.addContent( new Element( "display-name" ).setText( subType.getDisplayName() ) );

        if ( subType instanceof InputSubType )
        {
            final InputSubType inputSubType = (InputSubType) subType;
            typeEl.addContent( formItemSerializer.serialize( inputSubType.getInput() ) );
        }
        else
        {
            final FormItemSetSubType formItemSetSubType = (FormItemSetSubType) subType;
            typeEl.addContent( formItemSerializer.serialize( formItemSetSubType.getFormItemSet() ) );
        }
    }

    @Override
    public SubType toSubType( final String xml )
        throws ParsingException
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

    private SubType parse( final Element subTypeEl )
        throws IOException
    {
        Class type = resolveType( subTypeEl );

        if ( type.equals( Input.class ) )
        {
            return parseInputSubType( subTypeEl );
        }
        else if ( type.equals( FormItemSet.class ) )
        {
            return parseFormItemSetSubType( subTypeEl );
        }
        else
        {
            throw new IllegalArgumentException( "Unsupported type of SubType: " + type.getSimpleName() );
        }
    }

    private SubType parseFormItemSetSubType( final Element subTypeEl )
    {
        final FormItemSetSubType.Builder builder = FormItemSetSubType.newFormItemSetSubType();
        builder.module( ModuleName.from( subTypeEl.getChildTextTrim( "module" ) ) );
        builder.displayName( subTypeEl.getChildTextTrim( "display-name" ) );
        final String formItemSetElementName = formItemSerializer.classNameToXmlElementName( FormItemSet.class.getSimpleName() );
        final Element formItemSetEl = subTypeEl.getChild( formItemSetElementName );
        builder.formItemSet( (FormItemSet) formItemSerializer.parse( formItemSetEl ) );
        return builder.build();
    }

    private SubType parseInputSubType( final Element subTypeEl )
    {
        final InputSubType.Builder builder = InputSubType.newInputSubType();
        builder.module( ModuleName.from( subTypeEl.getChildTextTrim( "module" ) ) );
        builder.displayName( subTypeEl.getChildTextTrim( "display-name" ) );
        final String inputElementName = formItemSerializer.classNameToXmlElementName( Input.class.getSimpleName() );
        final Element inputEl = subTypeEl.getChild(inputElementName );
        builder.input( (Input) formItemSerializer.parse( inputEl ) );
        return builder.build();
    }

    private Class resolveType( final Element subTypeEl )
    {
        final String formItemSetElementName = formItemSerializer.classNameToXmlElementName( FormItemSet.class.getSimpleName() );
        final Element formItemSetElement = subTypeEl.getChild( formItemSetElementName );
        if ( formItemSetElement != null )
        {
            return FormItemSet.class;
        }

        final String inputElementName = formItemSerializer.classNameToXmlElementName( Input.class.getSimpleName() );
        final Element inputElement = subTypeEl.getChild( inputElementName );
        if ( inputElement != null )
        {
            return Input.class;
        }

        throw new XmlParsingException( "Unrecognised SubType: " + subTypeEl.toString() );
    }
}
