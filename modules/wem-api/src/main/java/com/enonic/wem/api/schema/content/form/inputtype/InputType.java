package com.enonic.wem.api.schema.content.form.inputtype;


import com.enonic.wem.api.content.data.Property;
import com.enonic.wem.api.content.data.Value;
import com.enonic.wem.api.plugin.ext.Extension;
import com.enonic.wem.api.schema.content.form.BreaksRequiredContractException;

/**
 * Common interface for all kinds of input types.
 */
public interface InputType
    extends Extension
{
    String getName();

    boolean isBuiltIn();

    boolean requiresConfig();

    Class requiredConfigClass();

    AbstractInputTypeConfigJsonSerializer getInputTypeConfigJsonSerializer();

    AbstractInputTypeConfigXmlSerializer getInputTypeConfigXmlSerializer();

    void checkBreaksRequiredContract( Property property )
        throws BreaksRequiredContractException;

    Value newValue( String value );
}
