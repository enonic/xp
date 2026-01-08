package com.enonic.xp.lib.content;

import java.util.Map;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentNotFoundException;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.EditableWorkflow;
import com.enonic.xp.content.UpdateWorkflowParams;
import com.enonic.xp.content.UpdateWorkflowResult;
import com.enonic.xp.content.WorkflowCheckState;
import com.enonic.xp.content.WorkflowEditor;
import com.enonic.xp.content.WorkflowState;
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

    private void updateWorkflow( final EditableWorkflow target, final Map<String, ?> map )
    {
        edit( map, "state", String.class, val -> target.state = val.map( WorkflowState::valueOf ).orElse( null ) );

        final Object checks = map.get( "checks" );
        if ( checks instanceof Map )
        {
            target.checks.clear();
            ( (Map<String, String>) checks ).forEach( ( key, value ) -> target.checks.put( key, WorkflowCheckState.valueOf( value ) ) );
        }
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
