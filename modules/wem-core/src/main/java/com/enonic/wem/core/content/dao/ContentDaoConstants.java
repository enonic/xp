package com.enonic.wem.core.content.dao;


import com.enonic.wem.core.jcr.JcrConstants;

public interface ContentDaoConstants
{
    public static final String CONTENTS_NODE = "contents";

    public static final String CONTENT_TYPES_NODE = "contentTypes";

    public static final String CONTENTS_PATH = JcrConstants.ROOT_NODE + "/" + CONTENTS_NODE + "/";

    public static final String CONTENT_TYPES_PATH = JcrConstants.ROOT_NODE + "/" + CONTENT_TYPES_NODE + "/";
}
