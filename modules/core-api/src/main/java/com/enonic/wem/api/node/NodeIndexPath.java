package com.enonic.wem.api.node;

import com.enonic.wem.api.index.IndexPath;

public class NodeIndexPath
{
    private static final String DIVIDER = ".";

    private static final String PERMISSIONS_ROOT = "_permissions";

    public static final IndexPath STATE = IndexPath.from( "_state" );

    public static final IndexPath VERSION = IndexPath.from( "_versionKey" );

    public static final IndexPath NODE_TYPE = IndexPath.from( "_nodeType" );

    public static final IndexPath ALL_TEXT = IndexPath.from( "_allText" );

    public static final IndexPath MANUAL_ORDER_VALUE = IndexPath.from( "_manualOrderValue" );

    public static final IndexPath PATH = IndexPath.from( "_path" );

    public static final IndexPath ID = IndexPath.from( "_id" );

    public static final IndexPath MODIFIER = IndexPath.from( "_modifier" );

    public static final IndexPath NAME = IndexPath.from( "_name" );

    public static final IndexPath CREATED_TIME = IndexPath.from( "_createdTime" );

    public static final IndexPath CREATOR = IndexPath.from( "_creator" );

    public static final IndexPath PARENT_PATH = IndexPath.from( "_parentPath" );

    public static final IndexPath MODIFIED_TIME = IndexPath.from( "_modifiedTime" );

    public static final IndexPath PERMISSIONS_READ = IndexPath.from( PERMISSIONS_ROOT + DIVIDER + "read" );

    public static final IndexPath PERMISSIONS_CREATE = IndexPath.from( PERMISSIONS_ROOT + DIVIDER + "create" );

    public static final IndexPath PERMISSIONS_DELETE = IndexPath.from( PERMISSIONS_ROOT + DIVIDER + "delete" );

    public static final IndexPath PERMISSIONS_MODIFY = IndexPath.from( PERMISSIONS_ROOT + DIVIDER + "modify" );

    public static final IndexPath PERMISSIONS_PUBLISH = IndexPath.from( PERMISSIONS_ROOT + DIVIDER + "publish" );

    public static final IndexPath PERMISSIONS_READ_PERMISSION = IndexPath.from( PERMISSIONS_ROOT + DIVIDER + "readpermissions" );

    public static final IndexPath PERMISSIONS_WRITE_PERMISSION = IndexPath.from( PERMISSIONS_ROOT + DIVIDER + "writepermissions" );
}
