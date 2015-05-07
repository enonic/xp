package com.enonic.wem.repo.internal.entity;

import com.enonic.xp.node.NodeId;

public class ResolveContext
{
    private final NodeId contextNodeId;

    private boolean becauseParent = false;

    private boolean becauseChild = false;

    private boolean becauseReferredTo = false;

    public ResolveContext( final boolean becauseChild, final boolean becauseParent, final boolean becauseReferredTo,
                            final NodeId contextNodeId )
    {
        this.becauseChild = becauseChild;
        this.becauseParent = becauseParent;
        this.becauseReferredTo = becauseReferredTo;
        this.contextNodeId = contextNodeId;
    }

    static ResolveContext parentFor( final NodeId nodeId )
    {
        return new ResolveContext( false, true, false, nodeId );
    }

    static ResolveContext childOf( final NodeId nodeId )
    {
        return new ResolveContext( true, false, false, nodeId );
    }

    static ResolveContext referredFrom( final NodeId nodeId )
    {
        return new ResolveContext( false, false, true, nodeId );
    }

    static ResolveContext requested()
    {
        return new ResolveContext( false, false, false, null );
    }

    public NodeId getContextNodeId()
    {
        return contextNodeId;
    }

    public boolean isBecauseParent()
    {
        return becauseParent;
    }

    public boolean isBecauseChild()
    {
        return becauseChild;
    }

    public boolean isBecauseReferredTo()
    {
        return becauseReferredTo;
    }
}
