package com.enonic.xp.lib.content;

import java.util.Map;

import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.DuplicateContentParams;
import com.enonic.xp.lib.content.mapper.DuplicateContentsResultMapper;

public class DuplicateContentHandler
    extends BaseContentHandler
{
    private String contentId;

    private Map<String, Object> workflow;

    private boolean includeChildren = true;

    private boolean variant;

    private String name;

    private String parentPath;

    @Override
    protected Object doExecute()
    {
        return new DuplicateContentsResultMapper( contentService.duplicate( createDuplicateParams() ) );
    }

    public void setContentId( final String contentId )
    {
        this.contentId = contentId;
    }

    public void setWorkflow( final Map<String, Object> workflow )
    {
        this.workflow = workflow;
    }

    public void setIncludeChildren( final boolean includeChildren )
    {
        this.includeChildren = includeChildren;
    }

    public void setVariant( final boolean variant )
    {
        this.variant = variant;
    }

    public void setName( final String name )
    {
        this.name = name;
    }

    public void setParentPath( final String parentPath )
    {
        this.parentPath = parentPath;
    }

    private DuplicateContentParams createDuplicateParams()
    {
        final DuplicateContentParams.Builder builder = DuplicateContentParams.create()
            .contentId( ContentId.from( contentId ) )
            .includeChildren( includeChildren )
            .variant( variant )
            .name( name );

        if ( parentPath != null )
        {
            builder.parent( ContentPath.from( parentPath ) );
        }
        if ( workflow != null )
        {
            builder.workflowInfo( createWorkflowInfo( workflow ) );
        }
        return builder.build();
    }
}
