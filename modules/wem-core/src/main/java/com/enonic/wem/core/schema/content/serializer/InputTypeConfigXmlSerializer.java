package com.enonic.wem.core.schema.content.serializer;

import org.jdom.Element;

import com.google.common.base.Preconditions;

import com.enonic.wem.api.form.inputtype.AbstractInputTypeConfigXmlSerializer;
import com.enonic.wem.api.form.inputtype.InputTypeConfig;

public class InputTypeConfigXmlSerializer
{
    public InputTypeConfig parse( final Element inputEl, final Class inputTypeClass )
    {
        Preconditions.checkNotNull( inputEl, "inputEl cannot be null" );
        Preconditions.checkNotNull( inputTypeClass, "inputTypeClass cannot be null" );

        final Element inputTypeConfigEl = inputEl.getChild( "config" );
        if ( inputTypeConfigEl == null )
        {
            return null;
        }
        final String className = inputTypeClass.getPackage().getName() + "." + inputTypeClass.getSimpleName() + "Config";
        final String serializerClassName = className + "XmlSerializer";

        AbstractInputTypeConfigXmlSerializer parser = instantiateInputTypeConfigXmlParser( serializerClassName );
        return parser.parseConfig( inputTypeConfigEl );
    }

    private AbstractInputTypeConfigXmlSerializer instantiateInputTypeConfigXmlParser( String className )
    {
        try
        {
            Class cls = Class.forName( className );

            return (AbstractInputTypeConfigXmlSerializer) cls.newInstance();
        }
        catch ( ClassNotFoundException | InstantiationException | IllegalAccessException e )
        {
            throw new IllegalArgumentException( "Failed to instantiate AbstractInputTypeConfigXmlSerializer: " + className, e );
        }
    }
}
