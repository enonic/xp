package com.enonic.wem.api.content.type.component.inputtype;


import com.enonic.wem.api.content.data.Data;
import com.enonic.wem.api.content.type.component.BreaksRequiredContractException;

/**
 * Common interface for all kinds of input types.
 */
public interface InputType
{
    String getName();

    boolean isBuiltIn();

    boolean requiresConfig();

    Class requiredConfigClass();

    AbstractInputTypeConfigSerializerJson getInputTypeConfigJsonGenerator();

    AbstractInputTypeConfigSerializerXml getInputTypeConfigXmlGenerator();

    void checkBreaksRequiredContract( Data data )
        throws BreaksRequiredContractException;

    void ensureType( Data data );
}
