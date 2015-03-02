package com.enonic.xp.xml.parser;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.w3c.dom.Element;

import com.enonic.xp.form.FieldSet;
import com.enonic.xp.form.Form;
import com.enonic.xp.form.FormItem;
import com.enonic.xp.form.FormItemSet;
import com.enonic.xp.form.InlineMixin;
import com.enonic.xp.form.Input;
import com.enonic.xp.form.Occurrences;
import com.enonic.xp.form.inputtype.InputType;
import com.enonic.xp.form.inputtype.InputTypeConfig;
import com.enonic.xp.form.inputtype.InputTypes;
import com.enonic.xp.module.ModuleKey;
import com.enonic.xp.module.ModuleRelativeResolver;
import com.enonic.xp.xml.DomHelper;
import com.enonic.xp.xml.XmlException;

import static com.enonic.xp.xml.parser.XmlParserHelper.getAttributeAsInteger;
import static com.enonic.xp.xml.parser.XmlParserHelper.getAttributeAsString;
import static com.enonic.xp.xml.parser.XmlParserHelper.getChildElementAsBoolean;
import static com.enonic.xp.xml.parser.XmlParserHelper.getChildElementAsString;

final class XmlFormMapper
{
    private final ModuleKey currentModule;

    public XmlFormMapper( final ModuleKey currentModule )
    {
        this.currentModule = currentModule;
    }

    public Form buildForm( final Element root )
    {
        final Form.Builder builder = Form.newForm();
        if ( root != null )
        {
            buildForm( root, builder );
        }

        return builder.build();
    }

    private void buildForm( final Element root, final Form.Builder builder )
    {
        builder.addFormItems( buildItems( root ) );
    }

    private List<FormItem> buildItems( final Element root )
    {
        if ( root == null )
        {
            return Collections.emptyList();
        }

        return DomHelper.getChildElements( root ).stream().map( this::buildItem ).collect( Collectors.toList() );
    }

    private FormItem buildItem( final Element root )
    {
        final String tagName = root.getTagName();
        if ( tagName.equals( "input" ) )
        {
            return buildInputItem( root );
        }

        if ( tagName.equals( "field-set" ) )
        {
            return buildFieldSetItem( root );
        }

        if ( tagName.equals( "inline" ) )
        {
            return buildInlineItem( root );
        }

        if ( tagName.equals( "form-item-set" ) )
        {
            return buildFormItemSetItem( root );
        }

        throw new XmlException( "Unknown item type [{0}]", tagName );
    }

    private Input buildInputItem( final Element root )
    {
        final Input.Builder builder = Input.newInput();

        final InputType type = InputTypes.parse( getAttributeAsString( root, "type", null ) );
        builder.inputType( type );

        builder.name( getAttributeAsString( root, "name", null ) );
        builder.label( getChildElementAsString( root, "label", null ) );
        builder.customText( getChildElementAsString( root, "custom-text", null ) );
        builder.helpText( getChildElementAsString( root, "help-text", null ) );
        builder.occurrences( buildOccurrence( DomHelper.getChildElementByTagName( root, "occurrences" ) ) );
        builder.immutable( getChildElementAsBoolean( root, "immutable", false ) );
        builder.indexed( getChildElementAsBoolean( root, "indexed", false ) );
        builder.validationRegexp( getChildElementAsString( root, "validation-regexp", null ) );
        builder.inputTypeConfig( fromConfigXml( type, DomHelper.getChildElementByTagName( root, "config" ) ) );

        return builder.build();
    }

    private FieldSet buildFieldSetItem( final Element root )
    {
        final FieldSet.Builder builder = FieldSet.newFieldSet();
        builder.name( getAttributeAsString( root, "name", null ) );
        builder.label( getChildElementAsString( root, "label", null ) );
        builder.addFormItems( buildItems( DomHelper.getChildElementByTagName( root, "items" ) ) );
        return builder.build();
    }

    private InlineMixin buildInlineItem( final Element root )
    {
        final InlineMixin.Builder builder = InlineMixin.newInlineMixin();
        builder.mixin( new ModuleRelativeResolver( this.currentModule ).toMixinName( root.getAttribute( "mixin" ) ) );
        return builder.build();
    }

    private FormItemSet buildFormItemSetItem( final Element root )
    {
        final FormItemSet.Builder builder = FormItemSet.newFormItemSet();
        builder.name( getAttributeAsString( root, "name", null ) );
        builder.label( getChildElementAsString( root, "label", null ) );
        builder.customText( getChildElementAsString( root, "custom-text", null ) );
        builder.helpText( getChildElementAsString( root, "help-text", null ) );
        builder.occurrences( buildOccurrence( DomHelper.getChildElementByTagName( root, "occurrences" ) ) );
        builder.immutable( getChildElementAsBoolean( root, "immutable", false ) );
        builder.addFormItems( buildItems( DomHelper.getChildElementByTagName( root, "items" ) ) );
        return builder.build();
    }

    private Occurrences buildOccurrence( final Element root )
    {
        final Occurrences.Builder builder = Occurrences.newOccurrences();
        builder.minimum( getAttributeAsInteger( root, "minimum", 0 ) );
        builder.maximum( getAttributeAsInteger( root, "maximum", 0 ) );
        return builder.build();
    }

    private InputTypeConfig fromConfigXml( final InputType type, final Element value )
    {
        if ( value == null )
        {
            return null;
        }

        return type.getInputTypeConfigXmlSerializer().parseConfig( this.currentModule, value );
    }
}
