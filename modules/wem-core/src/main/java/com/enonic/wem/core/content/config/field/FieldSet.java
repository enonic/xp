package com.enonic.wem.core.content.config.field;

import java.util.List;

public class FieldSet
    extends ConfigItem
{
    private String displayName;

    private List<Field> fields;

    protected FieldSet()
    {
        super( ConfigType.FIELD_SET );
    }

    @Override
    ConfigItemJsonGenerator getJsonGenerator()
    {
        return null;
    }
}
