package com.enonic.wem.core.elasticsearch;

import org.elasticsearch.common.xcontent.XContentBuilder;

import com.enonic.wem.core.index.IndexException;
import com.enonic.wem.core.workspace.WorkspaceDocument;

class WorkspaceXContentBuilderFactory
    extends AbstractXContentBuilderFactor
{
    public static final String BLOBKEY_FIELD_NAME = "blobKey";

    public static final String WORKSPACE_FIELD_NAME = "workspace";

    public static final String ENTITY_ID_FIELD_NAME = "entityId";

    public static final String PATH_FIELD_NAME = "path";

    public static final String PARENT_PATH_FIELD_NAME = "parentPath";

    static XContentBuilder create( final WorkspaceDocument workspaceDocument )
    {
        try
        {
            final XContentBuilder builder = startBuilder();

            addField( builder, BLOBKEY_FIELD_NAME, workspaceDocument.getNodeVersionId().toString() );
            addField( builder, WORKSPACE_FIELD_NAME, workspaceDocument.getWorkspace().toString() );
            addField( builder, ENTITY_ID_FIELD_NAME, workspaceDocument.getEntityId().toString() );
            addField( builder, PATH_FIELD_NAME, workspaceDocument.getPath() );
            addField( builder, PARENT_PATH_FIELD_NAME, workspaceDocument.getParentPath() );

            endBuilder( builder );
            return builder;
        }
        catch ( Exception e )
        {
            throw new IndexException( "Failed to build xContent for WorkspaceDocument", e );
        }

    }

}
