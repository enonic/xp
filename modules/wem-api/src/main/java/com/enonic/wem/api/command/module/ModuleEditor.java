package com.enonic.wem.api.command.module;

import com.enonic.wem.api.module.Module;

public interface ModuleEditor
{
    /**
     * @param module to be edited
     * @return updated module, null if it has not been modified.
     */
    public Module edit( Module module )
        throws Exception;
}
