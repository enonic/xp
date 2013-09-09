package com.enonic.wem.api.command.content;

import com.enonic.wem.api.command.Command;
import com.enonic.wem.api.content.Contents;

public class GetRootContent
    extends Command<Contents>
{

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }

        if ( !( o instanceof GetContents ) )
        {
            return false;
        }

        return true;
    }

    @Override
    public void validate()
    {
    }
}
