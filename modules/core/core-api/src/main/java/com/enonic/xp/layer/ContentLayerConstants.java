package com.enonic.xp.layer;

import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.data.PropertyPath;
import com.enonic.xp.node.NodePath;

public class ContentLayerConstants
{
    public static final String NODE_TYPE = "layer";

    public static final NodePath LAYER_PARENT_PATH = NodePath.create( NodePath.ROOT, "layers" ).
        build();

    public static final NodePath DEFAULT_LAYER_PATH = NodePath.create( LAYER_PARENT_PATH, ContentLayerName.DEFAULT_LAYER_NAME.getValue() ).
        build();

    public static final PropertyPath NAME_PROPERTY_PATH = PropertyPath.from( "name" );

    public static final PropertyPath PARENT_NAME_PROPERTY_PATH = PropertyPath.from( "parentName" );

    public static final PropertyPath DISPLAY_NAME_PROPERTY_PATH = PropertyPath.from( "displayName" );

    public static final PropertyPath DESCRIPTION_PROPERTY_PATH = PropertyPath.from( "description" );

    public static final PropertyPath LANGUAGE_PROPERTY_PATH = PropertyPath.from( "language" );

    public static final PropertyPath ICON_PROPERTY_PATH = PropertyPath.from( "icon" );

    public static final PropertyPath ICON_NAME_PROPERTY_PATH = PropertyPath.from( "name" );

    public static final PropertyPath ICON_LABEL_PROPERTY_PATH = PropertyPath.from( "label" );

    public static final PropertyPath ICON_MIMETYPE_PROPERTY_PATH = PropertyPath.from( "mimeType" );

    public static final PropertyPath ICON_SIZE_PROPERTY_PATH = PropertyPath.from( "size" );

    public static final String ICON_LABEL_VALUE = "icon";

    public static final String BRANCH_PREFIX_DRAFT = ContentConstants.BRANCH_VALUE_DRAFT + "-";

    public static final String BRANCH_PREFIX_MASTER = ContentConstants.BRANCH_VALUE_MASTER + "-";
}
