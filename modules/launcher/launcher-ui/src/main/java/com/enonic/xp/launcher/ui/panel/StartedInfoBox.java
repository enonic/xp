package com.enonic.xp.launcher.ui.panel;

import javax.swing.*;

final class StartedInfoBox
    extends AbstractInfoBox
{
    public StartedInfoBox()
    {
        super( "started.html" );
        addSouth( new JButton( new LaunchBrowserAction() ) );
    }
}