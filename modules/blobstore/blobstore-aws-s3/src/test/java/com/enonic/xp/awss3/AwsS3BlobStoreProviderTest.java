package com.enonic.xp.awss3;

import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.xp.awss3.config.AwsS3Config;

import static org.junit.Assert.*;

public class AwsS3BlobStoreProviderTest
{
    @Test
    public void invalid_config()
        throws Exception
    {
        final AwsS3Config config = Mockito.mock( AwsS3Config.class );
        Mockito.when( config.isValid() ).thenReturn( false );

        final AwsS3BlobStoreProvider provider = new AwsS3BlobStoreProvider();
        provider.setConfig( config );

        assertNull(provider.get());
    }
}