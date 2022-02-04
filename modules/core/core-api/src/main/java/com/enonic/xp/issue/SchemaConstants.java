package com.enonic.xp.issue;

import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeType;

public final class SchemaConstants
{
    public static final String SCHEMA_ROOT_NAME = "schemas";

    public static final String ADMIN_ROOT_NAME = "admin";

    public static final String WIDGET_ROOT_NAME = "widget";

    public static final String SITE_ROOT_NAME = "site";

    public static final String CONTENT_TYPE_ROOT_NAME = "content-type";

    public static final String PART_ROOT_NAME = "part";

    public static final String PAGE_ROOT_NAME = "page";

    public static final String LAYOUT_ROOT_NAME = "layout";

    public static final NodePath SCHEMA_ROOT_PARENT = NodePath.ROOT;

    public static final NodePath SCHEMA_ROOT_PATH = NodePath.create( SCHEMA_ROOT_PARENT, SCHEMA_ROOT_NAME ).build();

    public static final NodeType SCHEMA_NODE_TYPE = NodeType.from( "schema" );
}
