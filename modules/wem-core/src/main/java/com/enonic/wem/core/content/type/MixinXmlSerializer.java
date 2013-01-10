package com.enonic.wem.core.content.type;


import java.io.IOException;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;

import com.enonic.wem.api.content.type.form.FormItemSet;
import com.enonic.wem.api.content.type.form.FormItemSetMixin;
import com.enonic.wem.api.content.type.form.Input;
import com.enonic.wem.api.content.type.form.InputMixin;
import com.enonic.wem.api.content.type.form.Mixin;
import com.enonic.wem.api.module.ModuleName;
import com.enonic.wem.core.content.ParsingException;
import com.enonic.wem.core.content.SerializingException;
import com.enonic.wem.core.content.XmlParsingException;
import com.enonic.wem.core.content.type.form.FormItemXmlSerializer;
import com.enonic.wem.core.content.type.form.FormItemsXmlSerializer;

import com.enonic.cms.framework.util.JDOMUtil;

public class MixinXmlSerializer
    implements MixinSerializer
{
    private FormItemsXmlSerializer formItemsSerializer = new FormItemsXmlSerializer();

    private FormItemXmlSerializer formItemSerializer = formItemsSerializer.getFormItemXmlSerializer();

    private boolean prettyPrint = false;

    public MixinXmlSerializer prettyPrint( boolean value )
    {
        this.prettyPrint = value;
        return this;
    }

    @Override
    public String toString( final Mixin mixin )
        throws SerializingException
    {
        if ( prettyPrint )
        {
            return JDOMUtil.prettyPrintDocument( toJDomDocument( mixin ) );
        }
        else
        {
            return JDOMUtil.printDocument( toJDomDocument( mixin ) );
        }
    }

    public Document toJDomDocument( Mixin type )
    {
        final Element typeEl = new Element( "mixin" );
        generate( type, typeEl );
        return new Document( typeEl );
    }

    private void generate( final Mixin mixin, final Element typeEl )
    {
        typeEl.addContent( new Element( "name" ).setText( mixin.getName() ) );
        typeEl.addContent( new Element( "module" ).setText( mixin.getModuleName().toString() ) );
        typeEl.addContent( new Element( "qualified-name" ).setText( mixin.getQualifiedName().toString() ) );
        typeEl.addContent( new Element( "display-name" ).setText( mixin.getDisplayName() ) );

        if ( mixin instanceof InputMixin )
        {
            final InputMixin inputMixin = (InputMixin) mixin;
            typeEl.addContent( formItemSerializer.serialize( inputMixin.getInput() ) );
        }
        else
        {
            final FormItemSetMixin formItemSetMixin = (FormItemSetMixin) mixin;
            typeEl.addContent( formItemSerializer.serialize( formItemSetMixin.getFormItemSet() ) );
        }
    }

    @Override
    public Mixin toMixin( final String xml )
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

    private Mixin parse( final Element mixinEl )
        throws IOException
    {
        Class type = resolveType( mixinEl );

        if ( type.equals( Input.class ) )
        {
            return parseInputMixin( mixinEl );
        }
        else if ( type.equals( FormItemSet.class ) )
        {
            return parseFormItemSetMixin( mixinEl );
        }
        else
        {
            throw new IllegalArgumentException( "Unsupported type of Mixin: " + type.getSimpleName() );
        }
    }

    private Mixin parseFormItemSetMixin( final Element mixinEl )
    {
        final FormItemSetMixin.Builder builder = FormItemSetMixin.newFormItemSetMixin();
        builder.module( ModuleName.from( mixinEl.getChildTextTrim( "module" ) ) );
        builder.displayName( mixinEl.getChildTextTrim( "display-name" ) );
        final String formItemSetElementName = formItemSerializer.classNameToXmlElementName( FormItemSet.class.getSimpleName() );
        final Element formItemSetEl = mixinEl.getChild( formItemSetElementName );
        builder.formItemSet( (FormItemSet) formItemSerializer.parse( formItemSetEl ) );
        return builder.build();
    }

    private Mixin parseInputMixin( final Element mixinEl )
    {
        final InputMixin.Builder builder = InputMixin.newInputMixin();
        builder.module( ModuleName.from( mixinEl.getChildTextTrim( "module" ) ) );
        builder.displayName( mixinEl.getChildTextTrim( "display-name" ) );
        final String inputElementName = formItemSerializer.classNameToXmlElementName( Input.class.getSimpleName() );
        final Element inputEl = mixinEl.getChild( inputElementName );
        builder.input( (Input) formItemSerializer.parse( inputEl ) );
        return builder.build();
    }

    private Class resolveType( final Element mixinEl )
    {
        final String formItemSetElementName = formItemSerializer.classNameToXmlElementName( FormItemSet.class.getSimpleName() );
        final Element formItemSetElement = mixinEl.getChild( formItemSetElementName );
        if ( formItemSetElement != null )
        {
            return FormItemSet.class;
        }

        final String inputElementName = formItemSerializer.classNameToXmlElementName( Input.class.getSimpleName() );
        final Element inputElement = mixinEl.getChild( inputElementName );
        if ( inputElement != null )
        {
            return Input.class;
        }

        throw new XmlParsingException( "Unrecognised Mixin: " + mixinEl.toString() );
    }
}
