package com.enonic.xp.core.impl.content.serializer;

import com.enonic.xp.content.WorkflowInfo;
import com.enonic.xp.data.PropertySet;

import static com.enonic.xp.content.ContentPropertyNames.WORKFLOW_INFO_STATE;

public class WorkflowInfoSerializer
{
    public WorkflowInfo extract( final PropertySet workflowInfo )
    {
        if ( workflowInfo == null )
        {
            return WorkflowInfo.ready();
        }

        return WorkflowInfo.create().state( workflowInfo.getString( WORKFLOW_INFO_STATE ) ).build();
    }
}
