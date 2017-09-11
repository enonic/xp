package com.enonic.xp.repo.impl.vacuum.binary;

import java.util.Set;

import com.enonic.xp.repo.impl.vacuum.EntryState;

class BinaryNodeStateResolver
{
    private final Set<String> usedBinaryReferences;

    public BinaryNodeStateResolver( final Set<String> usedBinaryReferences )
    {
        this.usedBinaryReferences = usedBinaryReferences;
    }

    public EntryState resolve( final String binaryRef )
    {
        if ( this.usedBinaryReferences.contains( binaryRef ) )
        {
            return EntryState.IN_USE;
        }

        return EntryState.NOT_IN_USE;
    }
}
