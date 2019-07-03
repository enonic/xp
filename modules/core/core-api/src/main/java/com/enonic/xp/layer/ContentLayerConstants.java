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

    public static final String BRANCH_PREFIX_DRAFT = ContentConstants.BRANCH_VALUE_DRAFT + "-";

    public static final String BRANCH_PREFIX_MASTER = ContentConstants.BRANCH_VALUE_MASTER + "-";
}
