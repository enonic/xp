package com.enonic.xp.impl.macro;

import com.enonic.xp.macro.MacroContext;
import com.enonic.xp.macro.MacroProcessor;

public class ModifiedUserMacroProcessor
    implements MacroProcessor
{

    private static final String USER_PARAM = "modified_user";

    private static final String USER_UNKNOWN = "unknown";

    @Override
    public String process( final MacroContext context )
    {

        final String user = context.getParam( USER_PARAM );
        return user == null ? USER_UNKNOWN : user;
    }
}
