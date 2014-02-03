package com.enonic.wem.api.module;

import com.google.common.base.Preconditions;

public final class UpdateModuleSpec
{
    private ModuleKey moduleKey;

    private ModuleEditor editor;

    public UpdateModuleSpec module( final ModuleKey moduleKey )
    {
        this.moduleKey = moduleKey;
        return this;
    }

    public UpdateModuleSpec editor( final ModuleEditor editor )
    {
        this.editor = editor;
        return this;
    }

    public ModuleKey getModuleKey()
    {
        return moduleKey;
    }

    public ModuleEditor getEditor()
    {
        return editor;
    }

    public void validate()
    {
        Preconditions.checkNotNull( this.moduleKey, "module key cannot be null" );
        Preconditions.checkNotNull( this.editor, "editor cannot be null" );
    }
}
