package com.enonic.xp.portal.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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

    private RedirectChecksumService redirectChecksumService;

    @BeforeEach
    void setup()
    {
        redirectChecksumService = new RedirectChecksumService( new HmacService( nodeService ) );
    }

    @Test
    void generateChecksum_returnsExpectedChecksum()
    {
        String redirect = "https://example.com/";
        String expectedChecksum = "21b0f177dbf5014444db8df4b0687b8fbc5f36ec";

        when( nodeService.getByPath( GENERIC_KEY_PATH ) ).thenReturn( genericKeyNode() );

        String result = redirectChecksumService.generateChecksum( redirect );

        assertNotNull( result );
        assertEquals( expectedChecksum, result );
    }

    @Test
    void verifyChecksum_returnsTrueWhenChecksumIsValid()
    {
        String redirect = "https://example.com/";
        String validChecksum = "21b0f177dbf5014444db8df4b0687b8fbc5f36ec";

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

    @Test
    void rejectsGenericAndImageKeyChecksums()
    {
        when( nodeService.getByPath( GENERIC_KEY_PATH ) ).thenReturn( genericKeyNode() );
        final HmacService hmac = HmacTestHelper.createHmacService();
        final String redirect = "https://example.com/";
        assertFalse( redirectChecksumService.verifyChecksum( redirect, hmac.generateChecksum( redirect ) ) );
        assertFalse( redirectChecksumService.verifyChecksum( redirect, hmac.generateChecksum( "image-fingerprint-v3", redirect ) ) );
    }

    private Node genericKeyNode()
    {
        final PropertyTree data = new PropertyTree();
        data.setString( "key", "8Kb/zREmBxlC9KkRcKw8fy7mA/b4tg/MElM1KYgJPWj62PRo74xCcKR88O4tt/51zXO12Ip+AR1lsIxBE5E4VA==" );
        return Node.create().name( GENERIC_KEY_PATH.getName() ).parentPath( GENERIC_KEY_PATH.getParentPath() ).data( data ).build();
    }

}
