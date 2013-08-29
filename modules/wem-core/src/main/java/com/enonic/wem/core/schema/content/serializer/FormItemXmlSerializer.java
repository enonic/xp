package com.enonic.wem.core.schema.content.serializer;


import org.jdom.Element;

import com.enonic.wem.api.schema.content.form.FieldSet;
import com.enonic.wem.api.schema.content.form.FormItem;
import com.enonic.wem.api.schema.content.form.FormItemSet;
import com.enonic.wem.api.schema.content.form.Input;
import com.enonic.wem.api.schema.content.form.Layout;
import com.enonic.wem.api.schema.content.form.MixinReference;
import com.enonic.wem.api.schema.content.form.inputtype.BaseInputType;
import com.enonic.wem.api.schema.content.form.inputtype.InputTypeName;
import com.enonic.wem.api.schema.mixin.QualifiedMixinName;
import com.enonic.wem.core.schema.content.form.inputtype.InputTypeResolver;
import com.enonic.wem.core.support.serializer.XmlParsingException;

import static com.enonic.wem.api.schema.content.form.FieldSet.newFieldSet;
import static com.enonic.wem.api.schema.content.form.FormItemSet.newFormItemSet;
import static com.enonic.wem.api.schema.content.form.Input.newInput;

public class FormItemXmlSerializer
{
    public static final String NAME = "name";

    public static final String TYPE = "type";

    public static final String LABEL = "label";

    public static final String IMMUTABLE = "immutable";

    public static final String INDEXED = "indexed";

    public static final String CUSTOM_TEXT = "custom-text";

    public static final String HELP_TEXT = "help-text";

    public static final String REFERENCE = "reference";

    public static final String VALIDATION_REGEX = "validation-regex";

    private final InputTypeConfigXmlSerializer inputTypeConfigSerializer = new InputTypeConfigXmlSerializer();

    private final OccurrencesXmlSerializer occurrencesXmlSerializer = new OccurrencesXmlSerializer();

    private final FormItemsXmlSerializer formItemsSerializer;

    public FormItemXmlSerializer( final FormItemsXmlSerializer formItemsSerializer )
    {
        this.formItemsSerializer = formItemsSerializer;
    }

    public Element serialize( FormItem formItem )
    {
        if ( formItem instanceof FormItemSet )
        {
            return serializeFormItemSet( (FormItemSet) formItem );
        }
        else if ( formItem instanceof Layout )
        {
            return serializeLayout( (Layout) formItem );
        }
        else if ( formItem instanceof Input )
        {
            return serializeInput( (Input) formItem );
        }
        else if ( formItem instanceof MixinReference )
        {
            return serializeReference( (MixinReference) formItem );
        }
        return null;
    }

    private Element serializeInput( final Input input )
    {
        Element inputEl = new Element( classNameToXmlElementName( Input.class.getSimpleName() ) );
        inputEl.setAttribute( TYPE, InputTypeName.from( (BaseInputType) input.getInputType() ).toString() );
        inputEl.setAttribute( NAME, String.valueOf( input.getName() ) );

        inputEl.addContent( new Element( LABEL ).setText( input.getLabel() ) );
        inputEl.addContent( new Element( IMMUTABLE ).setText( String.valueOf( input.isImmutable() ) ) );
        inputEl.addContent( new Element( INDEXED ).setText( String.valueOf( input.isIndexed() ) ) );
        inputEl.addContent( new Element( CUSTOM_TEXT ).setText( input.getCustomText() ) );
        inputEl.addContent( new Element( HELP_TEXT ).setText( input.getHelpText() ) );
        inputEl.addContent( occurrencesXmlSerializer.serialize( input.getOccurrences() ) );
        generateValidationRegex( input, inputEl );
        generateInputTypeConfig( input, inputEl );
        return inputEl;
    }

    private void generateInputTypeConfig( final Input input, final Element inputEl )
    {
        if ( input.getInputType().requiresConfig() && input.getInputTypeConfig() != null )
        {
            inputEl.addContent( input.getInputType().getInputTypeConfigXmlSerializer().generate( input.getInputTypeConfig() ) );
        }
    }

    private Element serializeFormItemSet( final FormItemSet set )
    {
        final Element formItemSetEl = new Element( classNameToXmlElementName( FormItemSet.class.getSimpleName() ) );
        formItemSetEl.setAttribute( NAME, String.valueOf( set.getName() ) );
        formItemSetEl.addContent( new Element( LABEL ).setText( set.getLabel() ) );
        formItemSetEl.addContent( new Element( IMMUTABLE ).setText( String.valueOf( set.isImmutable() ) ) );
        formItemSetEl.addContent( new Element( CUSTOM_TEXT ).setText( set.getCustomText() ) );
        formItemSetEl.addContent( new Element( HELP_TEXT ).setText( set.getCustomText() ) );

        formItemSetEl.addContent( occurrencesXmlSerializer.serialize( set.getOccurrences() ) );
        final Element itemsEl = new Element( "items" );
        formItemSetEl.addContent( itemsEl );
        formItemsSerializer.serialize( set.getFormItems(), itemsEl );
        return formItemSetEl;
    }

    private Element serializeLayout( final Layout layout )
    {
        final Element layoutEl = new Element( classNameToXmlElementName( Layout.class.getSimpleName() ) );
        layoutEl.setAttribute( NAME, String.valueOf( layout.getName() ) );

        if ( layout instanceof FieldSet )
        {
            generateFieldSet( (FieldSet) layout, layoutEl );
        }

        return layoutEl;
    }

    private void generateFieldSet( final FieldSet fieldSet, final Element layoutEl )
    {
        layoutEl.setAttribute( TYPE, FieldSet.class.getSimpleName() );
        layoutEl.addContent( new Element( LABEL ).setText( fieldSet.getLabel() ) );
        final Element itemsEl = new Element( "items" );
        layoutEl.addContent( itemsEl );
        formItemsSerializer.serialize( fieldSet.getFormItems(), itemsEl );
    }

    private Element serializeReference( final MixinReference mixinReference )
    {
        final Element referenceEl = new Element( classNameToXmlElementName( MixinReference.class.getSimpleName() ) );
        referenceEl.setAttribute( NAME, String.valueOf( mixinReference.getName() ) );
        referenceEl.addContent( new Element( NAME ).setText( mixinReference.getName() ) );
        referenceEl.addContent( new Element( REFERENCE ).setText( mixinReference.getQualifiedMixinName().toString() ) );
        referenceEl.addContent( new Element( TYPE ).setText( mixinReference.getMixinClass().getSimpleName() ) );
        return referenceEl;
    }

    private void generateValidationRegex( final Input input, final Element inputEl )
    {
        if ( input.getValidationRegexp() != null )
        {
            inputEl.addContent( new Element( VALIDATION_REGEX ).setText( input.getValidationRegexp().toString() ) );
        }
    }

    public FormItem parse( final Element formItemEl )
    {
        final String formItemType = xmlElementNameToClassName( formItemEl.getName() );

        final FormItem formItem;
        if ( formItemType.equals( Input.class.getSimpleName() ) )
        {
            formItem = parseInput( formItemEl );
        }
        else if ( formItemType.equals( FormItemSet.class.getSimpleName() ) )
        {
            formItem = parseFormItemSet( formItemEl );
        }
        else if ( formItemType.equals( Layout.class.getSimpleName() ) )
        {
            formItem = parseLayout( formItemEl );
        }
        else if ( formItemType.equals( MixinReference.class.getSimpleName() ) )
        {
            formItem = parseMixinReference( formItemEl );
        }
        else
        {
            throw new XmlParsingException( "Unknown FormItemType: " + formItemType );
        }

        return formItem;
    }

    private FormItem parseInput( final Element formItemEl )
    {
        final Input.Builder builder = newInput();
        builder.name( formItemEl.getAttributeValue( NAME ) );
        builder.label( formItemEl.getChildText( LABEL ) );
        builder.immutable( Boolean.valueOf( formItemEl.getChildText( IMMUTABLE ) ) );
        builder.indexed( Boolean.valueOf( formItemEl.getChildText( INDEXED ) ) );
        builder.helpText( formItemEl.getChildText( HELP_TEXT ) );
        builder.customText( formItemEl.getChildText( CUSTOM_TEXT ) );
        parseValidationRegexp( builder, formItemEl );

        builder.occurrences( occurrencesXmlSerializer.parse( formItemEl ) );
        parseInputType( builder, formItemEl );

        return builder.build();
    }

    private FormItemSet parseFormItemSet( final Element formItemEl )
    {
        final FormItemSet.Builder builder = newFormItemSet();
        builder.name( formItemEl.getAttributeValue( NAME ) );
        builder.label( formItemEl.getChildText( LABEL ) );
        builder.immutable( Boolean.valueOf( formItemEl.getChildText( IMMUTABLE ) ) );
        builder.helpText( formItemEl.getChildText( HELP_TEXT ) );
        builder.customText( formItemEl.getChildText( CUSTOM_TEXT ) );

        builder.occurrences( occurrencesXmlSerializer.parse( formItemEl ) );

        final Element itemsEl = formItemEl.getChild( "items" );
        for ( FormItem formItem : formItemsSerializer.parse( itemsEl ) )
        {
            builder.addFormItem( formItem );
        }

        return builder.build();
    }

    private FormItem parseLayout( final Element formItemEl )
    {
        final String layoutType = formItemEl.getAttributeValue( TYPE );
        if ( layoutType.equals( FieldSet.class.getSimpleName() ) )
        {
            return parseFieldSet( formItemEl );
        }
        else
        {
            throw new XmlParsingException( "Unknown layoutType: " + layoutType );
        }
    }

    private FormItem parseFieldSet( final Element formItemEl )
    {
        final FieldSet.Builder builder = newFieldSet();
        builder.name( formItemEl.getAttributeValue( NAME ) );
        builder.label( formItemEl.getChildText( LABEL ) );

        final Element itemsEl = formItemEl.getChild( "items" );
        for ( FormItem formItem : formItemsSerializer.parse( itemsEl ) )
        {
            builder.add( formItem );
        }

        return builder.build();
    }

    private MixinReference parseMixinReference( final Element formItemEl )
    {
        final MixinReference.Builder builder = MixinReference.newMixinReference();
        builder.name( formItemEl.getAttributeValue( NAME ) );
        builder.mixin( new QualifiedMixinName( formItemEl.getChildText( REFERENCE ) ) );
        builder.type( formItemEl.getChildText( TYPE ) );
        return builder.build();
    }

    private void parseValidationRegexp( final Input.Builder builder, final Element formItemEl )
    {
        String validationRegexp = formItemEl.getChildText( VALIDATION_REGEX );
        if ( validationRegexp != null )
        {
            builder.validationRegexp( validationRegexp );
        }
    }

    private void parseInputType( final Input.Builder builder, final Element formItemEl )
    {
        final String inputTypeName = formItemEl.getAttributeValue( TYPE );
        final BaseInputType inputType = InputTypeResolver.get().resolve( inputTypeName );
        builder.inputType( inputType );
        builder.inputTypeConfig( inputTypeConfigSerializer.parse( formItemEl, inputType.getClass() ) );
    }

    public String classNameToXmlElementName( final String s )
    {
        final StringBuilder newS = new StringBuilder( s.length() );
        for ( int i = 0; i < s.length(); i++ )
        {
            char c = s.charAt( i );
            if ( Character.isUpperCase( c ) && i == 0 )
            {
                newS.append( Character.toLowerCase( c ) );
            }
            else if ( Character.isUpperCase( c ) )
            {
                newS.append( "-" ).append( Character.toLowerCase( c ) );
            }
            else
            {
                newS.append( c );
            }
        }
        return newS.toString();
    }

    private String xmlElementNameToClassName( final String s )
    {
        final StringBuilder newS = new StringBuilder( s.length() );
        for ( int i = 0; i < s.length(); i++ )
        {
            char c = s.charAt( i );

            Character nextC = i < s.length() - 1 ? s.charAt( i + 1 ) : null;

            if ( nextC == null )
            {
                newS.append( c );
            }
            else if ( i == 0 )
            {
                newS.append( Character.toUpperCase( c ) );
            }
            else if ( c == '-' )
            {
                newS.append( Character.toUpperCase( nextC ) );
                i++;
            }
            else
            {
                newS.append( c );
            }
        }
        return newS.toString();
    }

}
