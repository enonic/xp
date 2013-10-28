package com.enonic.wem.api.schema;


import org.joda.time.DateTime;

import com.enonic.wem.api.Name;
import com.enonic.wem.api.support.ChangeTraceable;

public interface Schema
    extends ChangeTraceable
{
    SchemaKey getSchemaKey();

    String getName();

    Name getQualifiedName();

    String getDisplayName();

    DateTime getCreatedTime();

    DateTime getModifiedTime();

    boolean hasChildren();

}
