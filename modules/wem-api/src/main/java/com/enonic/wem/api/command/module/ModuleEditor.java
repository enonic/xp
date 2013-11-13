package com.enonic.wem.api.command.module;

import com.enonic.wem.api.module.Module;
import com.enonic.wem.api.support.Editor;

public interface ModuleEditor
    extends Editor<Module>
{
    /**
     * @param module to be edited
     * @return updated module, null if it has not been modified.
     */
    public Module edit( Module module );
}
