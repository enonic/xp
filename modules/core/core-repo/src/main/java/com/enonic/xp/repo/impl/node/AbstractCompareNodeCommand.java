package com.enonic.xp.repo.impl.node;

import java.util.Objects;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.repo.impl.storage.NodeStorageService;

class AbstractCompareNodeCommand
{
    final Branch target;

    final NodeStorageService nodeStorageService;

    AbstractCompareNodeCommand( Builder builder )
    {
        target = builder.target;
        this.nodeStorageService = builder.nodeStorageService;
    }


    public static class Builder<B extends Builder>
    {
        private Branch target;

        private NodeStorageService nodeStorageService;

        @SuppressWarnings("unchecked")
        public B target( final Branch target )
        {
            this.target = target;
            return (B) this;
        }

        @SuppressWarnings("unchecked")
        public B storageService( final NodeStorageService nodeStorageService )
        {
            this.nodeStorageService = nodeStorageService;
            return (B) this;
        }

        void validate()
        {
            Objects.requireNonNull( nodeStorageService );
            Objects.requireNonNull( target, "target is required" );
        }
    }
}
