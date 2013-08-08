package com.enonic.wem.api.schema.content.form.inputtype;


import com.enonic.wem.api.content.data.Property;
import com.enonic.wem.api.content.data.Value;
import com.enonic.wem.api.schema.content.form.BreaksRequiredContractException;
import com.enonic.wem.api.schema.content.form.inputtype.config.AbstractInputTypeConfigJsonGenerator;

/**
 * Common interface for all kinds of input types.
 */
public interface InputType
{
    String getName();

    boolean isBuiltIn();

    boolean requiresConfig();

    Class requiredConfigClass();

    AbstractInputTypeConfigJsonSerializer getInputTypeConfigJsonSerializer();

    AbstractInputTypeConfigJsonGenerator getInputTypeConfigJsonGenerator();

    AbstractInputTypeConfigXmlSerializer getInputTypeConfigXmlGenerator();

    void checkBreaksRequiredContract( Property property )
        throws BreaksRequiredContractException;

    Value newValue( String value );
}
