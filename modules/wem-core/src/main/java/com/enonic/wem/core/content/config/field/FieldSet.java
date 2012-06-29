package com.enonic.wem.core.content.config.field;

import java.util.List;

public class FieldSet
    extends ConfigItem
{
    private String displayName;

    private List<Field> fields;

    @Override
    ConfigItemJsonGenerator getJsonGenerator()
    {
        return null;
    }
}
