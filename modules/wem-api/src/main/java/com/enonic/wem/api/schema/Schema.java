package com.enonic.wem.api.schema;


import org.joda.time.DateTime;

import com.enonic.wem.api.support.ChangeTraceable;

public interface Schema
    extends ChangeTraceable
{
    SchemaKey getSchemaKey();

    SchemaName getName();

    String getDisplayName();

    String getDescription();

    DateTime getCreatedTime();

    DateTime getModifiedTime();

    boolean hasChildren();

    SchemaIcon getIcon();

}
