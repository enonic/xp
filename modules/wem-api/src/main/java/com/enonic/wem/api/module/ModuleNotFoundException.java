package com.enonic.wem.api.module;

import com.enonic.wem.api.exception.BaseException;

public class ModuleNotFoundException
    extends BaseException
{
    public ModuleNotFoundException( final ModuleKey moduleKey )
    {
        super( "Module [{0}] was not found", moduleKey );
    }
}
