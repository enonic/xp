package com.enonic.xp.core.impl.app.resolver;

import java.util.Set;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.core.impl.app.NodeValueResource;
import com.enonic.xp.core.impl.app.VirtualAppConstants;
import com.enonic.xp.core.impl.app.VirtualAppContext;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeName;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeService;
import com.enonic.xp.resource.Resource;
import com.enonic.xp.resource.ResourceKey;

public class FakeCmsYmlUrlResolver
    implements ApplicationUrlResolver
{
    private static final Set<String> SINGLE_FILE_SET = Set.of( VirtualAppConstants.CMS_RESOURCE_PATH );

    private final ApplicationKey applicationKey;

    private final NodeService nodeService;

    public FakeCmsYmlUrlResolver( final ApplicationKey applicationKey, final NodeService nodeService )
    {
        this.applicationKey = applicationKey;
        this.nodeService = nodeService;
    }

    @Override
    public Set<String> findFiles()
    {
        return SINGLE_FILE_SET;
    }

    @Override
    public Resource findResource( final String path )
    {
        if ( VirtualAppConstants.CMS_RESOURCE_PATH.equals( path ) )
        {
            final NodePath appPath =
                new NodePath( VirtualAppConstants.VIRTUAL_APP_ROOT_PARENT, NodeName.from( applicationKey.toString() ) );
            final Node applicationNode = VirtualAppContext.createContext().callWith( () -> nodeService.getByPath( appPath ) );
            if ( applicationNode != null )
            {
                return new NodeValueResource( ResourceKey.from( applicationKey, path ), VirtualAppConstants.DEFAULT_CMS_RESOURCE_VALUE,
                                              applicationNode.getTimestamp() );
            }
        }
        return null;
    }
}
