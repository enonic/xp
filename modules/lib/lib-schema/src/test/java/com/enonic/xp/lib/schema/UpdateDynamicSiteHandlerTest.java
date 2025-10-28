package com.enonic.xp.lib.schema;

import java.time.Instant;

import org.junit.jupiter.api.Test;

import com.enonic.xp.core.impl.content.parser.YmlCmsDescriptorParser;
import com.enonic.xp.resource.DynamicSchemaResult;
import com.enonic.xp.resource.Resource;
import com.enonic.xp.resource.UpdateDynamicCmsParams;
import com.enonic.xp.site.CmsDescriptor;

import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class UpdateDynamicSiteHandlerTest
    extends BaseSchemaHandlerTest
{
    @Test
    void testSite()
    {
        when( dynamicSchemaService.updateCms( isA( UpdateDynamicCmsParams.class ) ) ).thenAnswer( params -> {
            final UpdateDynamicCmsParams siteParams = params.getArgument( 0, UpdateDynamicCmsParams.class );

            final CmsDescriptor.Builder builder = YmlCmsDescriptorParser.parse( siteParams.getResource(), siteParams.getKey() );

            final Instant modifiedTime = Instant.parse( "2021-09-25T10:00:00.00Z" );
            builder.modifiedTime( modifiedTime );

            final Resource resource = mock( Resource.class );
            when( resource.readString() ).thenReturn( siteParams.getResource() );

            return new DynamicSchemaResult<>( builder.build(), resource );
        } );

        runScript( "/lib/xp/examples/schema/updateSite.js" );
    }

    @Test
    void testInvalidSite()
    {
        runFunction( "/test/UpdateDynamicSiteHandlerTest.js", "updateInvalidSite" );
    }
}
