package com.enonic.wem.api.command.module;

import com.enonic.wem.api.module.Module;

public interface ModuleEditor
{
    public Module edit( Module module )
        throws Exception;
}
