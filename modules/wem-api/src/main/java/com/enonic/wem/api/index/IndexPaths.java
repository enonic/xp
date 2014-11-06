package com.enonic.wem.api.index;

public final class IndexPaths
{
    public static final String ID_KEY = "_id";

    public static final IndexDocumentItemPath CREATED_TIME_PATH = IndexDocumentItemPath.from( "createdtime" );

    public static final IndexDocumentItemPath NAME_PATH = IndexDocumentItemPath.from( "name" );

    public static final String ENTITY_KEY = "_entity";

    public static final IndexDocumentItemPath CREATOR_PATH = IndexDocumentItemPath.from( "creator" );

    public static final String MODIFIED_TIME_KEY = "modifiedTime";

    public static final IndexDocumentItemPath MODIFIED_TIME_PATH = IndexDocumentItemPath.from( MODIFIED_TIME_KEY );

    public static final IndexDocumentItemPath MODIFIER_PATH = IndexDocumentItemPath.from( "modifier" );

    public static final String PARENT_PATH_KEY = "parentpath";

    public static final IndexDocumentItemPath PARENT_PATH = IndexDocumentItemPath.from( PARENT_PATH_KEY );

    public static final String PATH_KEY = "path";

    public static final IndexDocumentItemPath PATH_PATH = IndexDocumentItemPath.from( PATH_KEY );

    public static final String ORDER_EXPRESSION_KEY = "childorder";

    public static final IndexDocumentItemPath ORDER_EXPRESSION_PATH = IndexDocumentItemPath.from( ORDER_EXPRESSION_KEY );

    public static final String MANUAL_ORDER_VALUE_KEY = "manualordervalue";

    public static final IndexDocumentItemPath MANUAL_ORDER_VALUE_PATH = IndexDocumentItemPath.from( MANUAL_ORDER_VALUE_KEY );

    public static final String VERSION_KEY = "versionkey";

    public static final IndexDocumentItemPath VERSION_KEY_PATH = IndexDocumentItemPath.from( VERSION_KEY );

}
