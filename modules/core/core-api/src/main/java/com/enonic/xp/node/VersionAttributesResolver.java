package com.enonic.xp.node;

import org.jspecify.annotations.Nullable;

import com.enonic.xp.branch.Branch;

@FunctionalInterface
public interface VersionAttributesResolver
{
    Attributes resolve( Node editedNode, @Nullable Node originalNode, Branch branch, @Nullable Attributes originalAttributes );

    static VersionAttributesResolver of( final Attributes attributes )
    {
        return ( _, _, _, _ ) -> attributes;
    }
}
