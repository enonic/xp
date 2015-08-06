package com.enonic.xp.form.inputtype;

import org.w3c.dom.Element;

import com.fasterxml.jackson.databind.node.ObjectNode;

import com.enonic.xp.app.ApplicationKey;

public abstract class ConfigurableInputType<T extends InputTypeConfig>
    extends InputType
{
    private final InputTypeConfigSerializer<T> serializer;

    public ConfigurableInputType( final InputTypeName name, final InputTypeConfigSerializer<T> serializer )
    {
        super( name );
        this.serializer = serializer;
    }

    public T getDefaultConfig()
    {
        return null;
    }

    public final T parseConfig( final ApplicationKey app, final Element elem )
    {
        return this.serializer.parseConfig( app, elem );
    }

    public final ObjectNode serializeConfig( final T config )
    {
        return this.serializer.serializeConfig( config );
    }
}
