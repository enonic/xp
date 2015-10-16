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
        final Connector[] connectors = this.object.getConnectors();
        return connectors.length > 0 ? (ServerConnector) connectors[0] : null;
    }
}
