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
import com.enonic.xp.form.FormOptionSet;
import com.enonic.xp.form.FormOptionSetOption;
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

        if ( "option-set".equals( tagName ) )
        {
            return buildFormOptionSetItem( root );
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

        final String labelI18n = getLabelI18n( root );
        builder.label( getLabel( root, labelI18n ) );
        builder.labelI18nKey( labelI18n );

        builder.customText( root.getChildValue( "custom-text" ) );

        final String helpTextI18n = getHelpTextI18n( root );
        builder.helpText( getHelpText( root, helpTextI18n ) );
        builder.helpTextI18nKey( helpTextI18n );

        builder.occurrences( buildOccurrence( root.getChild( "occurrences" ) ) );
        builder.immutable( root.getChildValueAs( "immutable", Boolean.class, false ) );
        builder.indexed( root.getChildValueAs( "indexed", Boolean.class, false ) );
        builder.validationRegexp( root.getChildValue( "validation-regexp" ) );
        builder.maximizeUIInputWidth( root.getChildValueAs( "maximize", Boolean.class, true ) );

        if ( root.getChild( "default" ) != null )
        {
            builder.defaultValue( this.defaultMapper.build( root.getChild( "default" ) ) );
        }
        buildConfig( builder, root.getChild( "config" ) );

        return builder.build();
    }

    private FieldSet buildFieldSetItem( final DomElement root )
    {
        final FieldSet.Builder builder = FieldSet.create();
        builder.name( root.getAttribute( "name" ) );
        final String labelI18n = getLabelI18n( root );
        builder.label( getLabel( root, labelI18n ) );
        builder.labelI18nKey( labelI18n );
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
        final String labelI18n = getLabelI18n( root );
        builder.label( getLabel( root, labelI18n ) );
        builder.labelI18nKey( labelI18n );
        builder.customText( root.getChildValue( "custom-text" ) );
        final String helpTextI18n = getHelpTextI18n( root );
        builder.helpText( getHelpText( root, helpTextI18n ) );
        builder.helpTextI18nKey( helpTextI18n );
        builder.occurrences( buildOccurrence( root.getChild( "occurrences" ) ) );
        builder.immutable( root.getChildValueAs( "immutable", Boolean.class, false ) );
        builder.addFormItems( buildItems( root.getChild( "items" ) ) );
        return builder.build();
    }

    private FormOptionSet buildFormOptionSetItem( final DomElement root )
    {
        final FormOptionSet.Builder builder = FormOptionSet.create();
        builder.name( root.getAttribute( "name" ) );
        final String labelI18n = getLabelI18n( root );
        builder.label( getLabel( root, labelI18n ) );
        builder.labelI18nKey( labelI18n );
        final String helpTextI18n = getHelpTextI18n( root );
        builder.helpText( getHelpText( root, helpTextI18n ) );
        builder.helpTextI18nKey( helpTextI18n );
        builder.expanded( root.getChildValueAs( "expanded", Boolean.class, false ) );
        builder.occurrences( buildOccurrence( root.getChild( "occurrences" ) ) );
        builder.multiselection( buildOccurrence( root.getChild( "options" ) ) );
        builder.addOptionSetOptions( buildOptionSetOptions( root.getChild( "options" ) ) );
        return builder.build();
    }

    private List<FormOptionSetOption> buildOptionSetOptions( final DomElement root )
    {
        if ( root == null )
        {
            return Collections.emptyList();
        }

        return root.getChildren().stream().map( this::buildOptionSetOption ).collect( Collectors.toList() );
    }

    private FormOptionSetOption buildOptionSetOption( final DomElement root )
    {
        final String tagName = root.getTagName();
        if ( "option".equals( tagName ) )
        {
            final FormOptionSetOption.Builder builder = FormOptionSetOption.create();

            builder.name( root.getAttribute( "name" ) );
            final String labelI18n = getLabelI18n( root );
            builder.label( getLabel( root, labelI18n ) );
            builder.labelI18nKey( labelI18n );
            final String helpTextI18n = getHelpTextI18n( root );
            builder.helpText( getHelpText( root, helpTextI18n ) );
            builder.helpTextI18nKey( helpTextI18n );
            builder.defaultOption( root.getChildValueAs( "default", Boolean.class, false ) );
            builder.addFormItems( buildItems( root.getChild( "items" ) ) );

            return builder.build();
        }

        throw new XmlException( "Unknown item type [{0}]", tagName );
    }

    private Occurrences buildOccurrence( final DomElement root )
    {
        if ( root == null )
        {
            return Occurrences.create( 0, 1 );
        }
        final int min = root.getAttributeAs( "minimum", Integer.class, 0 );
        final int max = root.getAttributeAs( "maximum", Integer.class, 0 );
        return Occurrences.create( min, max );
    }

    private void buildConfig( final Input.Builder builder, final DomElement root )
    {
        builder.inputTypeConfig( this.configMapper.build( root ) );
    }

    private String getLabelI18n( final DomElement element )
    {
        return element.getChild( "label" ) != null ? element.getChild( "label" ).getAttribute( "i18n", null ) : null;
    }

    private String getLabel( final DomElement element, final String defaultValue )
    {
        String label = element.getChildValue( "label" );
        if ( label != null && label.trim().isEmpty() )
        {
            label = null;
        }
        return label != null ? label : defaultValue;
    }

    private String getHelpTextI18n( final DomElement element )
    {
        return element.getChild( "help-text" ) != null ? element.getChild( "help-text" ).getAttribute( "i18n", null ) : null;
    }

    private String getHelpText( final DomElement element, final String defaultValue )
    {
        String helpText = element.getChildValue( "help-text" );
        if ( helpText != null && helpText.trim().isEmpty() )
        {
            helpText = null;
        }
        return helpText != null ? helpText : defaultValue;
    }
}
