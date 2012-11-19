package com.enonic.wem.core.content.type.form;


import org.jdom.Element;

import com.enonic.wem.api.content.type.form.FieldSet;
import com.enonic.wem.api.content.type.form.FormItem;
import com.enonic.wem.api.content.type.form.FormItemSet;
import com.enonic.wem.api.content.type.form.FormItems;
import com.enonic.wem.api.content.type.form.HierarchicalFormItem;
import com.enonic.wem.api.content.type.form.Input;
import com.enonic.wem.api.content.type.form.Layout;
import com.enonic.wem.api.content.type.form.SubTypeQualifiedName;
import com.enonic.wem.api.content.type.form.SubTypeReference;
import com.enonic.wem.core.content.XmlParsingException;
import com.enonic.wem.core.content.type.form.inputtype.InputTypeConfigXmlSerializer;
import com.enonic.wem.core.content.type.form.inputtype.InputTypeFactory;

import static com.enonic.wem.api.content.type.form.FieldSet.newFieldSet;
import static com.enonic.wem.api.content.type.form.FormItemSet.newFormItemSet;
import static com.enonic.wem.api.content.type.form.Input.newInput;

class ComponentXmlSerializer
{
    public static final String NAME = "name";

    public static final String TYPE = "type";

    public static final String BUILT_IN = "built-in";

    public static final String LABEL = "label";

    public static final String IMMUTABLE = "immutable";

    public static final String INDEXED = "indexed";

    public static final String CUSTOM_TEXT = "customText";

    public static final String HELP_TEXT = "helpText";

    public static final String REFERENCE = "reference";

    public static final String VALIDATION_REGEX = "validationRegex";

    public static final String SUB_TYPE_CLASS = "sub-type-class";

    private final InputTypeConfigXmlSerializer inputTypeConfigSerializer = new InputTypeConfigXmlSerializer();

    private final OccurrencesXmlSerializer occurrencesXmlSerializer = new OccurrencesXmlSerializer();

    private final ComponentsXmlSerializer componentsSerializer;

    public ComponentXmlSerializer( final ComponentsXmlSerializer componentsSerializer )
    {
        this.componentsSerializer = componentsSerializer;
    }

    public Element serialize( FormItem formItem )
    {
        if ( formItem instanceof FormItemSet )
        {
            return serializeComponentSet( (FormItemSet) formItem );
        }
        else if ( formItem instanceof Layout )
        {
            return serializeLayout( (Layout) formItem );
        }
        else if ( formItem instanceof Input )
        {
            return serializeInput( (Input) formItem );
        }
        else if ( formItem instanceof SubTypeReference )
        {
            return serializeReference( (SubTypeReference) formItem );
        }
        return null;
    }

    private Element serializeInput( final Input input )
    {
        Element inputEl = new Element( classNameToXmlElementName( Input.class.getSimpleName() ) );
        inputEl.setAttribute( TYPE, input.getInputType().getClass().getSimpleName() );
        inputEl.setAttribute( BUILT_IN, String.valueOf( input.getInputType().isBuiltIn() ) );

        inputEl.addContent( new Element( NAME ).setText( input.getName() ) );
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
            inputEl.addContent( input.getInputType().getInputTypeConfigXmlGenerator().generate( input.getInputTypeConfig() ) );
        }
    }

    private Element serializeComponentSet( final FormItemSet componentSet )
    {
        final Element componentSetEl = new Element( classNameToXmlElementName( FormItemSet.class.getSimpleName() ) );
        componentSetEl.addContent( new Element( NAME ).setText( componentSet.getName() ) );
        componentSetEl.addContent( new Element( LABEL ).setText( componentSet.getLabel() ) );
        componentSetEl.addContent( new Element( IMMUTABLE ).setText( String.valueOf( componentSet.isImmutable() ) ) );
        componentSetEl.addContent( new Element( CUSTOM_TEXT ).setText( componentSet.getCustomText() ) );
        componentSetEl.addContent( new Element( HELP_TEXT ).setText( componentSet.getCustomText() ) );

        componentSetEl.addContent( occurrencesXmlSerializer.serialize( componentSet.getOccurrences() ) );
        componentSetEl.addContent( componentsSerializer.serialize( componentSet.getFormItems() ) );
        return componentSetEl;
    }

    private Element serializeLayout( final Layout layout )
    {
        final Element layoutEl = new Element( classNameToXmlElementName( Layout.class.getSimpleName() ) );
        layoutEl.addContent( new Element( NAME ).setText( layout.getName() ) );

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
        layoutEl.addContent( componentsSerializer.serialize( fieldSet.getFormItems() ) );
    }

    private Element serializeReference( final SubTypeReference subTypeReference )
    {
        final Element referenceEl = new Element( classNameToXmlElementName( SubTypeReference.class.getSimpleName() ) );
        referenceEl.addContent( new Element( NAME ).setText( subTypeReference.getName() ) );
        referenceEl.addContent( new Element( REFERENCE ).setText( subTypeReference.getSubTypeQualifiedName().toString() ) );
        referenceEl.addContent( new Element( SUB_TYPE_CLASS ).setText( subTypeReference.getSubTypeClass().getSimpleName() ) );
        return referenceEl;
    }

    private void generateValidationRegex( final Input input, final Element inputEl )
    {
        if ( input.getValidationRegexp() != null )
        {
            inputEl.addContent( new Element( VALIDATION_REGEX ).setText( input.getValidationRegexp().toString() ) );
        }
    }

    public FormItem parse( final Element componentEl )
    {
        final String componentType = xmlElementNameToClassName( componentEl.getName() );

        final FormItem formItem;
        if ( componentType.equals( Input.class.getSimpleName() ) )
        {
            formItem = parseInput( componentEl );
        }
        else if ( componentType.equals( FormItemSet.class.getSimpleName() ) )
        {
            formItem = parseComponentSet( componentEl );
        }
        else if ( componentType.equals( Layout.class.getSimpleName() ) )
        {
            formItem = parseLayout( componentEl );
        }
        else if ( componentType.equals( SubTypeReference.class.getSimpleName() ) )
        {
            formItem = parseSubTypeReference( componentEl );
        }
        else
        {
            throw new XmlParsingException( "Unknown FormItemType: " + componentType );
        }

        return formItem;
    }

    private FormItem parseInput( final Element componentEl )
    {
        final Input.Builder builder = newInput();
        builder.name( componentEl.getChildText( NAME ) );
        builder.label( componentEl.getChildText( LABEL ) );
        builder.immutable( Boolean.valueOf( componentEl.getChildText( IMMUTABLE ) ) );
        builder.helpText( componentEl.getChildText( HELP_TEXT ) );
        builder.customText( componentEl.getChildText( CUSTOM_TEXT ) );
        parseValidationRegexp( builder, componentEl );

        builder.occurrences( occurrencesXmlSerializer.parse( componentEl ) );
        parseInputType( builder, componentEl );
        parseInputTypeConfig( builder, componentEl );

        return builder.build();
    }

    private HierarchicalFormItem parseComponentSet( final Element componentEl )
    {
        final FormItemSet.Builder builder = newFormItemSet();
        builder.name( componentEl.getChildText( NAME ) );
        builder.label( componentEl.getChildText( LABEL ) );
        builder.immutable( Boolean.valueOf( componentEl.getChildText( IMMUTABLE ) ) );
        builder.helpText( componentEl.getChildText( HELP_TEXT ) );
        builder.customText( componentEl.getChildText( CUSTOM_TEXT ) );

        builder.occurrences( occurrencesXmlSerializer.parse( componentEl ) );

        final FormItems formItems = componentsSerializer.parse( componentEl );
        for ( FormItem formItem : formItems.iterable() )
        {
            builder.add( formItem );
        }

        return builder.build();
    }

    private FormItem parseLayout( final Element componentEl )
    {
        final String layoutType = componentEl.getAttributeValue( TYPE );
        if ( layoutType.equals( FieldSet.class.getSimpleName() ) )
        {
            return parseFieldSet( componentEl );
        }
        else
        {
            throw new XmlParsingException( "Unknown layoutType: " + layoutType );
        }
    }

    private FormItem parseFieldSet( final Element componentEl )
    {
        final FieldSet.Builder builder = newFieldSet();
        builder.name( componentEl.getChildText( NAME ) );
        builder.label( componentEl.getChildText( LABEL ) );

        final FormItems formItems = componentsSerializer.parse( componentEl );
        for ( FormItem formItem : formItems.iterable() )
        {
            builder.add( formItem );
        }

        return builder.build();
    }

    private HierarchicalFormItem parseSubTypeReference( final Element componentEl )
    {
        final SubTypeReference.Builder builder = SubTypeReference.newSubTypeReference();
        builder.name( componentEl.getChildText( NAME ) );
        builder.subType( new SubTypeQualifiedName( componentEl.getChildText( REFERENCE ) ) );
        builder.type( componentEl.getChildText( SUB_TYPE_CLASS ) );
        return builder.build();
    }

    private void parseValidationRegexp( final Input.Builder builder, final Element componentEl )
    {
        String validationRegexp = componentEl.getChildText( VALIDATION_REGEX );
        if ( validationRegexp != null )
        {
            builder.validationRegexp( validationRegexp );
        }
    }

    private void parseInputTypeConfig( final Input.Builder builder, final Element componentEl )
    {
        builder.inputTypeConfig( inputTypeConfigSerializer.parse( componentEl ) );
    }

    private void parseInputType( final Input.Builder builder, final Element componentEl )
    {
        final String inputTypeName = componentEl.getAttributeValue( TYPE );
        final boolean builtIn = Boolean.valueOf( componentEl.getAttributeValue( BUILT_IN ) );
        builder.type( InputTypeFactory.instantiate( inputTypeName, builtIn ) );
    }

    private String classNameToXmlElementName( final String s )
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
