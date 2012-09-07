package com.enonic.wem.core.content.type.formitem.comptype;


import com.enonic.wem.core.content.data.Data;
import com.enonic.wem.core.content.datatype.DataType;
import com.enonic.wem.core.content.type.formitem.BreaksRequiredContractException;

/**
 * Common interface for all kinds of Component types.
 */
public interface ComponentType
{
    String getName();

    String getClassName();

    DataType getDataType();

    boolean requiresConfig();

    Class requiredConfigClass();

    AbstractComponentTypeConfigSerializerJson getComponentTypeConfigJsonGenerator();

    void checkBreaksRequiredContract( Data data )
        throws BreaksRequiredContractException;

    void ensureType( Data data );
}
