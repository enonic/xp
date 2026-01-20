package com.enonic.xp.core.impl.issue;

import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.index.IndexConfig;
import com.enonic.xp.index.IndexConfigDocument;
import com.enonic.xp.index.IndexPath;
import com.enonic.xp.index.PatternIndexConfigDocument;

import static com.enonic.xp.issue.IssuePropertyNames.APPROVERS;
import static com.enonic.xp.issue.IssuePropertyNames.CREATED_TIME;
import static com.enonic.xp.issue.IssuePropertyNames.CREATOR;
import static com.enonic.xp.issue.IssuePropertyNames.DESCRIPTION;
import static com.enonic.xp.issue.IssuePropertyNames.MODIFIED_TIME;
import static com.enonic.xp.issue.IssuePropertyNames.MODIFIER;
import static com.enonic.xp.issue.IssuePropertyNames.PUBLISH_REQUEST;
import static com.enonic.xp.issue.IssuePropertyNames.STATUS;
import static com.enonic.xp.issue.IssuePropertyNames.TITLE;

class IssueIndexConfigFactory
{
    public static IndexConfigDocument create()
    {
        return doCreateIndexConfig();
    }

    private static IndexConfigDocument doCreateIndexConfig()
    {
        final PatternIndexConfigDocument.Builder configDocumentBuilder = PatternIndexConfigDocument.create().
            analyzer( ContentConstants.DOCUMENT_INDEX_DEFAULT_ANALYZER ).
            add( TITLE, IndexConfig.FULLTEXT ).
            add( CREATOR, IndexConfig.MINIMAL ).
            add( MODIFIER, IndexConfig.MINIMAL ).
            add( CREATED_TIME, IndexConfig.MINIMAL ).
            add( MODIFIED_TIME, IndexConfig.MINIMAL ).
            add( STATUS, IndexConfig.MINIMAL ).
            add( DESCRIPTION, IndexConfig.FULLTEXT ).
            add( APPROVERS, IndexConfig.MINIMAL ).
            add( PUBLISH_REQUEST, IndexConfig.MINIMAL ).add( IndexPath.from( PUBLISH_REQUEST, PublishRequestPropertyNames.EXCLUDE_IDS ),
                                                             IndexConfig.NONE ).
            defaultConfig( IndexConfig.BY_TYPE );

        return configDocumentBuilder.build();
    }
}
