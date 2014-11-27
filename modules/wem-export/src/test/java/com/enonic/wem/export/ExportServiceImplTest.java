package com.enonic.wem.export;

import org.junit.Before;
import org.mockito.Mockito;

import com.enonic.wem.api.node.NodeService;

public class ExportServiceImplTest
{
    private ExportServiceImpl exportService;

    @Before
    public void setUp()
        throws Exception
    {
        final NodeService nodeService = Mockito.mock( NodeService.class );

        this.exportService = new ExportServiceImpl();
        this.exportService.setNodeService( nodeService );

    }

}