package com.enonic.xp.core.impl.app;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.osgi.framework.Bundle;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.node.NodeService;

@ExtendWith(MockitoExtension.class)
public class ApplicationFactoryServiceMock
    implements ApplicationFactoryService
{
    @Mock(stubOnly = true)
    private NodeService nodeService;

    private final ApplicationFactory factory = new ApplicationFactory( nodeService );

    private final Map<Bundle, ApplicationAdaptor> map = new HashMap<>();

    @Override
    public ApplicationAdaptor getApplication( final Bundle bundle )
    {
        return map.computeIfAbsent( bundle, factory::create );
    }

    @Override
    public Optional<ApplicationAdaptor> findActiveApplication( final ApplicationKey applicationKey )
    {
        return map.entrySet()
            .stream()
            .filter( e -> ApplicationHelper.getApplicationKey( e.getKey() ).equals( applicationKey ) )
            .map( Map.Entry::getValue )
            .findAny();
    }

    @Override
    public ResourceSource findResourceSource( final ApplicationKey applicationKey, final String resolverName )
    {
        return map.entrySet()
            .stream()
            .filter( e -> ApplicationHelper.getApplicationKey( e.getKey() ).equals( applicationKey ) )
            .findAny()
            .map( e -> new ResourceSource( e.getKey(), e.getValue().getUrlResolver() ) )
            .orElse( ResourceSource.NONE );
    }
}
