package com.enonic.xp.inputtype;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public final class InputTypeNotFoundException
    extends RuntimeException
{
    public InputTypeNotFoundException( final InputTypeName name )
    {
        super( "Input type [" + name + "] not found" );
    }
}
