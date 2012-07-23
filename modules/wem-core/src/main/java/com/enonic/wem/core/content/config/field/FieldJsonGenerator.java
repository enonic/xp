package com.enonic.wem.core.content.config.field;


import java.io.IOException;

import org.codehaus.jackson.JsonGenerator;

public class FieldJsonGenerator
    extends ConfigItemJsonGenerator
{
    public static final FieldJsonGenerator DEFAULT = new FieldJsonGenerator();

    @Override
    public void generate( final ConfigItem configItem, final JsonGenerator g )
        throws IOException
    {
        final Field field = (Field) configItem;

        g.writeStartObject();
        g.writeStringField( "type", field.getConfigType().toString() );
        g.writeStringField( "path", field.getPath().toString() );
        field.getFieldType().getJsonGenerator().generate( field.getFieldType(), g );
        g.writeStringField( "name", field.getName() );
        g.writeStringField( "label", field.getLabel() );
        g.writeBooleanField( "required", field.isRequired() );
        g.writeBooleanField( "immutable", field.isImmutable() );
        MultipleJsonGenerator.generate( field.getMultiple(), g );
        g.writeBooleanField( "indexed", field.isIndexed() );
        g.writeStringField( "customText", field.getCustomText() );
        g.writeStringField( "validationRegexp", field.getValidationRegexp() );
        g.writeStringField( "helpText", field.getHelpText() );
        if ( field.getFieldType().requiresConfig() && field.getFieldTypeConfig() != null )
        {
            g.writeFieldName( "fieldTypeConfig" );
            field.getFieldType().getFieldTypeConfigJsonGenerator().generateBase( field.getFieldTypeConfig(), g );
        }

        g.writeEndObject();
    }
}
