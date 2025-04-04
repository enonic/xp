package com.enonic.xp.lib.content;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.google.common.io.ByteSource;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentNotFoundException;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.UpdateMediaParams;
import com.enonic.xp.content.WorkflowCheckState;
import com.enonic.xp.content.WorkflowInfo;
import com.enonic.xp.lib.content.mapper.ContentMapper;
import com.enonic.xp.script.ScriptValue;

public class UpdateMediaHandler
    extends BaseContextHandler
{
    private String key;

    private String name;

    private String mimeType;

    private ByteSource data;

    private double focalX = 0.5;

    private double focalY = 0.5;

    private String caption;

    private List<String> artist;

    private String copyright;

    private List<String> tags;

    private Map<String, Object> workflow;

    @Override
    protected ContentMapper doExecute()
    {
        final Content existingContent = getExistingContent( this.key );
        if ( existingContent == null )
        {
            return null;
        }

        final UpdateMediaParams params = new UpdateMediaParams();
        params.content( existingContent.getId() );
        params.name( name );
        params.mimeType( mimeType );
        params.byteSource( data );
        params.focalX( focalX );
        params.focalY( focalY );
        params.caption( caption );
        params.artist( artist );
        params.copyright( copyright );
        params.tags( tags );
        params.workflowInfo( createWorkflowInfo( workflow ) );

        final Content result = this.contentService.update( params );
        return new ContentMapper( result );
    }

    private Content getExistingContent( final String key )
    {
        try
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
        catch ( final ContentNotFoundException e )
        {
            return null;
        }
    }

    protected WorkflowInfo createWorkflowInfo( Map<String, Object> map )
    {
        if ( map == null )
        {
            return null;
        }

        Object state = map.get( "state" );
        Object checks = map.get( "checks" );
        ImmutableMap.Builder<String, WorkflowCheckState> checkMapBuilder = ImmutableMap.builder();

        if ( checks != null )
        {
            ( (Map<String, String>) checks ).forEach( ( key, value ) -> checkMapBuilder.put( key, WorkflowCheckState.valueOf( value ) ) );
        }

        return WorkflowInfo.create().
            state( state instanceof String ? (String) state : null ).
            checks( checkMapBuilder.build() ).
            build();
    }

    public void setKey( final String key )
    {
        this.key = key;
    }

    public void setName( final String name )
    {
        this.name = name;
    }

    public void setMimeType( final String mimeType )
    {
        this.mimeType = mimeType;
    }

    public void setData( final ByteSource data )
    {
        this.data = data;
    }

    public void setFocalX( final double focalX )
    {
        this.focalX = focalX;
    }

    public void setFocalY( final double focalY )
    {
        this.focalY = focalY;
    }

    public void setCaption( final String caption )
    {
        this.caption = caption;
    }

    public void setArtist( final String[] artist )
    {
        this.artist = artist != null ? Arrays.asList( artist ) : null;
    }

    public void setCopyright( final String copyright )
    {
        this.copyright = copyright;
    }

    public void setTags( final String[] tags )
    {
        this.tags = tags != null ? Arrays.asList( tags ) : null;
    }

    public void setWorkflow( final ScriptValue workflow )
    {
        if ( workflow != null )
        {
            this.workflow = workflow.getMap();
        }
    }
}
