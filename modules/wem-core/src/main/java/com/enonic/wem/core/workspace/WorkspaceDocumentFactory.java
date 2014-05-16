package com.enonic.wem.core.workspace;

import com.enonic.wem.api.blob.BlobKey;
import com.enonic.wem.api.entity.Node;
import com.enonic.wem.api.entity.Workspace;

public class WorkspaceDocumentFactory
{
    public static WorkspaceDocument create( final BlobKey blobKey, final Workspace workspace, final Node node )
    {
        final WorkspaceDocument doc = new WorkspaceDocument();

        doc.setBlobKey( blobKey );
        doc.setPath( node.path() );
        doc.setParentPath( node.parent() );
        doc.setEntityId( node.id() );
        doc.setWorkspace( workspace );

        return doc;
    }
}
