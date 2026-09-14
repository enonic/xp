package com.enonic.xp.portal.impl;

import java.util.Arrays;
import java.util.Base64;
import java.util.HexFormat;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import javax.crypto.SecretKey;

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
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HmacServiceTest
{
    private static final NodePath GENERIC_KEY_PATH = NodePath.create().addElement( "keys" ).addElement( "generic-hmac-sha512" ).build();

    @Mock
    private NodeService nodeService;

    @InjectMocks
    private HmacService hmacService;

    @Test
    void derivedKeyMatchesIndependentHkdfSha512Vector()
    {
        when( nodeService.getByPath( GENERIC_KEY_PATH ) ).thenReturn( genericKeyNode() );
        final SecretKey key = hmacService.deriveKey( "image-fingerprint-v3" );
        assertEquals( "HmacSHA512", key.getAlgorithm() );
        assertEquals( 64, key.getEncoded().length );
        assertEquals( "cf893dd121811d9b35c9482e6b6666519b46b52d94c10a762e64ed8ca38b983c4" +
                      "96c8a7960d1ad015eaff57a24653cfed5640cad21aebf7ac73f2eeb76472983",
                      HexFormat.of().formatHex( key.getEncoded() ) );
        assertEquals( key, hmacService.deriveKey( "image-fingerprint-v3" ) );
        assertNotEquals( key, hmacService.deriveKey( "other-purpose" ) );
        assertNotEquals( key, hmacService.deriveKey( "Image-fingerprint-v3" ) );
        final HmacService otherRoot = HmacTestHelper.createHmacService( Base64.getEncoder().encodeToString( new byte[64] ) );
        assertNotEquals( key, otherRoot.deriveKey( "image-fingerprint-v3" ) );
        Arrays.fill( key.getEncoded(), (byte) 0 );
        assertEquals( key, hmacService.deriveKey( "image-fingerprint-v3" ) );
    }

    @Test
    void namedChecksumsAreIsolatedFromOtherNamesAndGenericChecksums()
    {
        when( nodeService.getByPath( GENERIC_KEY_PATH ) ).thenReturn( genericKeyNode() );
        final String value = "https://example.com/";
        final String checksum = hmacService.generateChecksum( "image-fingerprint-v3", value );
        assertEquals( "e745b4236919bcd67a54f0180113e3c55c4ffb54", checksum );
        assertTrue( hmacService.verifyChecksum( "image-fingerprint-v3", value, checksum ) );
        assertFalse( hmacService.verifyChecksum( "other-purpose", value, checksum ) );
        assertFalse( hmacService.verifyChecksum( "image-fingerprint-v3", value + "changed", checksum ) );
        assertFalse( hmacService.verifyChecksum( value, checksum ) );
        assertFalse( hmacService.verifyChecksum( "image-fingerprint-v3", value, hmacService.generateChecksum( value ) ) );
    }

    @Test
    void invalidNamesAreRejected()
    {
        for ( String name : new String[]{"", " ", "image key", "image" + (char) 0 + "key", "é", Character.toString( 0xd800 )} )
        {
            assertThrows( IllegalArgumentException.class, () -> hmacService.deriveKey( name ) );
        }
    }

    @Test
    void concurrentDerivationAndSigningUseIndependentCryptoState()
        throws Exception
    {
        when( nodeService.getByPath( GENERIC_KEY_PATH ) ).thenReturn( genericKeyNode() );
        final var tasks = IntStream.range( 0, 32 ).mapToObj( i -> (Callable<String>) () -> {
            final String name = "purpose-" + i;
            final String value = "value-" + i;
            final String checksum = hmacService.generateChecksum( name, value );
            assertTrue( hmacService.verifyChecksum( name, value, checksum ) );
            return checksum;
        } ).toList();
        try (var executor = Executors.newVirtualThreadPerTaskExecutor())
        {
            final var results = executor.invokeAll( tasks, 10, TimeUnit.SECONDS );
            for ( int i = 0; i < results.size(); i++ )
            {
                assertEquals( hmacService.generateChecksum( "purpose-" + i, "value-" + i ), results.get( i ).get() );
            }
        }
    }

    @Test
    void generateChecksum_returnsExpectedChecksum()
    {
        String redirect = "https://example.com/";
        String expectedChecksum = "cc0fe2d62c8dccdf75b8d07393ecec2b5f8e70e3";

        when( nodeService.getByPath( GENERIC_KEY_PATH ) ).thenReturn( genericKeyNode() );

        String result = hmacService.generateChecksum( redirect );

        assertNotNull( result );
        assertEquals( expectedChecksum, result );
    }

    @Test
    void verifyChecksum_returnsTrueWhenChecksumIsValid()
    {
        String redirect = "https://example.com/";
        String validChecksum = "cc0fe2d62c8dccdf75b8d07393ecec2b5f8e70e3";

        when( nodeService.getByPath( GENERIC_KEY_PATH ) ).thenReturn( genericKeyNode() );

        boolean result = hmacService.verifyChecksum( redirect, validChecksum );

        assertTrue( result );
    }

    @Test
    void verifyChecksum_returnsFalseWhenChecksumIsInvalid()
    {
        String redirect = "https://example.com/";
        String invalidChecksum = "invalidChecksum";

        when( nodeService.getByPath( GENERIC_KEY_PATH ) ).thenReturn( genericKeyNode() );

        boolean result = hmacService.verifyChecksum( redirect, invalidChecksum );

        assertFalse( result );
    }

    private Node genericKeyNode()
    {
        final PropertyTree data = new PropertyTree();
        data.setString( "key", "8Kb/zREmBxlC9KkRcKw8fy7mA/b4tg/MElM1KYgJPWj62PRo74xCcKR88O4tt/51zXO12Ip+AR1lsIxBE5E4VA==" );
        return Node.create().name( GENERIC_KEY_PATH.getName() ).parentPath( GENERIC_KEY_PATH.getParentPath() ).data( data ).build();
    }

}
