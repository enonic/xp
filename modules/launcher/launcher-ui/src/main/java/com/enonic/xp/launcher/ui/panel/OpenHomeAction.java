package com.enonic.xp.launcher.ui.panel;

import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.AbstractAction;

import com.enonic.xp.launcher.ui.MainWindow;
import com.enonic.xp.launcher.ui.util.DesktopHelper;

final class OpenHomeAction
    extends AbstractAction
{
    public OpenHomeAction()
    {
        super( "Home Directory" );
        putValue( SHORT_DESCRIPTION, getValue( NAME ) );
        setEnabled( true );
    }

    @Override
    public void actionPerformed( ActionEvent event )
    {
        final File dir = MainWindow.get().getHomeDir();
        DesktopHelper.open( dir );
    }
}
