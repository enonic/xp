package com.enonic.xp.toolbox.repo;

import io.airlift.airline.Command;

@Command(name = "reindex", description = "Reindex content in search indices for the given repository and branches.")
public final class ReindexCommand
    extends RepoCommand
{
    @Override
    protected void execute()
        throws Exception
    {
    }
}
