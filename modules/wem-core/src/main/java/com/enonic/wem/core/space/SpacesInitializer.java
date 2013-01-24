package com.enonic.wem.core.space;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import com.enonic.wem.api.Client;
import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.space.CreateSpace;
import com.enonic.wem.api.command.space.UpdateSpaces;
import com.enonic.wem.api.space.SpaceName;

import static com.enonic.wem.api.space.editor.SpaceEditors.setDisplayName;

//@Component
//@DependsOn("jcrInitializer")
public class SpacesInitializer
{
    private Client client;

    @PostConstruct
    public void createDefaultSpaces()
    {
        createOrUpdate( "default", "Default space" );
        createOrUpdate( "blueman", "Blueman travels" );
    }

    private void createOrUpdate( final String name, final String displayName )
    {
        final SpaceName spaceName = SpaceName.from( name );
        final boolean exists = client.execute( Commands.space().get().name( spaceName ) ).isNotEmpty();
        if ( exists )
        {
            final UpdateSpaces updateCommand = Commands.space().update();
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

    @Autowired
    public void setClient( final Client client )
    {
        this.client = client;
    }
}
