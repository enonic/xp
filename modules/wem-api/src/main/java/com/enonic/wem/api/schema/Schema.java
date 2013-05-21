package com.enonic.wem.api.schema;


import org.joda.time.DateTime;

import com.enonic.wem.api.content.ModuleBasedQualifiedName;
import com.enonic.wem.api.module.ModuleName;

public interface Schema
{
    SchemaKey getSchemaKey();

    String getName();

    ModuleBasedQualifiedName getQualifiedName();

    String getDisplayName();

    ModuleName getModuleName();

    DateTime getCreatedTime();

    DateTime getModifiedTime();

}
