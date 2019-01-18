package com.enonic.xp.web.jetty.impl.configurator;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;

public abstract class ConnectorConfiguratorTest
    extends JettyConfiguratorTest<Server>
{
    @Override
    protected final Server setupObject()
    {
        return new Server();
    }

    protected final ServerConnector getConnector()
    {
        return getConnector( 0 );
    }

    protected final ServerConnector getConnector( final int index )
    {
        final Connector[] connectors = this.object.getConnectors();
        return connectors.length > index ? (ServerConnector) connectors[index] : null;
    }
}
