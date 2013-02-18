package com.enonic.wem.web.rest.rpc.space;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enonic.wem.api.Icon;
import com.enonic.wem.api.command.space.UpdateSpaces;
import com.enonic.wem.api.space.SpaceName;
import com.enonic.wem.web.json.JsonErrorResult;
import com.enonic.wem.web.json.rpc.JsonRpcContext;
import com.enonic.wem.web.json.rpc.JsonRpcException;
import com.enonic.wem.web.rest.rpc.AbstractDataRpcHandler;
import com.enonic.wem.web.rest.rpc.IconImageHelper;

import static com.enonic.wem.api.command.Commands.space;
import static com.enonic.wem.api.space.editor.SpaceEditors.composite;
import static com.enonic.wem.api.space.editor.SpaceEditors.setDisplayName;
import static com.enonic.wem.api.space.editor.SpaceEditors.setIcon;

@Component
public final class CreateOrUpdateSpaceRpcHandler
    extends AbstractDataRpcHandler
{
    private IconImageHelper iconImageHelper;

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
            icon = iconImageHelper.getUploadedIcon( iconReference );
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

    @Autowired
    public void setIconImageHelper( final IconImageHelper iconImageHelper )
    {
        this.iconImageHelper = iconImageHelper;
    }
}
