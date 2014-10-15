package com.enonic.wem.core.entity.index;

public final class IndexPaths
{
    public static final IndexDocumentItemPath CREATED_TIME_PROPERTY = IndexDocumentItemPath.from( "createdTime" );

    public static final IndexDocumentItemPath NAME_PROPERTY = IndexDocumentItemPath.from( "name" );

    public static final String ENTITY_KEY = "_entity";

    public static final IndexDocumentItemPath CREATOR_PROPERTY_PATH = IndexDocumentItemPath.from( "creator" );

    private static final String MODIFIED_TIME_KEY = "modifiedTime";

    public static final IndexDocumentItemPath MODIFIED_TIME_PROPERTY_PATH = IndexDocumentItemPath.from( MODIFIED_TIME_KEY );

    public static final IndexDocumentItemPath MODIFIER_PROPERTY_PATH = IndexDocumentItemPath.from( "modifier" );

    public static final String PARENT_PATH_KEY = "parentpath";

    public static final IndexDocumentItemPath PARENT_PROPERTY_PATH = IndexDocumentItemPath.from( PARENT_PATH_KEY );

    public static final String PATH_KEY = "path";

    public static final IndexDocumentItemPath PATH_PROPERTY_PATH = IndexDocumentItemPath.from( PATH_KEY );

}
