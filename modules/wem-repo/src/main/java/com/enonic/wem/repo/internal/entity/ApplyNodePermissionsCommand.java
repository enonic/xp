package com.enonic.wem.repo.internal.entity;

import java.time.Instant;

import org.apache.commons.lang.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;

import com.enonic.wem.api.node.ApplyNodePermissionsParams;
import com.enonic.wem.api.node.FindNodesByParentParams;
import com.enonic.wem.api.node.Node;
import com.enonic.wem.api.node.Nodes;
import com.enonic.wem.api.security.PrincipalKey;
import com.enonic.wem.api.security.acl.AccessControlEntry;
import com.enonic.wem.api.security.acl.AccessControlList;
import com.enonic.wem.api.security.acl.Permission;
import com.enonic.wem.repo.internal.index.query.QueryService;

final class ApplyNodePermissionsCommand
    extends AbstractNodeCommand
{
    private final static Logger LOG = LoggerFactory.getLogger( ApplyNodePermissionsCommand.class );

    private final ApplyNodePermissionsParams params;

    private ApplyNodePermissionsCommand( final Builder builder )
    {
        super( builder );
        this.params = builder.params;
    }

    public int execute()
    {
        final Node node = doGetById( params.getNodeId(), false );
        if ( node == null )
        {
            return 0;
        }

        final StopWatch stopWatch = new StopWatch();

        LOG.info( "Applying permissions to descendants of node [" + node.id() + "] " + node.path() );
        stopWatch.start();
        final int appliedNodeCount = applyPermissionsToChildren( node );
        stopWatch.stop();
        LOG.info( "Permissions applied to " + appliedNodeCount + " nodes. Total time: " + stopWatch.toString() );

        return appliedNodeCount;
    }

    private int applyPermissionsToChildren( final Node parent )
    {
        final AccessControlList parentPermissions = parent.getPermissions();

        final FindNodesByParentParams findByParentParams = FindNodesByParentParams.create().
            parentPath( parent.path() ).
            size( QueryService.GET_ALL_SIZE_FLAG ).
            build();
        final Nodes children = doFindNodesByParent( findByParentParams ).getNodes();

        int appliedNodeCount = 0;
        for ( Node child : children )
        {
            final Node childApplied = applyNodePermissions( parentPermissions, child );
            appliedNodeCount++;
            appliedNodeCount += applyPermissionsToChildren( childApplied );
        }

        return appliedNodeCount;
    }

    private Node applyNodePermissions( final AccessControlList parentPermissions, final Node node )
    {
        LOG.info( "Applying permissions to node [" + node.id() + "] " + node.path() );
        final Node updatedNode;
        if ( params.isOverwriteChildPermissions() || node.inheritsPermissions() )
        {
            updatedNode = createUpdatedNode( node, parentPermissions, true );
        }
        else
        {
            final AccessControlList mergedPermissions = mergePermissions( node.getPermissions(), parentPermissions );
            updatedNode = createUpdatedNode( node, mergedPermissions, false );
        }

        doStoreNode( updatedNode );
        return updatedNode;
    }

    private Node createUpdatedNode( final Node persistedNode, final AccessControlList permissions, final boolean inheritsPermissions )
    {
        final Node.Builder updateNodeBuilder = Node.newNode( persistedNode ).
            modifiedTime( Instant.now() ).
            modifier( params.getModifier() ).
            permissions( permissions ).
            inheritPermissions( inheritsPermissions );
        return updateNodeBuilder.build();
    }

    public AccessControlList mergePermissions( final AccessControlList childAcl, final AccessControlList parentAcl )
    {
        final AccessControlList.Builder effective = AccessControlList.create();
        // apply parent entries
        for ( AccessControlEntry parentEntry : parentAcl )
        {
            final PrincipalKey principal = parentEntry.getPrincipal();
            if ( childAcl.contains( principal ) )
            {
                final AccessControlEntry childEntry = childAcl.getEntry( principal );
                final AccessControlEntry mergedEntry = mergeAccessControlEntries( childEntry, parentEntry );
                effective.add( mergedEntry );
            }
            else
            {
                effective.add( parentEntry );
            }
        }

        // apply child entries not in parent
        for ( AccessControlEntry childEntry : childAcl )
        {
            if ( !parentAcl.contains( childEntry.getPrincipal() ) )
            {
                effective.add( childEntry );
            }
        }

        return effective.build();
    }

    private AccessControlEntry mergeAccessControlEntries( final AccessControlEntry childEntry, final AccessControlEntry parentEntry )
    {
        final AccessControlEntry.Builder entry = AccessControlEntry.create().principal( childEntry.getPrincipal() );
        for ( Permission permission : Permission.values() )
        {
            if ( childEntry.isSet( permission ) )
            {
                // set effective permission from child
                if ( childEntry.isAllowed( permission ) )
                {
                    entry.allow( permission );
                }
                else
                {
                    entry.deny( permission );
                }
            }
            else if ( parentEntry.isSet( permission ) )
            {
                // inherit permission from parent
                if ( parentEntry.isAllowed( permission ) )
                {
                    entry.allow( permission );
                }
                else
                {
                    entry.deny( permission );
                }
            }
        }
        return entry.build();
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static class Builder
        extends AbstractNodeCommand.Builder<Builder>
    {
        private ApplyNodePermissionsParams params;

        Builder()
        {
            super();
        }

        public Builder params( final ApplyNodePermissionsParams params )
        {
            this.params = params;
            return this;
        }

        public ApplyNodePermissionsCommand build()
        {
            validate();
            return new ApplyNodePermissionsCommand( this );
        }

        void validate()
        {
            Preconditions.checkNotNull( params );
        }
    }

}
