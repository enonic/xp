package com.enonic.xp.core.impl.issue;


import com.enonic.xp.index.IndexPath;

public final class IssuePropertyNames
{
    public static final String CREATOR = "creator";

    public static final String MODIFIER = "modifier";

    public static final String CREATED_TIME = "createdTime";

    public static final String MODIFIED_TIME = "modifiedTime";

    public static final String TITLE = "title";

    public static final String DESCRIPTION = "description";

    public static final String INDEX = "index";

    public static final String STATUS = "status";

    public static final String APPROVERS = "approverIds";

    public static final String PUBLISH_REQUEST = "publishRequest";

    public static final String PUBLISH_REQUEST_ITEM_ID = String.join( IndexPath.DIVIDER, PUBLISH_REQUEST, "items", "itemId" );

    public static final String TYPE = "type";
}
