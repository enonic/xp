package com.enonic.xp.launcher.ui.panel;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import com.enonic.xp.launcher.ui.MainWindow;
import com.enonic.xp.launcher.ui.util.DesktopHelper;

final class LaunchBrowserAction
    extends AbstractAction
{
    public LaunchBrowserAction()
    {
        super( "Launch Browser" );
        putValue( SHORT_DESCRIPTION, getValue( NAME ) );
        setEnabled( true );
    }

    @Override
    public void actionPerformed( ActionEvent event )
    {
        final String url = MainWindow.get().getHttpUrl();
        DesktopHelper.launch( url );
    }
}
