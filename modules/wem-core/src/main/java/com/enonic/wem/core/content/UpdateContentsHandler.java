package com.enonic.wem.core.content;


import org.springframework.stereotype.Component;

import com.enonic.wem.api.command.content.UpdateContents;
import com.enonic.wem.core.command.CommandContext;
import com.enonic.wem.core.command.CommandHandler;

@Component
public class UpdateContentsHandler
    extends CommandHandler<UpdateContents>
{

    public UpdateContentsHandler()
    {
        super( UpdateContents.class );

    }

    @Override
    public void handle( final CommandContext context, final UpdateContents command )
        throws Exception
    {

    }
}
