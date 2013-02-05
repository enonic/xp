package com.enonic.wem.api.content;


import org.joda.time.DateTime;

import com.enonic.wem.api.content.type.BaseTypeKey;
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
