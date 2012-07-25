package com.enonic.wem.core.content.type.configitem;

import java.util.List;

/**
 *
 */
public class FieldSet
    extends ConfigItem
{
    private String displayName;

    private List<ConfigItem> fields;

    protected FieldSet()
    {
        super( ConfigItemType.FIELD_SET );
    }

    @Override
    ConfigItemSerializerJson getJsonGenerator()
    {
        return null;
    }
}
