package com.enonic.wem.core.content.dao;


import java.util.List;

import javax.jcr.Session;

import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.ContentNotFoundException;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.content.ContentSelector;
import com.enonic.wem.api.content.ContentSelectors;
import com.enonic.wem.api.content.Contents;
import com.enonic.wem.api.content.UnableToDeleteContentException;
import com.enonic.wem.api.content.schema.content.QualifiedContentTypeName;
import com.enonic.wem.api.content.versioning.ContentVersion;
import com.enonic.wem.api.content.versioning.ContentVersionId;
import com.enonic.wem.api.support.tree.Tree;
import com.enonic.wem.core.jcr.JcrConstants;

public interface ContentDao
{
    public static final String SPACES_NODE = "spaces";

    public static final String SPACES_PATH = JcrConstants.ROOT_NODE + "/" + SPACES_NODE + "/";

    public static final String SPACE_CONTENT_ROOT_NODE = "root";

    public static final String CONTENT_VERSION_HISTORY_NODE = "__contentsVersionHistory";

    public static final String CONTENT_VERSION_PREFIX = "__contentVersion";

    public static final String CONTENT_NEXT_VERSION_PROPERTY = "nextVersion";

    ContentId create( Content content, Session session );

    void update( Content content, boolean createNewVersion, Session session );

    void delete( ContentSelector contentSelector, Session session )
        throws ContentNotFoundException, UnableToDeleteContentException;

    Contents select( ContentSelectors contentSelectors, Session session );

    Content select( ContentSelector contentSelector, Session session );

    boolean renameContent( ContentId contentId, String newName, Session session );

    void moveContent( ContentId contentId, ContentPath newPath, Session session );

    List<ContentVersion> getContentVersions( ContentSelector contentSelector, Session session );

    Contents findChildContent( ContentPath parentPath, Session session );

    Tree<Content> getContentTree( final Session session, final ContentSelectors<ContentId> contentSelectors );

    Tree<Content> getContentTree( Session session );

    int countContentTypeUsage( QualifiedContentTypeName qualifiedContentTypeName, Session session );

    Content getContentVersion( ContentSelector contentSelector, ContentVersionId versionId, Session session );

}
