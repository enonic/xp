package com.enonic.xp.core.node;

import java.util.Map;

import com.google.common.collect.Maps;

public class NodesToBePublished
{
    private final Map<NodeId, Reason> nodesToBePublished;

    public NodesToBePublished()
    {
        this.nodesToBePublished = Maps.newHashMap();
    }

    public void requested( final NodeId nodeId )
    {
        this.nodesToBePublished.put( nodeId, new Requested() );
    }

    public void parentFor( final NodeId nodeId, final NodeId parentOf )
    {
        this.nodesToBePublished.put( nodeId, new ParentFor( parentOf ) );
    }

    public void referredFrom( final NodeId nodeId, final NodeId referredFrom )
    {
        this.nodesToBePublished.put( nodeId, new ReferredFrom( referredFrom ) );
    }


    public abstract static class Reason
    {
        public abstract String getMessage();
    }

    public static class Requested
        extends Reason
    {
        @Override
        public String getMessage()
        {
            return "";
        }
    }

    public static class ParentFor
        extends Reason
    {
        private final String message = "Parent for %s";

        private final NodeId nodeId;

        public ParentFor( final NodeId nodeId )
        {
            this.nodeId = nodeId;
        }

        @Override
        public String getMessage()
        {
            return String.format( message, nodeId.toString() );
        }
    }

    public static class ReferredFrom
        extends Reason
    {
        private final String message = "Referred from %s";

        private final NodeId nodeId;

        public ReferredFrom( final NodeId nodeId )
        {
            this.nodeId = nodeId;
        }

        @Override
        public String getMessage()
        {
            return String.format( message, nodeId.toString() );
        }
    }


}
