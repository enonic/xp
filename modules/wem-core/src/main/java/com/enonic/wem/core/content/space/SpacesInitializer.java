package com.enonic.wem.core.content.space;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import com.enonic.wem.api.Client;
import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.content.space.CreateSpace;
import com.enonic.wem.api.command.content.space.UpdateSpaces;
import com.enonic.wem.api.content.space.SpaceName;

import static com.enonic.wem.api.content.space.editor.SpaceEditors.setDisplayName;

@Component
@DependsOn("jcrInitializer")
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
            final CreateSpace createCommand = Commands.space().create().name( name ).displayName( displayName );
            client.execute( createCommand );
        }
        else
        {
            final UpdateSpaces updateCommand = Commands.space().update();
            updateCommand.name( spaceName );
            updateCommand.editor( setDisplayName( displayName ) );
            client.execute( updateCommand );
        }
    }

    @Autowired
    public void setClient( final Client client )
    {
        this.client = client;
    }
}
