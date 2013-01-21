package com.enonic.wem.core.content.dao;


import java.util.List;

import javax.jcr.Session;

import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.content.ContentSelector;
import com.enonic.wem.api.content.ContentSelectors;
import com.enonic.wem.api.content.Contents;
import com.enonic.wem.api.content.type.QualifiedContentTypeName;
import com.enonic.wem.api.content.versioning.ContentVersion;
import com.enonic.wem.api.content.versioning.ContentVersionId;
import com.enonic.wem.api.support.tree.Tree;
import com.enonic.wem.core.jcr.JcrConstants;

public interface ContentDao
{
    public static final String CONTENTS_NODE = "contents";

    public static final String CONTENTS_PATH = JcrConstants.ROOT_NODE + "/" + CONTENTS_NODE + "/";

    public static final String CONTENT_VERSION_HISTORY_NODE = "__contentsVersionHistory";

    public static final String CONTENT_VERSION_PREFIX = "__contentVersion";

    public static final String CONTENT_NEXT_VERSION_PROPERTY = "nextVersion";

    ContentId createContent( Content content, Session session );

    void updateContent( Content content, boolean createNewVersion, Session session );

    void deleteContent( ContentSelector contentSelector, Session session );

    void renameContent( ContentPath contentPath, String newName, Session session );

    Content findContent( ContentSelector contentSelector, Session session );

    List<ContentVersion> getContentVersions( ContentSelector contentSelector, Session session );

    Contents findContents( ContentSelectors contentSelectors, Session session );

    Contents findChildContent( ContentPath parentPath, Session session );

    Tree<Content> getContentTree( Session session );

    int countContentTypeUsage( QualifiedContentTypeName qualifiedContentTypeName, Session session );

    Content getContentVersion( ContentSelector contentSelector, ContentVersionId versionId, Session session );
}
