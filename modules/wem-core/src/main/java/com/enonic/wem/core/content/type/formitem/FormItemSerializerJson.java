package com.enonic.wem.core.content.type.formitem;


import java.io.IOException;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonNode;

import com.enonic.wem.api.content.type.formitem.Component;
import com.enonic.wem.api.content.type.formitem.FieldSet;
import com.enonic.wem.api.content.type.formitem.FormItem;
import com.enonic.wem.api.content.type.formitem.FormItemSet;
import com.enonic.wem.api.content.type.formitem.FormItems;
import com.enonic.wem.api.content.type.formitem.HierarchicalFormItem;
import com.enonic.wem.api.content.type.formitem.Layout;
import com.enonic.wem.api.content.type.formitem.SubTypeQualifiedName;
import com.enonic.wem.api.content.type.formitem.SubTypeReference;
import com.enonic.wem.api.content.type.formitem.comptype.BaseComponentType;
import com.enonic.wem.core.content.JsonParserUtil;
import com.enonic.wem.core.content.JsonParsingException;
import com.enonic.wem.core.content.type.formitem.comptype.ComponentTypeConfigSerializerJson;
import com.enonic.wem.core.content.type.formitem.comptype.ComponentTypeSerializerJson;

import static com.enonic.wem.api.content.type.formitem.FieldSet.newFieldSet;
import static com.enonic.wem.api.content.type.formitem.FormItemSet.newFormItemSet;

public class FormItemSerializerJson
{
    private ComponentTypeSerializerJson componentTypeSerializer = new ComponentTypeSerializerJson();

    private ComponentTypeConfigSerializerJson componentTypeConfigSerializer = new ComponentTypeConfigSerializerJson();

    private final FormItemsSerializerJson formItemsSerializerJson;


    public FormItemSerializerJson()
    {
        this.formItemsSerializerJson = new FormItemsSerializerJson();
    }

    public FormItemSerializerJson( final FormItemsSerializerJson formItemsSerializerJson )
    {
        this.formItemsSerializerJson = formItemsSerializerJson;
    }

    public void generate( FormItem formItem, JsonGenerator g )
        throws IOException
    {
        if ( formItem instanceof FormItemSet )
        {
            generateFormItemSet( (FormItemSet) formItem, g );
        }
        else if ( formItem instanceof Layout )
        {
            generateLayout( (Layout) formItem, g );
        }
        else if ( formItem instanceof Component )
        {
            generateComponent( (Component) formItem, g );
        }
        else if ( formItem instanceof SubTypeReference )
        {
            generateReference( (SubTypeReference) formItem, g );
        }
    }

    private void generateComponent( final Component component, final JsonGenerator g )
        throws IOException
    {
        g.writeStartObject();
        g.writeStringField( "formItemType", Component.class.getSimpleName() );
        g.writeStringField( "name", component.getName() );
        componentTypeSerializer.generate( component.getComponentType(), g );
        g.writeStringField( "label", component.getLabel() );
        g.writeBooleanField( "required", component.isRequired() );
        g.writeBooleanField( "immutable", component.isImmutable() );
        OccurrencesSerializerJson.generate( component.getOccurrences(), g );
        g.writeBooleanField( "indexed", component.isIndexed() );
        g.writeStringField( "customText", component.getCustomText() );
        g.writeStringField( "validationRegexp",
                            component.getValidationRegexp() != null ? component.getValidationRegexp().toString() : null );
        g.writeStringField( "helpText", component.getHelpText() );
        if ( component.getComponentType().requiresConfig() && component.getComponentTypeConfig() != null )
        {
            g.writeFieldName( "componentTypeConfig" );
            component.getComponentType().getComponentTypeConfigJsonGenerator().generate( component.getComponentTypeConfig(), g );
        }

        g.writeEndObject();
    }

    private void generateFormItemSet( final FormItemSet formItemSet, JsonGenerator g )
        throws IOException
    {
        g.writeStartObject();
        g.writeStringField( "formItemType", FormItemSet.class.getSimpleName() );
        g.writeStringField( "name", formItemSet.getName() );
        g.writeStringField( "label", formItemSet.getLabel() );
        g.writeBooleanField( "required", formItemSet.isRequired() );
        g.writeBooleanField( "immutable", formItemSet.isImmutable() );
        OccurrencesSerializerJson.generate( formItemSet.getOccurrences(), g );
        g.writeStringField( "customText", formItemSet.getCustomText() );
        g.writeStringField( "helpText", formItemSet.getHelpText() );
        formItemsSerializerJson.generate( formItemSet.getFormItems(), g );

        g.writeEndObject();
    }

    private void generateLayout( final Layout layout, JsonGenerator g )
        throws IOException
    {
        g.writeStartObject();
        g.writeStringField( "formItemType", Layout.class.getSimpleName() );

        if ( layout instanceof FieldSet )
        {
            generateFieldSet( (FieldSet) layout, g );
        }

        g.writeEndObject();
    }

    private void generateFieldSet( final FieldSet fieldSet, JsonGenerator g )
        throws IOException
    {
        g.writeStringField( "layoutType", FieldSet.class.getSimpleName() );
        g.writeStringField( "label", fieldSet.getLabel() );
        g.writeStringField( "name", fieldSet.getName() );
        formItemsSerializerJson.generate( fieldSet.getFormItems(), g );
    }

    private void generateReference( final SubTypeReference subTypeReference, final JsonGenerator g )
        throws IOException
    {
        g.writeStartObject();
        g.writeStringField( "formItemType", SubTypeReference.class.getSimpleName() );
        g.writeStringField( "name", subTypeReference.getName() );
        g.writeStringField( "reference", subTypeReference.getSubTypeQualifiedName().toString() );
        g.writeStringField( "subTypeClass", subTypeReference.getSubTypeClass().getSimpleName() );
        g.writeEndObject();
    }

    public FormItem parse( final JsonNode formItemNode )
    {
        final String formItemType = JsonParserUtil.getStringValue( "formItemType", formItemNode );

        final FormItem formItem;

        if ( formItemType.equals( Component.class.getSimpleName() ) )
        {
            formItem = parseComponent( formItemNode );
        }
        else if ( formItemType.equals( FormItemSet.class.getSimpleName() ) )
        {
            formItem = parseFormItemSet( formItemNode );
        }
        else if ( formItemType.equals( Layout.class.getSimpleName() ) )
        {
            formItem = parseLayout( formItemNode );
        }
        else if ( formItemType.equals( SubTypeReference.class.getSimpleName() ) )
        {
            formItem = parseSubTypeReference( formItemNode );
        }
        else
        {
            throw new JsonParsingException( "Unknown FormItemType: " + formItemType );
        }

        return formItem;
    }

    private HierarchicalFormItem parseComponent( final JsonNode formItemNode )
    {
        final Component.Builder builder = Component.newComponent();
        builder.name( JsonParserUtil.getStringValue( "name", formItemNode ) );
        builder.label( JsonParserUtil.getStringValue( "label", formItemNode, null ) );
        builder.immutable( JsonParserUtil.getBooleanValue( "immutable", formItemNode ) );
        builder.helpText( JsonParserUtil.getStringValue( "helpText", formItemNode ) );
        builder.customText( JsonParserUtil.getStringValue( "customText", formItemNode ) );
        parseValidationRegexp( builder, formItemNode );

        parseOccurrences( builder, formItemNode.get( "occurrences" ) );
        parseComponentType( builder, formItemNode.get( "componentType" ) );
        parseComponentTypeConfig( builder, formItemNode.get( "componentTypeConfig" ) );

        return builder.build();
    }

    private HierarchicalFormItem parseFormItemSet( final JsonNode formItemNode )
    {
        final FormItemSet.Builder builder = newFormItemSet();
        builder.name( JsonParserUtil.getStringValue( "name", formItemNode ) );
        builder.label( JsonParserUtil.getStringValue( "label", formItemNode, null ) );
        builder.required( JsonParserUtil.getBooleanValue( "required", formItemNode ) );
        builder.immutable( JsonParserUtil.getBooleanValue( "immutable", formItemNode ) );
        builder.helpText( JsonParserUtil.getStringValue( "helpText", formItemNode ) );
        builder.customText( JsonParserUtil.getStringValue( "customText", formItemNode ) );

        parseOccurrences( builder, formItemNode.get( "occurrences" ) );

        final FormItems formItems = formItemsSerializerJson.parse( formItemNode.get( "items" ) );
        for ( FormItem formItem : formItems.iterable() )
        {
            builder.add( formItem );
        }

        return builder.build();
    }

    private FormItem parseLayout( final JsonNode formItemNode )
    {
        final String layoutType = JsonParserUtil.getStringValue( "layoutType", formItemNode );
        if ( layoutType.equals( FieldSet.class.getSimpleName() ) )
        {
            return parseFieldSet( formItemNode );
        }
        else
        {
            throw new JsonParsingException( "Unknown layoutType: " + layoutType );
        }
    }

    private FormItem parseFieldSet( final JsonNode formItemNode )
    {
        final FieldSet.Builder builder = newFieldSet();
        builder.label( JsonParserUtil.getStringValue( "label", formItemNode, null ) );
        builder.name( JsonParserUtil.getStringValue( "name", formItemNode, null ) );

        final FormItems formItems = formItemsSerializerJson.parse( formItemNode.get( "items" ) );
        for ( FormItem formItem : formItems.iterable() )
        {
            builder.add( formItem );
        }

        return builder.build();
    }

    private HierarchicalFormItem parseSubTypeReference( final JsonNode formItemNode )
    {
        final SubTypeReference.Builder builder = SubTypeReference.newSubTypeReference();
        builder.name( JsonParserUtil.getStringValue( "name", formItemNode ) );
        builder.subType( new SubTypeQualifiedName( JsonParserUtil.getStringValue( "reference", formItemNode ) ) );
        builder.type( JsonParserUtil.getStringValue( "subTypeClass", formItemNode ) );
        return builder.build();
    }

    private void parseValidationRegexp( final Component.Builder builder, final JsonNode componentNode )
    {
        final String validationRegexp = JsonParserUtil.getStringValue( "validationRegexp", componentNode, null );
        if ( validationRegexp != null )
        {
            builder.validationRegexp( validationRegexp );
        }
    }

    private void parseOccurrences( final Component.Builder builder, final JsonNode occurrencesNode )
    {
        if ( occurrencesNode != null )
        {
            builder.occurrences( OccurrencesSerializerJson.parse( occurrencesNode ) );
        }
        else
        {
            builder.multiple( false );
        }
    }

    private void parseOccurrences( final FormItemSet.Builder builder, final JsonNode occurrencesNode )
    {
        if ( occurrencesNode != null )
        {
            builder.occurrences( OccurrencesSerializerJson.parse( occurrencesNode ) );
        }
        else
        {
            builder.multiple( false );
        }
    }

    private void parseComponentTypeConfig( final Component.Builder builder, final JsonNode componentTypeConfigNode )
    {
        if ( componentTypeConfigNode != null )
        {
            builder.componentTypeConfig( componentTypeConfigSerializer.parse( componentTypeConfigNode ) );
        }
    }

    private void parseComponentType( final Component.Builder builder, final JsonNode componentTypeNode )
    {
        if ( componentTypeNode != null )
        {
            BaseComponentType componentType = componentTypeSerializer.parse( componentTypeNode );
            builder.type( componentType );
        }
    }
}
