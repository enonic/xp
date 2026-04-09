package com.enonic.xp.node;

import com.enonic.xp.branch.Branch;

@FunctionalInterface
public interface VersionAttributesResolver
{
    Attributes resolve( Node originalNode, Node editedNode, Branch branch );

    static VersionAttributesResolver of( final Attributes attributes )
    {
        return ( _, _, _ ) -> attributes;
    }
}
