package com.enonic.xp.repo.impl.node;

import java.util.concurrent.Callable;

import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.node.ApplyNodePermissionsParams;
import com.enonic.xp.node.BinaryAttachments;
import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.CreateRootNodeParams;
import com.enonic.xp.node.ImportNodeResult;
import com.enonic.xp.node.InsertManualStrategy;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.PatchNodeParams;
import com.enonic.xp.node.RefreshMode;
import com.enonic.xp.repo.impl.binary.BinaryService;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.acl.AccessControlList;
import com.enonic.xp.security.auth.AuthenticationInfo;

public class ImportNodeCommand
    extends AbstractNodeCommand
{
    private final InsertManualStrategy insertManualStrategy;

    private final BinaryAttachments binaryAttachments;

    private final Node importNode;

    private final BinaryService binaryService;

    private final boolean importPermissions;

    private final boolean importPermissionsOnCreate;

    private final RefreshMode refresh;

    private ImportNodeCommand( Builder builder )
    {
        super( builder );
        this.insertManualStrategy = builder.insertManualStrategy;
        this.binaryAttachments = builder.binaryAttachments;
        this.importNode = builder.importNode;
        this.binaryService = builder.binaryService;
        this.importPermissions = builder.importPermissions;
        this.importPermissionsOnCreate = builder.importPermissionsOnCreate;
        this.refresh = builder.refresh;

    }

    public static Builder create()
    {
        return new Builder();
    }

    public ImportNodeResult execute()
    {
        final boolean exists = CheckNodeExistsCommand.create( this )
            .nodePath( this.importNode.path() )
            .mode( CheckNodeExistsCommand.Mode.SPEED )
            .build()
            .execute();

        final Node node;
        if ( !exists )
        {
            node = createNode();
        }
        else
        {
            node = updateNode( doGetByPath( this.importNode.path() ) );
        }

        return ImportNodeResult.create().node( node ).preExisting( exists ).build();
    }

    private Node createNode()
    {
        final Node node;

        if ( this.importNode.isRoot() )
        {
            final CreateRootNodeParams.Builder createRootNodeParams =
                CreateRootNodeParams.create().childOrder( this.importNode.getChildOrder() );

            if ( this.importPermissionsOnCreate )
            {
                createRootNodeParams.permissions( this.importNode.getPermissions() );
            }

            node = CreateRootNodeCommand.create( this ).params( createRootNodeParams.build() ).build().execute();
        }
        else
        {
            PermissionsMergingStrategy mergingStrategy =
                this.importPermissionsOnCreate ? PermissionsMergingStrategy.MERGE : PermissionsMergingStrategy.OVERWRITE;

            final AccessControlList permissions =
                mergingStrategy.mergePermissions( this.importNode.getPermissions(), getParentPermissions( this.importNode ) );

            final CreateNodeParams createNodeParams = CreateNodeParams.create()
                .setNodeId( this.importNode.id() )
                .nodeType( this.importNode.getNodeType() )
                .childOrder( this.importNode.getChildOrder() )
                .setBinaryAttachments( this.binaryAttachments )
                .data( this.importNode.data() )
                .indexConfigDocument( this.importNode.getIndexConfigDocument() )
                .insertManualStrategy( this.insertManualStrategy )
                .manualOrderValue( this.importNode.getManualOrderValue() )
                .name( this.importNode.name() )
                .parent( this.importNode.parentPath() )
                .permissions( permissions )
                .build();

            node = CreateNodeCommand.create( this )
                .params( createNodeParams )
                .timestamp( this.importNode.getTimestamp() )
                .binaryService( binaryService )
                .build()
                .execute();
        }
        refresh( refresh );
        return node;
    }

    private Node updateNode( final Node existingNode )
    {
        final PatchNodeParams updateNodeParams = PatchNodeParams.create()
            .id( existingNode.id() )
            .setBinaryAttachments( this.binaryAttachments )
            .editor( editableNode -> {
                editableNode.data = this.importNode.data();
            } )
            .refresh( RefreshMode.ALL )
            .build();

        final Node updatedNode = PatchNodeCommand.create()
            .params( updateNodeParams ).binaryService( this.binaryService )
            .indexServiceInternal( this.indexServiceInternal )
            .storageService( this.nodeStorageService )
            .searchService( this.nodeSearchService )
            .build()
            .execute()
            .getResult( ContextAccessor.current().getBranch() );

        if ( this.importPermissions )
        {
            return ApplyNodePermissionsCommand.create( this )
                .params( ApplyNodePermissionsParams.create()
                             .nodeId( existingNode.id() )
                             .permissions( PermissionsMergingStrategy.MERGE.mergePermissions( this.importNode.getPermissions(),
                                                                                              getParentPermissions( this.importNode ) ) )
                             .build() )
                .build()
                .execute().getResults()
                .get( existingNode.id() )
                .stream()
                .filter( branchResult -> ContextAccessor.current().getBranch().equals( branchResult.branch() ) )
                .findAny()
                .map( br -> br.permissions() != null ? Node.create( updatedNode ).permissions( br.permissions() ).build() : updatedNode )
                .orElse( updatedNode );
        }

        return updatedNode;
    }

    private AccessControlList getParentPermissions( final Node node )
    {
        final Node parentNode = node.parentPath() != null ? callAsAdmin( () -> doGetByPath( node.parentPath() ) ) : null;
        return parentNode != null ? parentNode.getPermissions() : AccessControlList.empty();
    }

    private <T> T callAsAdmin( final Callable<T> callable )
    {
        final Context context = ContextAccessor.current();

        final AuthenticationInfo authenticationInfo =
            AuthenticationInfo.copyOf( context.getAuthInfo() ).principals( RoleKeys.ADMIN ).build();

        return ContextBuilder.from( context ).authInfo( authenticationInfo ).build().callWith( callable );
    }

    public static final class Builder
        extends AbstractNodeCommand.Builder<Builder>
    {
        private InsertManualStrategy insertManualStrategy;

        private BinaryAttachments binaryAttachments;

        private Node importNode;

        private BinaryService binaryService;

        private boolean importPermissions;

        private boolean importPermissionsOnCreate = true;

        private RefreshMode refresh;

        private Builder()
        {
        }

        public Builder insertManualStrategy( InsertManualStrategy insertManualStrategy )
        {
            this.insertManualStrategy = insertManualStrategy;
            return this;
        }

        public Builder binaryAttachments( BinaryAttachments binaryAttachments )
        {
            this.binaryAttachments = binaryAttachments;
            return this;
        }

        public Builder importNode( Node importNode )
        {
            this.importNode = importNode;
            return this;
        }

        public Builder binaryBlobStore( BinaryService binaryService )
        {
            this.binaryService = binaryService;
            return this;
        }

        public Builder importPermissions( boolean importPermissions )
        {
            this.importPermissions = importPermissions;
            return this;
        }

        public Builder importPermissionsOnCreate( boolean importPermissionsOnCreate )
        {
            this.importPermissionsOnCreate = importPermissionsOnCreate;
            return this;
        }

        public Builder refresh( final RefreshMode refresh )
        {
            this.refresh = refresh;
            return this;
        }

        public ImportNodeCommand build()
        {
            return new ImportNodeCommand( this );
        }
    }
}
