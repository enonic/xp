package com.enonic.wem.api.command.content;


import com.google.common.base.Preconditions;

import com.enonic.wem.api.command.Command;

public final class GenerateContentName
    extends Command<String>
{

    private String displayName;

    public GenerateContentName displayName( final String displayName )
    {
        this.displayName = displayName;
        return this;
    }

    @Override
    public void validate()
    {
        Preconditions.checkNotNull( this.displayName, "Display name cannot be null" );
    }

    public String getDisplayName()
    {
        return displayName;
    }
}
