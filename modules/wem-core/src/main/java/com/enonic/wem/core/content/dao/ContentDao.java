package com.enonic.wem.core.content.dao;


import java.util.List;

import javax.jcr.Session;

import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.ContentIds;
import com.enonic.wem.api.content.ContentNotFoundException;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.content.ContentPaths;
import com.enonic.wem.api.content.Contents;
import com.enonic.wem.api.content.UnableToDeleteContentException;
import com.enonic.wem.api.content.versioning.ContentVersion;
import com.enonic.wem.api.content.versioning.ContentVersionId;
import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.api.support.tree.Tree;
import com.enonic.wem.core.jcr.JcrConstants;

public interface ContentDao
{
    public static final String CONTENTS_NODE = "contents";

    public static final String CONTENTS_ROOT_PATH = JcrConstants.ROOT_NODE + "/" + CONTENTS_NODE + "/";

    public static final String NON_CONTENT_NODE_PREFIX = "__";

    public static final String CONTENT_VERSION_HISTORY_NODE = NON_CONTENT_NODE_PREFIX + "contentsVersionHistory";

    public static final String CONTENT_VERSION_PREFIX = NON_CONTENT_NODE_PREFIX + "contentVersion";

    public static final String CONTENT_EMBEDDED_NODE = NON_CONTENT_NODE_PREFIX + "embedded";

    public static final String CONTENT_ATTACHMENTS_NODE = NON_CONTENT_NODE_PREFIX + "attachments";

    public static final String CONTENT_NEXT_VERSION_PROPERTY = "nextVersion";

    Content create( Content content, Session session );

    Content update( Content content, boolean createNewVersion, Session session );

    void deleteById( ContentId contentId, Session session )
        throws ContentNotFoundException, UnableToDeleteContentException;

    void deleteByPath( ContentPath contentPath, Session session )
        throws ContentNotFoundException, UnableToDeleteContentException;

    void forceDelete( ContentId contentId, Session session )
        throws ContentNotFoundException;

    Content selectById( ContentId contentId, Session session );

    Content selectByPath( ContentPath contentPath, Session session );

    Contents selectByIds( ContentIds contentIds, Session session );

    Contents selectByPaths( ContentPaths contentPaths, Session session );

    boolean renameContent( ContentId contentId, String newName, Session session );

    void moveContent( ContentId contentId, ContentPath newPath, Session session );

    List<ContentVersion> getContentVersionsById( ContentId contentId, Session session );

    List<ContentVersion> getContentVersionsByPath( ContentPath contentPath, Session session );

    Contents findChildContent( ContentPath parentPath, Session session );

    Tree<Content> getContentTree( Session session );

    int countContentTypeUsage( ContentTypeName contentTypeName, Session session );

    Content getContentVersionById( ContentId contentId, ContentVersionId versionId, Session session );

    Content getContentVersionByPath( ContentPath contentPath, ContentVersionId versionId, Session session );
}
