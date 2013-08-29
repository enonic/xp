package com.enonic.wem.core.schema.content.form.inputtype;

import java.util.LinkedHashMap;

import com.enonic.wem.api.schema.content.form.inputtype.InputType;
import com.enonic.wem.core.plugin.ext.ExtensionPoint;

public class InputTypeExtensionsImpl
    extends ExtensionPoint<InputType>
    implements InputTypeExtensions
{
    private static final InputTypeExtensionsImpl instance = new InputTypeExtensionsImpl();

    private final LinkedHashMap<String, InputType> inputTypeByName = new LinkedHashMap<>();

    public static InputTypeExtensionsImpl get()
    {
        return instance;
    }

    public InputTypeExtensionsImpl()
    {
        super( InputType.class );
    }

    public InputType getInputType( final String name )
    {
        return this.inputTypeByName.get( name );
    }

    @Override
    protected synchronized void addExtension( final InputType ext )
    {
        super.addExtension( ext );
        inputTypeByName.put( ext.getName(), ext );
    }

    @Override
    protected synchronized void removeExtension( final InputType ext )
    {
        super.removeExtension( ext );
        inputTypeByName.remove( ext.getName() );
    }

    @Override
    public int compare( final InputType inputTypeA, final InputType inputTypeB )
    {
        return inputTypeA.getName().compareTo( inputTypeB.getName() );
    }
}
