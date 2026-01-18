package com.enonic.xp.core.impl.issue;

import java.time.Instant;
import java.util.Objects;

import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.core.impl.issue.serializer.IssueDataSerializer;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.index.IndexConfigDocument;
import com.enonic.xp.issue.CreateIssueParams;
import com.enonic.xp.issue.Issue;
import com.enonic.xp.issue.IssueAlreadyExistsException;
import com.enonic.xp.issue.IssueConstants;
import com.enonic.xp.issue.IssueName;
import com.enonic.xp.issue.IssueQuery;
import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.FindNodesByQueryResult;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeAlreadyExistAtPathException;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeName;
import com.enonic.xp.node.NodeQuery;
import com.enonic.xp.node.RefreshMode;
import com.enonic.xp.security.User;

import static com.enonic.xp.issue.IssuePropertyNames.CREATED_TIME;
import static com.enonic.xp.issue.IssuePropertyNames.CREATOR;
import static com.enonic.xp.issue.IssuePropertyNames.INDEX;
import static com.enonic.xp.issue.IssuePropertyNames.MODIFIED_TIME;

public class CreateIssueCommand
    extends AbstractIssueCommand
{
    private final CreateIssueParams params;

    private CreateIssueCommand( Builder builder )
    {
        super( builder );
        this.params = builder.params;
    }

    public Issue execute()
    {
        return doExecute();
    }

    private Issue doExecute()
    {
        validateBlockingChecks();

        final long index = countTotalIssues() + 1;

        final IssueName issueName = IssueNameFactory.create( index );

        final CreateNodeParams createNodeParams = CreateNodeParamsFactory.create( this.params, this.getCurrentUser(), index, issueName );

        final Node createdNode;
        try
        {
            createdNode = nodeService.create( createNodeParams );
        }
        catch ( NodeAlreadyExistAtPathException e )
        {
            throw new IssueAlreadyExistsException( issueName );
        }

        return IssueNodeTranslator.fromNode( createdNode );
    }

    private void validateBlockingChecks()
    {
        if ( params.getTitle() == null )
        {
            throw new IllegalArgumentException( "Issue title can not be null." );
        }

        if ( params.getStatus() == null )
        {
            throw new IllegalArgumentException( "Issue status can not be null." );
        }
    }

    User getCurrentUser()
    {
        return Objects.requireNonNullElseGet( ContextAccessor.current().getAuthInfo().getUser(), User::anonymous );
    }

    private long countTotalIssues()
    {
        final IssueQuery query = IssueQuery.create().size( 0 ).count( true ).build();

        final NodeQuery nodeQuery = IssueQueryNodeQueryTranslator.translate( query );

        final FindNodesByQueryResult result = nodeService.findByQuery( nodeQuery );

        return result.getTotalHits();
    }

    public static Builder create()
    {
        return new Builder();
    }

    static class Builder
        extends AbstractIssueCommand.Builder<Builder>
    {

        private CreateIssueParams params;

        public Builder params( final CreateIssueParams params )
        {
            this.params = params;
            return this;
        }

        private Builder()
        {
        }

        public CreateIssueCommand build()
        {
            return new CreateIssueCommand( this );
        }
    }

    private static class CreateNodeParamsFactory
    {

        private static final IssueDataSerializer ISSUE_DATA_SERIALIZER = new IssueDataSerializer();

        public static CreateNodeParams create( final CreateIssueParams params, final User creator, final long index,
                                               final IssueName issueName )
        {
            final Instant now = Instant.now();
            final PropertyTree contentAsData = ISSUE_DATA_SERIALIZER.toCreateNodeData( params );

            contentAsData.getRoot().ifNotNull().addInstant( CREATED_TIME, now );
            contentAsData.getRoot().ifNotNull().addInstant( MODIFIED_TIME, now );
            contentAsData.getRoot().ifNotNull().addString( CREATOR, creator.getKey().toString() );
            contentAsData.getRoot().ifNotNull().addLong( INDEX, index );

            final IndexConfigDocument indexConfigDocument = IssueIndexConfigFactory.create();

            final CreateNodeParams.Builder builder = CreateNodeParams.create()
                .setNodeId( NodeId.from( params.getId() ) )
                .name( NodeName.from( issueName ) )
                .parent( IssueConstants.ISSUE_ROOT_PATH )
                .data( contentAsData )
                .indexConfigDocument( indexConfigDocument )
                .inheritPermissions( true )
                .childOrder( IssueConstants.DEFAULT_CHILD_ORDER )
                .nodeType( IssueConstants.ISSUE_NODE_COLLECTION )
                .refresh( RefreshMode.ALL );

            return builder.build();
        }
    }
}
