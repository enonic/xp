package com.enonic.xp.xml.parser;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.google.common.annotations.Beta;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.ApplicationRelativeResolver;
import com.enonic.xp.form.FieldSet;
import com.enonic.xp.form.Form;
import com.enonic.xp.form.FormItem;
import com.enonic.xp.form.FormItemSet;
import com.enonic.xp.form.InlineMixin;
import com.enonic.xp.form.Input;
import com.enonic.xp.form.Occurrences;
import com.enonic.xp.inputtype.InputTypeName;
import com.enonic.xp.xml.DomElement;
import com.enonic.xp.xml.XmlException;

@Beta
public final class XmlFormMapper
{
    private final ApplicationKey currentApplication;

    private XmlInputTypeConfigMapper configMapper;

    private XmlInputTypeDefaultMapper defaultMapper;

    public XmlFormMapper( final ApplicationKey currentApplication )
    {
        this.currentApplication = currentApplication;
    }

    public Form buildForm( final DomElement root )
    {
        final Form.Builder builder = Form.create();
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
        final InputTypeName inputTypeName = InputTypeName.from( root.getAttribute( "type" ) );

        this.configMapper = new XmlInputTypeConfigMapper( this.currentApplication, inputTypeName );
        this.defaultMapper = new XmlInputTypeDefaultMapper();

        builder.inputType( inputTypeName );
        builder.name( root.getAttribute( "name" ) );
        builder.label( root.getChildValue( "label" ) );

        builder.customText( root.getChildValue( "custom-text" ) );
        builder.helpText( root.getChildValue( "help-text" ) );
        builder.occurrences( buildOccurrence( root.getChild( "occurrences" ) ) );
        builder.immutable( root.getChildValueAs( "immutable", Boolean.class, false ) );
        builder.indexed( root.getChildValueAs( "indexed", Boolean.class, false ) );
        builder.validationRegexp( root.getChildValue( "validation-regexp" ) );
        builder.maximizeUIInputWidth( root.getChildValueAs( "maximize", Boolean.class, true ) );

        if ( root.getChild( "default" ) != null )
        {
            builder.defaultValue( this.defaultMapper.build( root.getChild( "default" ) ) );
        }
        buildConfig( builder, root.getChild( "config" ), inputTypeName );

        return builder.build();
    }

    private FieldSet buildFieldSetItem( final DomElement root )
    {
        final FieldSet.Builder builder = FieldSet.create();
        builder.name( root.getAttribute( "name" ) );
        builder.label( root.getChildValue( "label" ) );
        builder.addFormItems( buildItems( root.getChild( "items" ) ) );
        return builder.build();
    }

    private InlineMixin buildInlineItem( final DomElement root )
    {
        final InlineMixin.Builder builder = InlineMixin.create();
        builder.mixin( new ApplicationRelativeResolver( this.currentApplication ).toMixinName( root.getAttribute( "mixin" ) ) );
        return builder.build();
    }

    private FormItemSet buildFormItemSetItem( final DomElement root )
    {
        final FormItemSet.Builder builder = FormItemSet.create();
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
        final int min = root.getAttributeAs( "minimum", Integer.class, 0 );
        final int max = root.getAttributeAs( "maximum", Integer.class, 0 );
        return Occurrences.create( min, max );
    }

    private void buildConfig( final Input.Builder builder, final DomElement root, final InputTypeName inputTypeName )
    {
        builder.inputTypeConfig( this.configMapper.build( root ) );
    }
}
