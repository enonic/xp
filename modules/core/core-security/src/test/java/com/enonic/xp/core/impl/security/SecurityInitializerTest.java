package com.enonic.xp.core.impl.security;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.enonic.xp.index.IndexService;
import com.enonic.xp.node.NodeService;
import com.enonic.xp.security.CreateIdProviderParams;
import com.enonic.xp.security.PrincipalKeys;
import com.enonic.xp.security.SecurityService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SecurityInitializerTest
{
    @Mock(stubOnly = true)
    private IndexService indexService;

    @Mock
    private NodeService nodeService;

    @Mock
    private SecurityService securityService;

    @Test
    void system_id_provider_created_without_first_login_config()
    {
        when( securityService.getMemberships( any() ) ).thenReturn( PrincipalKeys.empty() );

        final SecurityInitializer initializer = SecurityInitializer.create()
            .setIndexService( indexService )
            .setNodeService( nodeService )
            .setSecurityService( securityService )
            .build();

        initializer.doInitialize();

        final ArgumentCaptor<CreateIdProviderParams> captor = ArgumentCaptor.forClass( CreateIdProviderParams.class );
        verify( securityService ).createIdProvider( captor.capture() );

        assertThat( captor.getValue().getIdProviderConfig() ).isNull();
    }
}