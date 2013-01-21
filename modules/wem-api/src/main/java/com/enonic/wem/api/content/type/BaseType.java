package com.enonic.wem.api.content.type;


import org.joda.time.DateTime;

import com.enonic.wem.api.content.ModuleBasedQualifiedName;
import com.enonic.wem.api.module.ModuleName;

public interface BaseType
{
    BaseTypeKey getBaseTypeKey();

    String getName();

    ModuleBasedQualifiedName getQualifiedName();

    String getDisplayName();

    ModuleName getModuleName();

    DateTime getCreatedTime();

    DateTime getModifiedTime();

}
