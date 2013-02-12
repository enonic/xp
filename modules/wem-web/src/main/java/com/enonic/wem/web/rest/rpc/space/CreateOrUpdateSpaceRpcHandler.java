package com.enonic.wem.web.rest.rpc.space;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enonic.wem.api.Icon;
import com.enonic.wem.api.command.space.UpdateSpaces;
import com.enonic.wem.api.space.SpaceName;
import com.enonic.wem.web.json.rpc.JsonRpcContext;
import com.enonic.wem.web.rest.rpc.AbstractDataRpcHandler;
import com.enonic.wem.web.rest.service.upload.UploadItem;
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
        final Icon icon = getUploadedImage( iconReference );

        if ( !spaceExists( spaceName ) )
        {
            client.execute( space().create().name( spaceName ).displayName( displayName ).icon( icon ) );
            context.setResult( CreateOrUpdateSpaceJsonResult.created() );
        }
        else
        {
            final UpdateSpaces updateCommand =
                space().update().name( spaceName ).editor( composite( setDisplayName( displayName ), setIcon( icon ) ) );
            client.execute( updateCommand );
            context.setResult( CreateOrUpdateSpaceJsonResult.updated() );
        }
    }

    private boolean spaceExists( final SpaceName spaceName )
    {
        return !client.execute( space().get().name( spaceName ) ).isEmpty();
    }

    private Icon getUploadedImage( final String iconReference )
        throws IOException
    {
        if ( iconReference == null )
        {
            return null;
        }
        final UploadItem uploadItem = uploadService.getItem( iconReference );
        if ( uploadItem != null )
        {
            final File file = uploadItem.getFile();
            if ( file.exists() )
            {
                final byte[] imageData = FileUtils.readFileToByteArray( file );
                return Icon.from( imageData, uploadItem.getMimeType() );
            }
        }
        return null;
    }

    @Autowired
    public void setUploadService( final UploadService uploadService )
    {
        this.uploadService = uploadService;
    }
}
