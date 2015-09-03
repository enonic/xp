package com.enonic.wem.repo.internal.entity;

import com.enonic.wem.repo.internal.InternalContext;
import com.enonic.wem.repo.internal.entity.dao.NodeDao;
import com.enonic.wem.repo.internal.storage.branch.NodeBranchVersion;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.index.ChildOrder;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodePath;

public class NodeChildOrderResolver
    extends AbstractNodeCommand
{
    private final NodePath parentPath;

    private final ChildOrder childOrder;

    private NodeChildOrderResolver( final Builder builder )
    {
        super( builder );
        parentPath = builder.nodePath;
        childOrder = builder.childOrder;
    }

    public ChildOrder resolve()
    {
        return NodeHelper.runAsAdmin( this::doResolve );
    }

    private ChildOrder doResolve()
    {
        if ( this.childOrder != null && !this.childOrder.isEmpty() )
        {
            return this.childOrder;
        }

        final NodeBranchVersion parentVersion =
            this.branchService.get( this.parentPath, InternalContext.from( ContextAccessor.current() ) );

        if ( parentVersion == null )
        {
            return ChildOrder.defaultOrder();
        }

        final Node parentNode = this.nodeDao.getByVersionId( parentVersion.getVersionId() );

        return parentNode.getChildOrder();
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static Builder create( final AbstractNodeCommand source )
    {
        return new Builder( source );
    }

    public static final class Builder
        extends AbstractNodeCommand.Builder<Builder>
    {
        private NodePath nodePath;

        private ChildOrder childOrder;

        private Builder()
        {
        }

        private Builder( final AbstractNodeCommand source )
        {
            super( source );
        }

        @Override
        public Builder nodeDao( final NodeDao nodeDao )
        {
            this.nodeDao = nodeDao;
            return this;
        }

        public Builder nodePath( final NodePath nodePath )
        {
            this.nodePath = nodePath;
            return this;
        }

        public Builder childOrder( final ChildOrder childOrder )
        {
            this.childOrder = childOrder;
            return this;
        }

        public NodeChildOrderResolver build()
        {
            return new NodeChildOrderResolver( this );
        }
    }
}
