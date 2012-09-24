package com.enonic.wem.core.command;

import com.enonic.wem.api.Client;
import com.enonic.wem.core.jcr.old.JcrSession;

public interface CommandContext
{
    public Client getClient();

    public JcrSession getJcrSession();
}
