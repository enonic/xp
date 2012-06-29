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
        g.writeStringField( "path", field.getPath().toString() );
        field.getType().getJsonGenerator().generate( field.getType(), g );
        g.writeStringField( "name", field.getName() );
        g.writeStringField( "label", field.getLabel() );
        g.writeBooleanField( "required", field.isRequired() );
        g.writeBooleanField( "immutable", field.isImmutable() );
        MultipleJsonGenerator.generate( field.getMultiple(), g );
        g.writeBooleanField( "indexed", field.isIndexed() );
        g.writeStringField( "customText", field.getCustomText() );
        g.writeStringField( "validationRegexp", field.getValidationRegexp() );
        g.writeStringField( "helpText", field.getHelpText() );
        g.writeEndObject();
    }
}
