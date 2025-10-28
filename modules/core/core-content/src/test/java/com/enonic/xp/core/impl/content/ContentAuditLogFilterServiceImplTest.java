package com.enonic.xp.core.impl.content;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.mock;

class ContentAuditLogFilterServiceImplTest
{
    @Test
    void default_disallow_update()
    {
        final ContentConfig config = mock( ContentConfig.class, invocation -> invocation.getMethod().getDefaultValue() );
        Assertions.assertFalse( new ContentAuditLogFilterServiceImpl( config ).accept( "system.content.update" ) );
    }

    @Test
    void empty()
    {
        final ContentConfig config = mock( ContentConfig.class, invocation -> "" );
        Assertions.assertFalse( new ContentAuditLogFilterServiceImpl( config ).accept( "system.content.any" ) );
    }
}
