package com.enonic.wem.api.command;


import com.enonic.wem.api.exception.BaseException;

public class CommandHandlerNotFoundException
    extends BaseException
{
    public CommandHandlerNotFoundException( final Class type )
    {
        super( "Handle for command [{0}] not found", type.getName() );
    }
}
