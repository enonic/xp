package com.enonic.xp.lib.schema;

import java.time.Instant;

import org.junit.jupiter.api.Test;

import com.enonic.xp.core.impl.content.parser.YmlCmsDescriptorParser;
import com.enonic.xp.resource.CreateOrUpdateCmsParams;
import com.enonic.xp.resource.DynamicSchemaResult;
import com.enonic.xp.resource.Resource;
import com.enonic.xp.site.CmsDescriptor;

import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CreateOrUpdateCmsHandlerTest
    extends BaseSchemaHandlerTest
{
    @Test
    void testCms()
    {
        when( dynamicSchemaService.createOrUpdateCms( isA( CreateOrUpdateCmsParams.class ) ) ).thenAnswer( params -> {
            final CreateOrUpdateCmsParams cmsParams = params.getArgument( 0, CreateOrUpdateCmsParams.class );

            final CmsDescriptor.Builder builder = YmlCmsDescriptorParser.parse( cmsParams.getResource(), cmsParams.getKey() );

            final Instant modifiedTime = Instant.parse( "2021-09-25T10:00:00.00Z" );
            builder.modifiedTime( modifiedTime );

            final Resource resource = mock( Resource.class );
            when( resource.readString() ).thenReturn( cmsParams.getResource() );

            return new DynamicSchemaResult<>( builder.build(), resource );
        } );

        runScript( "/lib/xp/examples/schema/createOrUpdateCms.js" );
    }

    @Test
    void testInvalidCms()
    {
        runFunction( "/test/CreateOrUpdateCmsHandlerTest.js", "createInvalidCms" );
    }

    @Test
    void testSystemApplication()
    {
        // the service refuses a system application: the error reaches the script
        when( dynamicSchemaService.createOrUpdateCms( isA( CreateOrUpdateCmsParams.class ) ) ).thenThrow(
            new IllegalArgumentException( "Cannot modify system application: base" ) );

        runFunction( "/test/CreateOrUpdateCmsHandlerTest.js", "createSystemCms" );
    }
}
