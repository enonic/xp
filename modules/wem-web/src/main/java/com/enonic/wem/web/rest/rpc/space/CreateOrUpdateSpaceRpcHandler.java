package com.enonic.wem.web.rest.rpc.space;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import com.enonic.wem.api.Icon;
import com.enonic.wem.api.command.space.UpdateSpaces;
import com.enonic.wem.api.space.SpaceName;
import com.enonic.wem.api.space.editor.SpaceEditor;
import com.enonic.wem.web.json.JsonErrorResult;
import com.enonic.wem.web.json.rpc.JsonRpcContext;
import com.enonic.wem.web.json.rpc.JsonRpcException;
import com.enonic.wem.web.rest.rpc.AbstractDataRpcHandler;
import com.enonic.wem.web.rest.rpc.UploadedIconFetcher;
import com.enonic.wem.web.rest.service.upload.UploadService;

import static com.enonic.wem.api.command.Commands.space;
import static com.enonic.wem.api.space.editor.SpaceEditors.composite;
import static com.enonic.wem.api.space.editor.SpaceEditors.setDisplayName;
import static com.enonic.wem.api.space.editor.SpaceEditors.setIcon;

@Component
public final class CreateOrUpdateSpaceRpcHandler
    extends AbstractDataRpcHandler
{
    private UploadService uploadService;

    public CreateOrUpdateSpaceRpcHandler()
    {
        super( "space_createOrUpdate" );
    }

    @Override
    public void handle( final JsonRpcContext context )
        throws Exception
    {
        final SpaceName spaceName = SpaceName.from( context.param( "spaceName" ).notBlank().asString() );
        final String displayName = context.param( "displayName" ).notBlank().asString();
        final String iconReference = context.param( "iconReference" ).asString();

        final Icon icon;
        try
        {
            icon = new UploadedIconFetcher( uploadService ).getUploadedIcon( iconReference );
        }
        catch ( JsonRpcException e )
        {
            context.setResult( new JsonErrorResult( e.getError().getMessage() ) );
            return;
        }

        if ( !spaceExists( spaceName ) )
        {
            client.execute( space().create().name( spaceName ).displayName( displayName ).icon( icon ) );
            context.setResult( CreateOrUpdateSpaceJsonResult.created() );
        }
        else
        {
            final SpaceEditor editor;
            if ( icon == null )
            {
                editor = setDisplayName( displayName );
            }
            else
            {
                editor = composite( setDisplayName( displayName ), setIcon( icon ) );
            }
            final UpdateSpaces updateCommand = space().update().name( spaceName ).editor( editor );
            client.execute( updateCommand );
            context.setResult( CreateOrUpdateSpaceJsonResult.updated() );
        }
    }

    private boolean spaceExists( final SpaceName spaceName )
    {
        return !client.execute( space().get().name( spaceName ) ).isEmpty();
    }

    @Inject
    public void setUploadService( final UploadService uploadService )
    {
        this.uploadService = uploadService;
    }
}
