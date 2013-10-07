package com.enonic.wem.api.schema;


import org.joda.time.DateTime;

import com.enonic.wem.api.support.ChangeTraceable;
import com.enonic.wem.api.content.QualifiedName;

public interface Schema
    extends ChangeTraceable
{
    SchemaKey getSchemaKey();

    String getName();

    QualifiedName getQualifiedName();

    String getDisplayName();

    DateTime getCreatedTime();

    DateTime getModifiedTime();

}
