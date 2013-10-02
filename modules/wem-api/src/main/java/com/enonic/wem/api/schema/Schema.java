package com.enonic.wem.api.schema;


import org.joda.time.DateTime;

import com.enonic.wem.api.content.ModuleBasedQualifiedName;
import com.enonic.wem.api.module.ModuleName;
import com.enonic.wem.api.support.ChangeTraceable;

public interface Schema
    extends ChangeTraceable
{
    SchemaKey getSchemaKey();

    String getName();

    ModuleBasedQualifiedName getQualifiedName();

    String getDisplayName();

    ModuleName getModuleName();

    DateTime getCreatedTime();

    DateTime getModifiedTime();

}
