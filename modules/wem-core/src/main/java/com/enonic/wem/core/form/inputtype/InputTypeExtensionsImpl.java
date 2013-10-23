package com.enonic.wem.core.form.inputtype;

import java.util.LinkedHashMap;

import com.enonic.wem.api.form.inputtype.InputType;
import com.enonic.wem.api.form.inputtype.InputTypeExtension;
import com.enonic.wem.core.plugin.ext.ExtensionPoint;

public class InputTypeExtensionsImpl
    extends ExtensionPoint<InputTypeExtension>
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
        super( InputTypeExtension.class );
    }

    public InputType getInputType( final String name )
    {
        return this.inputTypeByName.get( name );
    }

    @Override
    protected synchronized void addExtension( final InputTypeExtension ext )
    {
        super.addExtension( ext );
        inputTypeByName.put( ext.getName(), ext );
    }

    @Override
    protected synchronized void removeExtension( final InputTypeExtension ext )
    {
        super.removeExtension( ext );
        inputTypeByName.remove( ext.getName() );
    }

    @Override
    public int compare( final InputTypeExtension inputTypeA, final InputTypeExtension inputTypeB )
    {
        return inputTypeA.getName().compareTo( inputTypeB.getName() );
    }
}
