package com.enonic.xp.repo.impl.dump.upgrade.flattenedpage;

import java.util.HashMap;
import java.util.Map;

import com.google.common.collect.ImmutableMap;

import com.enonic.xp.data.PropertySet;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.node.NodeId;

import static com.enonic.xp.repo.impl.dump.upgrade.flattenedpage.FlattenedPageSourceConstants.SRC_CONTROLLER_KEY;
import static com.enonic.xp.repo.impl.dump.upgrade.flattenedpage.FlattenedPageSourceConstants.SRC_PAGE_KEY;

public class TemplateControllerMappings
{
    private static final String CONTENT_TYPE_KEY = "type";

    private static final String TEMPLATE_CONTENT_TYPE_VALUE = "portal:page-template";

    private final Map<String, String> templateControllerMap = new HashMap<>();


    public void handle( final NodeId nodeId, final PropertyTree nodeData )
    {
        final String contentType = nodeData.getString( CONTENT_TYPE_KEY );
        final PropertySet sourcePageSet = nodeData.getSet( SRC_PAGE_KEY );
        if ( sourcePageSet == null || !TEMPLATE_CONTENT_TYPE_VALUE.equals( contentType ) )
        {
            return;
        }

        final String controllerKey = sourcePageSet.getString( SRC_CONTROLLER_KEY );
        if (controllerKey != null) {
            templateControllerMap.put( nodeId.toString(), controllerKey );
        }
    }

    public Map<String, String> getMappings() {
        return ImmutableMap.copyOf( templateControllerMap );
    }
}
