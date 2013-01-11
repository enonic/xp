package com.enonic.wem.core.content;

import com.enonic.wem.api.command.content.GenerateContentName;
import com.enonic.wem.core.command.CommandContext;
import com.enonic.wem.core.command.CommandHandler;

public class GenerateContentNameHandler
    extends CommandHandler<GenerateContentName>
{
    public GenerateContentNameHandler()
    {
        super( GenerateContentName.class );
    }

    @Override
    public void handle( final CommandContext context, final GenerateContentName command )
        throws Exception
    {
        final ContentPathNameGenerator nameGenerator = new ContentPathNameGenerator();
        final String displayName = command.getDisplayName();
        final String generatedDisplayName = nameGenerator.generatePathName( displayName );
        command.setResult( generatedDisplayName );
    }
}
