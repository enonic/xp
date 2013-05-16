package com.enonic.wem.core.space;

import javax.inject.Inject;

import com.enonic.wem.api.Client;
import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.space.CreateSpace;
import com.enonic.wem.api.command.space.UpdateSpace;
import com.enonic.wem.api.space.SpaceName;
import com.enonic.wem.core.support.BaseInitializer;

import static com.enonic.wem.api.space.editor.SpaceEditors.setDisplayName;


public class SpacesInitializer
    extends BaseInitializer
{
    private Client client;

    protected SpacesInitializer()
    {
        super( 10, "spaces" );
    }

    @Override
    public void initialize()
        throws Exception
    {
        createDefaultSpaces();
    }

    private void createDefaultSpaces()
    {
        createOrUpdate( SpaceName.temporary().name(), "Temporary content space" );
        createOrUpdate( "bluman trampoliner", "Bluman Trampoliner" );
        createOrUpdate( "bluman intranett", "Bluman Intranett" );
        createOrUpdate( "bildearkiv", "Bildearkiv" );
    }

    private void createOrUpdate( final String name, final String displayName )
    {
        final SpaceName spaceName = SpaceName.from( name );
        final boolean exists = client.execute( Commands.space().get().name( spaceName ) ).isNotEmpty();
        if ( exists )
        {
            final UpdateSpace updateCommand = Commands.space().update();
            updateCommand.name( spaceName );
            updateCommand.editor( setDisplayName( displayName ) );
            client.execute( updateCommand );
        }
        else
        {
            final CreateSpace createCommand = Commands.space().create().name( name ).displayName( displayName );
            client.execute( createCommand );
        }
    }

    @Inject
    public void setClient( final Client client )
    {
        this.client = client;
    }
}
