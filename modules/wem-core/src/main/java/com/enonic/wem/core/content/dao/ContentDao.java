package com.enonic.wem.core.content.dao;


import java.util.List;

import javax.jcr.Session;

import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.content.ContentSelector;
import com.enonic.wem.api.content.ContentSelectors;
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

    void deleteContent( ContentSelector contentSelector, Session session );

    void renameContent( ContentPath contentPath, String newName, Session session );

    Content findContent( ContentSelector contentSelector, Session session );

    List<ContentVersion> getContentVersions( ContentSelector contentSelector, Session session );

    Contents findContents( ContentSelectors contentSelectors, Session session );

    Contents findChildContent( ContentPath parentPath, Session session );

    ContentTree getContentTree( Session session );

    int countContentTypeUsage( QualifiedContentTypeName qualifiedContentTypeName, Session session );

    Content getContentVersion( ContentSelector contentSelector, ContentVersionId versionId, Session session );
}
