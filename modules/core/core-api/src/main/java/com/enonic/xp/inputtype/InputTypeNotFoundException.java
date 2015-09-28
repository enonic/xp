package com.enonic.xp.inputtype;

public final class InputTypeNotFoundException
    extends RuntimeException
{
    public InputTypeNotFoundException( final InputTypeName name )
    {
        super( "Input type [" + name + "] not found" );
    }
}
