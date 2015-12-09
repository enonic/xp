package com.enonic.xp.launcher.ui.panel;

import javax.swing.JProgressBar;

final class StartingInfoBox
    extends AbstractInfoBox
{
    public StartingInfoBox()
    {
        super( "starting.html" );

        final JProgressBar bar = new JProgressBar();
        bar.setIndeterminate( true );

        addSouth( bar );
    }
}
