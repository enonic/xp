package com.enonic.wem.core.content;

import com.enonic.wem.api.command.content.GetContentByPaths;
import com.enonic.wem.api.content.ContentNotFoundException;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.content.Contents;
import com.enonic.wem.api.entity.NoNodeAtPathFoundException;
import com.enonic.wem.core.command.CommandHandler;


public class GetContentByPathsHandler
    extends CommandHandler<GetContentByPaths>
{
    @Override
    public void handle()
        throws Exception
    {
        final Contents contents;

        try
        {
            contents = new GetContentByPathsService( this.context.getJcrSession(), this.command ).execute();
        }
        catch ( NoNodeAtPathFoundException ex )
        {
            throw new ContentNotFoundException( ContentPath.from( ex.getPath().toString() ) );
        }

        command.setResult( contents );
    }
}
