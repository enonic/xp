package com.enonic.xp.core.impl.project.init;

import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.archive.ArchiveConstants;
import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.content.ContentPropertyNames;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.data.PropertySet;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.RefreshMode;
import com.enonic.xp.schema.content.ContentTypeName;


public class ArchiveInitializer
    extends RepoDependentInitializer
{
    private static final Logger LOG = LoggerFactory.getLogger( ArchiveInitializer.class );

    private ArchiveInitializer( final Builder builder )
    {
        super( builder );
    }

    public static Builder create()
    {
        return new Builder();
    }

    @Override
    protected void doInitialize()
    {
        createAdminContext( ContentConstants.BRANCH_DRAFT ).runWith( this::initArchiveNode );
    }

    @Override
    public boolean isInitialized()
    {
        return createAdminContext( ContentConstants.BRANCH_DRAFT ).
            callWith( () -> nodeService.getByPath( ArchiveConstants.ARCHIVE_ROOT_PATH ) != null );
    }

    @Override
    protected String getInitializationSubject()
    {
        return repositoryId + " repo [archive] layout";
    }

    private void initArchiveNode()
    {
        LOG.info( "Archive root-node not found, creating" );

        final PropertyTree data = new PropertyTree();
        data.setString( ContentPropertyNames.TYPE, ContentTypeName.folder().toString() );
        data.setSet( ContentPropertyNames.DATA, new PropertySet() );
        data.setString( ContentPropertyNames.CREATOR, ContextAccessor.current().getAuthInfo().getUser().toString() );
        data.setString( ContentPropertyNames.MODIFIER, ContextAccessor.current().getAuthInfo().getUser().toString() );

        final Node archiveRoot = nodeService.create( CreateNodeParams.create().
            data( data ).
            name( ArchiveConstants.ARCHIVE_ROOT_NAME ).
            parent( NodePath.ROOT ).
            permissions( Objects.requireNonNullElse( accessControlList, ArchiveConstants.ARCHIVE_ROOT_DEFAULT_ACL ) ).
            childOrder( ArchiveConstants.DEFAULT_ARCHIVE_REPO_ROOT_ORDER ).
            build() );

        LOG.info( "Created archive root-node: " + archiveRoot.path() );

        nodeService.refresh( RefreshMode.ALL );

//        nodeService.push( NodeIds.from( archiveRoot.id() ), ContentConstants.BRANCH_MASTER );
    }

    public static class Builder
        extends RepoDependentInitializer.Builder<Builder>
    {
        public ArchiveInitializer build()
        {
            validate();
            return new ArchiveInitializer( this );
        }
    }
}
