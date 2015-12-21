package com.enonic.xp.launcher.ui.panel;

import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JPanel;

final class StartedInfoBox
    extends AbstractInfoBox
{
    public StartedInfoBox()
    {
        super( "started.html" );

        final JPanel panel = new JPanel();
        panel.setOpaque( false );

        final GridLayout layout = new GridLayout( 1, 2 );
        panel.setLayout( layout );

        panel.add( newHomeButton() );
        panel.add( newLaunchButton() );

        addSouth( panel );
    }

    private JButton newLaunchButton()
    {
        return new JButton( new LaunchBrowserAction() );
    }

    private JButton newHomeButton()
    {
        return new JButton( new OpenHomeAction() );
    }
}
