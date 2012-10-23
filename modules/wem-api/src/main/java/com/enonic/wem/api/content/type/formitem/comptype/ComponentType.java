package com.enonic.wem.api.content.type.formitem.comptype;


import com.enonic.wem.api.content.data.Data;
import com.enonic.wem.api.content.type.formitem.BreaksRequiredContractException;

/**
 * Common interface for all kinds of Component types.
 */
public interface ComponentType
{
    String getName();

    String getClassName();

    boolean requiresConfig();

    Class requiredConfigClass();

    AbstractComponentTypeConfigSerializerJson getComponentTypeConfigJsonGenerator();

    AbstractComponentTypeConfigSerializerXml getComponentTypeConfigXmlGenerator();

    void checkBreaksRequiredContract( Data data )
        throws BreaksRequiredContractException;

    void ensureType( Data data );
}
