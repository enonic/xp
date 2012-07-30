package com.enonic.wem.core.content.type.configitem;


import java.io.IOException;

import org.codehaus.jackson.JsonGenerator;

public class FieldSetSerializerJson
    extends ConfigItemSerializerJson
{
    public static final FieldSetSerializerJson DEFAULT = new FieldSetSerializerJson();

    public void generate( ConfigItem configItem, JsonGenerator g )
        throws IOException
    {
        FieldSet fieldSet = (FieldSet) configItem;

        g.writeStartObject();
        g.writeStringField( "type", fieldSet.getItemType().toString() );
        g.writeStringField( "path", fieldSet.getPath().toString() );
        g.writeStringField( "name", fieldSet.getName() );
        g.writeStringField( "label", fieldSet.getLabel() );
        g.writeBooleanField( "required", fieldSet.isRequired() );
        g.writeBooleanField( "immutable", fieldSet.isImmutable() );
        MultipleSerializerJson.generate( fieldSet.getMultiple(), g );
        g.writeStringField( "customText", fieldSet.getCustomText() );
        g.writeStringField( "helpText", fieldSet.getHelpText() );
        ConfigItemsSerializerJson.generate( fieldSet.getConfigItems(), g );

        g.writeEndObject();
    }
}
