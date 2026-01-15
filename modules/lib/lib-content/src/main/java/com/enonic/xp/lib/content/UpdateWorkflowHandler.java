package com.enonic.xp.lib.content;

import java.util.Map;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentNotFoundException;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.EditableContentWorkflow;
import com.enonic.xp.content.UpdateWorkflowParams;
import com.enonic.xp.content.UpdateWorkflowResult;
import com.enonic.xp.content.WorkflowEditor;
import com.enonic.xp.content.WorkflowInfo;
import com.enonic.xp.lib.content.mapper.ContentMapper;
import com.enonic.xp.lib.content.mapper.UpdateWorkflowResultMapper;
import com.enonic.xp.script.ScriptValue;

public final class UpdateWorkflowHandler
    extends BaseContentHandler
{
    private String key;

    private ScriptValue editor;

    @Override
    protected Object doExecute()
    {
        final Content existingContent;
        try
        {
            existingContent = getExistingContent( this.key );
        }
        catch ( final ContentNotFoundException e )
        {
            return null;
        }

        final UpdateWorkflowParams params =
            UpdateWorkflowParams.create().contentId( existingContent.getId() ).editor( newWorkflowEditor() ).build();

        final UpdateWorkflowResult result = this.contentService.updateWorkflow( params );

        return new UpdateWorkflowResultMapper( result );
    }

    @Override
    protected boolean strictDataValidation()
    {
        return false;
    }

    private Content getExistingContent( final String key )
    {
        if ( !key.startsWith( "/" ) )
        {
            return this.contentService.getById( ContentId.from( key ) );
        }
        else
        {
            return this.contentService.getByPath( ContentPath.from( key ) );
        }
    }

    private WorkflowEditor newWorkflowEditor()
    {
        return edit -> {
            final ScriptValue value = this.editor.call( new ContentMapper( edit.source ) );
            if ( value != null )
            {
                updateWorkflow( edit, value.getMap() );
            }
        };
    }

    private void updateWorkflow( final EditableContentWorkflow target, final Map<String, ?> map )
    {
        edit( map, "state", String.class, v -> target.workflow = v.map( val -> WorkflowInfo.create().state( val ).build() ).orElseThrow() );
    }

    public void setKey( final String key )
    {
        this.key = key;
    }

    public void setEditor( final ScriptValue editor )
    {
        this.editor = editor;
    }
}
