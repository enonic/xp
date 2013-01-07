package com.enonic.wem.core.content.dao;


import java.util.List;

import javax.jcr.Session;

import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.ContentIds;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.content.ContentPaths;
import com.enonic.wem.api.content.ContentTree;
import com.enonic.wem.api.content.Contents;
import com.enonic.wem.api.content.type.QualifiedContentTypeName;
import com.enonic.wem.api.content.versioning.ContentVersion;
import com.enonic.wem.api.content.versioning.ContentVersionId;

public interface ContentDao
    extends ContentDaoConstants
{
    ContentId createContent( Content content, Session session );

    void updateContent( Content content, boolean createNewVersion, Session session );

    void deleteContent( ContentPath contentPath, Session session );

    void deleteContent( ContentId contentId, Session session );

    void renameContent( ContentPath contentPath, String newName, Session session );

    Content findContent( ContentPath contentPath, Session session );

    Content findContent( ContentId contentId, Session session );

    List<ContentVersion> getContentVersions( ContentPath contentPath, Session session );

    List<ContentVersion> getContentVersions( ContentId contentId, Session session );

    Contents findContents( ContentPaths contentPaths, Session session );

    Contents findContents( ContentIds contentIds, Session session );

    Contents findChildContent( ContentPath parentPath, Session session );

    ContentTree getContentTree( final Session session );

    int countContentTypeUsage( QualifiedContentTypeName qualifiedContentTypeName, Session session );

    Content getContentVersion( ContentPath path, ContentVersionId versionId, Session session );

    Content getContentVersion( ContentId contentId, ContentVersionId versionId, Session session );
}
