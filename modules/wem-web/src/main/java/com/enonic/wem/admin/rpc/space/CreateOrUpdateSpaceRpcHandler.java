package com.enonic.wem.admin.rpc.space;

import javax.inject.Inject;

import com.enonic.wem.admin.json.JsonErrorResult;
import com.enonic.wem.admin.json.rpc.JsonRpcContext;
import com.enonic.wem.admin.json.rpc.JsonRpcException;
import com.enonic.wem.admin.rpc.AbstractDataRpcHandler;
import com.enonic.wem.admin.rpc.UploadedIconFetcher;
import com.enonic.wem.admin.rest.service.upload.UploadService;
import com.enonic.wem.api.Icon;
import com.enonic.wem.api.command.space.UpdateSpace;
import com.enonic.wem.api.space.SpaceName;
import com.enonic.wem.api.space.editor.SpaceEditor;

import static com.enonic.wem.api.command.Commands.space;
import static com.enonic.wem.api.space.editor.SpaceEditors.composite;
import static com.enonic.wem.api.space.editor.SpaceEditors.setDisplayName;
import static com.enonic.wem.api.space.editor.SpaceEditors.setIcon;


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
        final String newName = context.param( "newSpaceName" ).asString();

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
            final UpdateSpace updateCommand = space().update().name( spaceName ).editor( editor );
            client.execute( updateCommand );

            if ( newName != null )
            {
                client.execute( space().rename().space( spaceName ).newName( newName ) );
            }
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
