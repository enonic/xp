package com.enonic.wem.core.elasticsearch.xcontent;

import org.elasticsearch.common.xcontent.XContentBuilder;

import com.enonic.wem.api.workspace.Workspace;
import com.enonic.wem.core.index.IndexException;
import com.enonic.wem.core.workspace.StoreWorkspaceDocument;

public class WorkspaceXContentBuilderFactory
    extends AbstractXContentBuilderFactor
{
    public static final String NODE_VERSION_ID_FIELD_NAME = "versionid";

    public static final String WORKSPACE_FIELD_NAME = "workspace";

    public static final String NODE_ID_FIELD_NAME = "nodeid";

    public static final String PATH_FIELD_NAME = "path";

    public static final String PARENT_PATH_FIELD_NAME = "parentpath";

    public static XContentBuilder create( final StoreWorkspaceDocument storeWorkspaceDocument, final Workspace workspace )
    {
        try
        {
            final XContentBuilder builder = startBuilder();

            addField( builder, NODE_VERSION_ID_FIELD_NAME, storeWorkspaceDocument.getNodeVersionId().toString() );
            addField( builder, WORKSPACE_FIELD_NAME, workspace.getName() );
            addField( builder, NODE_ID_FIELD_NAME, storeWorkspaceDocument.getNodeId().toString() );
            addField( builder, PATH_FIELD_NAME,
                      storeWorkspaceDocument.getPath() != null ? storeWorkspaceDocument.getPath().toString() : null );
            addField( builder, PARENT_PATH_FIELD_NAME,
                      storeWorkspaceDocument.getParentPath() != null ? storeWorkspaceDocument.getParentPath().toString() : null );

            endBuilder( builder );
            return builder;
        }
        catch ( Exception e )
        {
            throw new IndexException( "Failed to build xContent for WorkspaceDocument", e );
        }

    }

}
