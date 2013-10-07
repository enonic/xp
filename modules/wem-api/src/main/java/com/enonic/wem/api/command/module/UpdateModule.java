package com.enonic.wem.api.command.module;

import com.enonic.wem.api.command.Command;
import com.enonic.wem.api.module.ModuleVersion;

public final class UpdateModule
    extends Command<Boolean>
{
    private ModuleVersion module;

    private ModuleEditor editor;

    public UpdateModule module( final ModuleVersion module )
    {
        this.module = module;
        return this;
    }

    public UpdateModule editor( final ModuleEditor editor )
    {
        this.editor = editor;
        return this;
    }

    ModuleVersion getModule()
    {
        return module;
    }

    ModuleEditor getEditor()
    {
        return editor;
    }

    @Override
    public void validate()
    {
    }
}
