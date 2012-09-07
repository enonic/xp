package com.enonic.wem.core.content.type.formitem;


import java.io.IOException;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonNode;

import com.enonic.wem.core.content.JsonParserUtil;
import com.enonic.wem.core.content.JsonParsingException;
import com.enonic.wem.core.content.type.formitem.fieldtype.ComponentType;
import com.enonic.wem.core.content.type.formitem.fieldtype.ComponentTypeConfigSerializerJson;
import com.enonic.wem.core.content.type.formitem.fieldtype.FieldTypeSerializerJson;

import static com.enonic.wem.core.content.type.formitem.FormItemSet.newFormItemTest;
import static com.enonic.wem.core.content.type.formitem.TemplateReference.newTemplateReference;
import static com.enonic.wem.core.content.type.formitem.VisualFieldSet.newVisualFieldSet;

public class FormItemSerializerJson
{
    private FieldTypeSerializerJson componentTypeSerializer = new FieldTypeSerializerJson();

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
        else if ( formItem instanceof VisualFieldSet )
        {
            generateVisualFieldSet( (VisualFieldSet) formItem, g );
        }
        else if ( formItem instanceof Component )
        {
            generateField( (Component) formItem, g );
        }
        else if ( formItem instanceof TemplateReference )
        {
            generateReference( (TemplateReference) formItem, g );
        }
    }

    private void generateField( final Component component, final JsonGenerator g )
        throws IOException
    {
        g.writeStartObject();
        g.writeStringField( "formItemType", component.getFormItemType().toString() );
        g.writeStringField( "path", component.getPath().toString() );
        g.writeStringField( "name", component.getName() );
        componentTypeSerializer.generate( component.getComponentType(), g );
        g.writeStringField( "label", component.getLabel() );
        g.writeBooleanField( "required", component.isRequired() );
        g.writeBooleanField( "immutable", component.isImmutable() );
        OccurrencesSerializerJson.generate( component.getOccurrences(), g );
        g.writeBooleanField( "indexed", component.isIndexed() );
        g.writeStringField( "customText", component.getCustomText() );
        g.writeStringField( "validationRegexp", component.getValidationRegexp() );
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
        g.writeStringField( "formItemType", formItemSet.getFormItemType().toString() );
        g.writeStringField( "path", formItemSet.getPath().toString() );
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

    private void generateVisualFieldSet( final VisualFieldSet visualFieldSet, JsonGenerator g )
        throws IOException
    {
        g.writeStartObject();
        g.writeStringField( "formItemType", visualFieldSet.getFormItemType().toString() );
        g.writeStringField( "label", visualFieldSet.getLabel() );
        g.writeStringField( "name", visualFieldSet.getName() );
        formItemsSerializerJson.generate( visualFieldSet.getFormItems(), g );

        g.writeEndObject();
    }

    private void generateReference( final TemplateReference templateReference, final JsonGenerator g )
        throws IOException
    {
        g.writeStartObject();
        g.writeStringField( "formItemType", templateReference.getFormItemType().toString() );
        g.writeStringField( "path", templateReference.getPath().toString() );
        g.writeStringField( "name", templateReference.getName() );
        g.writeStringField( "reference", templateReference.getTemplateQualifiedName().toString() );
        g.writeStringField( "templateType", templateReference.getTemplateType().toString() );
        g.writeEndObject();
    }

    public FormItem parse( final JsonNode formItemNode )
    {
        FormItemType formItemType = FormItemType.valueOf( JsonParserUtil.getStringValue( "formItemType", formItemNode ) );

        FormItem formItem;

        if ( formItemType == FormItemType.COMPONENT )
        {
            formItem = parseField( formItemNode );
        }
        else if ( formItemType == FormItemType.FORM_ITEM_SET )
        {
            formItem = parseFormItemSet( formItemNode );
        }
        else if ( formItemType == FormItemType.VISUAL_FIELD_SET )
        {
            formItem = parseVisualFieldSet( formItemNode );
        }
        else if ( formItemType == FormItemType.REFERENCE )
        {
            formItem = parseTemplateReference( formItemNode );
        }
        else
        {
            throw new JsonParsingException( "Unknown FormItemType: " + formItemType );
        }

        if ( formItem instanceof DirectAccessibleFormItem )
        {
            applyPath( formItemNode, (DirectAccessibleFormItem) formItem );
        }
        return formItem;
    }

    private void applyPath( final JsonNode formItemNode, final DirectAccessibleFormItem formItem )
    {
        if ( formItem.getFormItemType() != FormItemType.VISUAL_FIELD_SET )
        {
            formItem.setPath( new FormItemPath( JsonParserUtil.getStringValue( "path", formItemNode ) ) );
        }
    }

    private DirectAccessibleFormItem parseField( final JsonNode formItemNode )
    {
        final Component.Builder builder = Component.newBuilder();
        builder.name( JsonParserUtil.getStringValue( "name", formItemNode ) );
        builder.label( JsonParserUtil.getStringValue( "label", formItemNode, null ) );
        builder.immutable( JsonParserUtil.getBooleanValue( "immutable", formItemNode ) );
        builder.helpText( JsonParserUtil.getStringValue( "helpText", formItemNode ) );
        builder.customText( JsonParserUtil.getStringValue( "customText", formItemNode ) );

        parseOccurrences( builder, formItemNode.get( "occurrences" ) );
        parseComponentType( builder, formItemNode.get( "componentType" ) );
        parseComponentTypeConfig( builder, formItemNode.get( "componentTypeConfig" ) );

        return builder.build();
    }

    private DirectAccessibleFormItem parseFormItemSet( final JsonNode formItemNode )
    {
        final FormItemSet.Builder builder = newFormItemTest();
        builder.name( JsonParserUtil.getStringValue( "name", formItemNode ) );
        builder.label( JsonParserUtil.getStringValue( "label", formItemNode, null ) );
        builder.immutable( JsonParserUtil.getBooleanValue( "immutable", formItemNode ) );
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

    private FormItem parseVisualFieldSet( final JsonNode formItemNode )
    {
        final VisualFieldSet.Builder builder = newVisualFieldSet();
        builder.label( JsonParserUtil.getStringValue( "label", formItemNode, null ) );
        builder.name( JsonParserUtil.getStringValue( "name", formItemNode, null ) );

        final FormItems formItems = formItemsSerializerJson.parse( formItemNode.get( "items" ) );
        for ( FormItem formItem : formItems.iterable() )
        {
            builder.add( formItem );
        }

        return builder.build();
    }

    private DirectAccessibleFormItem parseTemplateReference( final JsonNode formItemNode )
    {
        final TemplateReference.Builder builder = newTemplateReference();
        builder.name( JsonParserUtil.getStringValue( "name", formItemNode ) );
        builder.template( new TemplateQualifiedName( JsonParserUtil.getStringValue( "reference", formItemNode ) ) );
        builder.type( TemplateType.valueOf( JsonParserUtil.getStringValue( "templateType", formItemNode ) ) );
        return builder.build();
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
            ComponentType componentType = componentTypeSerializer.parse( componentTypeNode );
            builder.type( componentType );
        }
    }
}
