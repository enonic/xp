package com.enonic.xp.core.impl.dump.reader;

import java.io.IOException;

import com.google.common.io.LineProcessor;

import com.enonic.xp.node.NodeService;

public class DumpLineProcessor
    implements LineProcessor<EntryLoadResult>
{
    private EntryLoadResult result;

    private final NodeService nodeService;

    public DumpLineProcessor( final NodeService nodeService )
    {
        this.nodeService = nodeService;
    }

    @Override
    public boolean processLine( final String line )
        throws IOException
    {
        // Deserialize to DumpEntry

        return false;
    }

    @Override
    public EntryLoadResult getResult()
    {
        return result;
    }
}
