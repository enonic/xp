package com.enonic.wem.api.schema;


import org.joda.time.Instant;

import com.enonic.wem.api.support.ChangeTraceable;

public interface Schema
    extends ChangeTraceable
{
    SchemaKey getSchemaKey();

    SchemaName getName();

    String getDisplayName();

    String getDescription();

    Instant getCreatedTime();

    Instant getModifiedTime();

    boolean hasChildren();

    SchemaIcon getIcon();

}
