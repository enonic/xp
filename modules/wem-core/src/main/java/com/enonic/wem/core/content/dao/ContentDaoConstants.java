package com.enonic.wem.core.content.dao;


import com.enonic.wem.core.jcr.JcrConstants;

public interface ContentDaoConstants
{
    public static final String CONTENTS_NODE = "contents";

    public static final String CONTENT_VERSION_HISTORY_NODE = "contentsVersionHistory";

    public static final String CONTENT_TYPES_NODE = "contentTypes";

    public static final String CONTENTS_PATH = JcrConstants.ROOT_NODE + "/" + CONTENTS_NODE + "/";

    public static final String CONTENT_VERSION_HISTORY_PATH = JcrConstants.ROOT_NODE + "/" + CONTENT_VERSION_HISTORY_NODE + "/";

    public static final String CONTENT_VERSION_PREFIX = "__contentVersion";

    public static final String CONTENT_NEXT_VERSION_PROPERTY = "nextVersion";

    public static final String CONTENT_TYPES_PATH = JcrConstants.ROOT_NODE + "/" + CONTENT_TYPES_NODE + "/";
}
