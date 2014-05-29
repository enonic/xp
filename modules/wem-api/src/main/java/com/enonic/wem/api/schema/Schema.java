package com.enonic.wem.api.schema;


import java.time.Instant;

import com.enonic.wem.api.Icon;
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

    Icon getIcon();

}
