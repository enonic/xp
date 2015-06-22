package com.enonic.xp.xml.parser;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.google.common.annotations.Beta;

import com.enonic.xp.form.FieldSet;
import com.enonic.xp.form.Form;
import com.enonic.xp.form.FormItem;
import com.enonic.xp.form.FormItemSet;
import com.enonic.xp.form.InlineMixin;
import com.enonic.xp.form.Input;
import com.enonic.xp.form.Occurrences;
import com.enonic.xp.form.inputtype.AbstractInputTypeConfigXmlSerializer;
import com.enonic.xp.form.inputtype.InputType;
import com.enonic.xp.form.inputtype.InputTypeConfig;
import com.enonic.xp.form.inputtype.InputTypes;
import com.enonic.xp.module.ModuleKey;
import com.enonic.xp.module.ModuleRelativeResolver;
import com.enonic.xp.xml.DomElement;
import com.enonic.xp.xml.XmlException;

@Beta
public final class XmlFormMapper
{
    private final ModuleKey currentModule;

    public XmlFormMapper( final ModuleKey currentModule )
    {
        this.currentModule = currentModule;
    }

    public Form buildForm( final DomElement root )
    {
        final Form.Builder builder = Form.newForm();
        if ( root != null )
        {
            buildForm( root, builder );
        }

        return builder.build();
    }

    private void buildForm( final DomElement root, final Form.Builder builder )
    {
        builder.addFormItems( buildItems( root ) );
    }

    private List<FormItem> buildItems( final DomElement root )
    {
        if ( root == null )
        {
            return Collections.emptyList();
        }

        return root.getChildren().stream().map( this::buildItem ).collect( Collectors.toList() );
    }

    private FormItem buildItem( final DomElement root )
    {
        final String tagName = root.getTagName();
        if ( "input".equals( tagName ) )
        {
            return buildInputItem( root );
        }

        if ( "field-set".equals( tagName ) )
        {
            return buildFieldSetItem( root );
        }

        if ( "inline".equals( tagName ) )
        {
            return buildInlineItem( root );
        }

        if ( "item-set".equals( tagName ) )
        {
            return buildFormItemSetItem( root );
        }

        throw new XmlException( "Unknown item type [{0}]", tagName );
    }

    private Input buildInputItem( final DomElement root )
    {
        final Input.Builder builder = Input.create();

        final InputType type = InputTypes.parse( root.getAttribute( "type" ) );
        builder.inputType( type );

        builder.name( root.getAttribute( "name" ) );
        builder.label( root.getChildValue( "label" ) );
        builder.customText( root.getChildValue( "custom-text" ) );
        builder.helpText( root.getChildValue( "help-text" ) );
        builder.occurrences( buildOccurrence( root.getChild( "occurrences" ) ) );
        builder.immutable( root.getChildValueAs( "immutable", Boolean.class, false ) );
        builder.indexed( root.getChildValueAs( "indexed", Boolean.class, false ) );
        builder.validationRegexp( root.getChildValue( "validation-regexp" ) );
        builder.inputTypeConfig( fromConfigXml( type, root.getChild( "config" ) ) );

        return builder.build();
    }

    private FieldSet buildFieldSetItem( final DomElement root )
    {
        final FieldSet.Builder builder = FieldSet.newFieldSet();
        builder.name( root.getAttribute( "name" ) );
        builder.label( root.getChildValue( "label" ) );
        builder.addFormItems( buildItems( root.getChild( "items" ) ) );
        return builder.build();
    }

    private InlineMixin buildInlineItem( final DomElement root )
    {
        final InlineMixin.Builder builder = InlineMixin.newInlineMixin();
        builder.mixin( new ModuleRelativeResolver( this.currentModule ).toMixinName( root.getAttribute( "mixin" ) ) );
        return builder.build();
    }

    private FormItemSet buildFormItemSetItem( final DomElement root )
    {
        final FormItemSet.Builder builder = FormItemSet.newFormItemSet();
        builder.name( root.getAttribute( "name" ) );
        builder.label( root.getChildValue( "label" ) );
        builder.customText( root.getChildValue( "custom-text" ) );
        builder.helpText( root.getChildValue( "help-text" ) );
        builder.occurrences( buildOccurrence( root.getChild( "occurrences" ) ) );
        builder.immutable( root.getChildValueAs( "immutable", Boolean.class, false ) );
        builder.addFormItems( buildItems( root.getChild( "items" ) ) );
        return builder.build();
    }

    private Occurrences buildOccurrence( final DomElement root )
    {
        final Occurrences.Builder builder = Occurrences.newOccurrences();
        builder.minimum( root.getAttributeAs( "minimum", Integer.class, 0 ) );
        builder.maximum( root.getAttributeAs( "maximum", Integer.class, 0 ) );
        return builder.build();
    }

    private InputTypeConfig fromConfigXml( final InputType type, final DomElement value )
    {
        if ( value == null )
        {
            return type.getDefaultConfig();
        }

        final AbstractInputTypeConfigXmlSerializer configXmlSerializer = type.getInputTypeConfigXmlSerializer();
        if ( configXmlSerializer == null )
        {
            return type.getDefaultConfig();
        }
        return configXmlSerializer.parseConfig( this.currentModule, value.getWrapped() );
    }
}
