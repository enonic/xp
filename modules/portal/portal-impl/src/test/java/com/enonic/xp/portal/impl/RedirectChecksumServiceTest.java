package com.enonic.xp.portal.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RedirectChecksumServiceTest
{
    private static final NodePath GENERIC_KEY_PATH = NodePath.create().addElement( "keys" ).addElement( "generic-hmac-sha512" ).build();

    @Mock
    private NodeService nodeService;

    @InjectMocks
    private RedirectChecksumService redirectChecksumService;

    @Test
    void generateChecksum_returnsExpectedChecksum()
    {
        String redirect = "https://example.com/";
        String expectedChecksum = "cc0fe2d62c8dccdf75b8d07393ecec2b5f8e70e3";

        when( nodeService.getByPath( GENERIC_KEY_PATH ) ).thenReturn( genericKeyNode() );

        String result = redirectChecksumService.generateChecksum( redirect );

        assertNotNull( result );
        assertEquals( expectedChecksum, result );
    }

    @Test
    void verifyChecksum_returnsTrueWhenChecksumIsValid()
    {
        String redirect = "https://example.com/";
        String validChecksum = "cc0fe2d62c8dccdf75b8d07393ecec2b5f8e70e3";

        when( nodeService.getByPath( GENERIC_KEY_PATH ) ).thenReturn( genericKeyNode() );

        boolean result = redirectChecksumService.verifyChecksum( redirect, validChecksum );

        assertTrue( result );
    }

    @Test
    void verifyChecksum_returnsFalseWhenChecksumIsInvalid()
    {
        String redirect = "https://example.com/";
        String invalidChecksum = "invalidChecksum";

        when( nodeService.getByPath( GENERIC_KEY_PATH ) ).thenReturn( genericKeyNode() );

        boolean result = redirectChecksumService.verifyChecksum( redirect, invalidChecksum );

        assertFalse( result );
    }

    private Node genericKeyNode()
    {
        final PropertyTree data = new PropertyTree();
        data.setString( "key", "8Kb/zREmBxlC9KkRcKw8fy7mA/b4tg/MElM1KYgJPWj62PRo74xCcKR88O4tt/51zXO12Ip+AR1lsIxBE5E4VA==" );
        return Node.create().name( GENERIC_KEY_PATH.getName() ).parentPath( GENERIC_KEY_PATH.getParentPath() ).data( data ).build();
    }

}
