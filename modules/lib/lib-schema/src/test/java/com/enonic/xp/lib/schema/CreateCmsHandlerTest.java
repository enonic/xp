package com.enonic.xp.lib.schema;

import java.time.Instant;

import org.junit.jupiter.api.Test;

import com.enonic.xp.core.impl.schema.parser.YmlCmsDescriptorParser;
import com.enonic.xp.schema.CreateCmsParams;
import com.enonic.xp.schema.SchemaResult;
import com.enonic.xp.resource.Resource;
import com.enonic.xp.site.CmsDescriptor;

import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CreateCmsHandlerTest
    extends BaseSchemaHandlerTest
{
    @Test
    void testCreateCms()
    {
        when( schemaService.createCms( isA( CreateCmsParams.class ) ) ).thenAnswer( params -> {
            final CreateCmsParams cmsParams = params.getArgument( 0, CreateCmsParams.class );

            final CmsDescriptor.Builder builder = YmlCmsDescriptorParser.parse( cmsParams.getResource(), cmsParams.getKey() );

            final Instant modifiedTime = Instant.parse( "2021-09-25T10:00:00.00Z" );
            builder.modifiedTime( modifiedTime );

            final Resource resource = mock( Resource.class );
            when( resource.readString() ).thenReturn( cmsParams.getResource() );

            return new SchemaResult<>( builder.build(), resource );
        } );

        runScript( "/lib/xp/examples/schema/createCms.js" );
    }
}
