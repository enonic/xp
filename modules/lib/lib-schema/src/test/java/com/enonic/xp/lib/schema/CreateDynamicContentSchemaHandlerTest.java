package com.enonic.xp.lib.schema;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import org.junit.jupiter.api.Test;

import com.enonic.xp.icon.Icon;
import com.enonic.xp.resource.CreateDynamicContentSchemaParams;
import com.enonic.xp.resource.DynamicSchemaResult;
import com.enonic.xp.resource.Resource;
import com.enonic.xp.schema.content.ContentType;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.mixin.Mixin;
import com.enonic.xp.schema.mixin.MixinName;
import com.enonic.xp.schema.xdata.XData;
import com.enonic.xp.schema.xdata.XDataName;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.xml.parser.XmlContentTypeParser;
import com.enonic.xp.xml.parser.XmlMixinParser;
import com.enonic.xp.xml.parser.XmlXDataParser;

import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CreateDynamicContentSchemaHandlerTest
    extends BaseSchemaHandlerTest
{
    @Test
    public void testContentType()
    {
        when( dynamicSchemaService.createContentSchema( isA( CreateDynamicContentSchemaParams.class ) ) ).thenAnswer( params -> {
            final CreateDynamicContentSchemaParams schemaParams = params.getArgument( 0, CreateDynamicContentSchemaParams.class );

            final XmlContentTypeParser parser = new XmlContentTypeParser();

            final ContentType.Builder builder = ContentType.create();

            final Instant modifiedTime = Instant.parse( "2021-09-25T10:00:00.00Z" );
            builder.modifiedTime( modifiedTime );
            builder.createdTime( modifiedTime );

            builder.name( ContentTypeName.from( schemaParams.getName().getApplicationKey(), schemaParams.getName().getLocalName() ) );

            parser.builder( builder );
            parser.source( schemaParams.getResource() );
            parser.currentApplication( schemaParams.getName().getApplicationKey() );

            parser.parse();

            final Resource resource = mock( Resource.class );
            when( resource.readString() ).thenReturn( schemaParams.getResource() );

            byte[] data = new byte[]{1, 2, 3, 4, 5, 6};
            final Instant ts = LocalDateTime.of( 2016, 1, 1, 12, 0, 0 ).toInstant( ZoneOffset.UTC );
            Icon icon = Icon.from( data, "image/png", ts );

            builder.icon( icon );
            builder.creator( PrincipalKey.ofAnonymous() );

            return new DynamicSchemaResult<ContentType>( builder.build(), resource );
        } );

        runScript( "/lib/xp/examples/schema/createContentType.js" );
    }

    @Test
    public void testMixin()
    {
        when( dynamicSchemaService.createContentSchema( isA( CreateDynamicContentSchemaParams.class ) ) ).thenAnswer( params -> {
            final CreateDynamicContentSchemaParams schemaParams = params.getArgument( 0, CreateDynamicContentSchemaParams.class );

            final XmlMixinParser parser = new XmlMixinParser();

            Mixin.Builder builder = Mixin.create();

            final Instant modifiedTime = Instant.parse( "2021-09-25T10:00:00.00Z" );
            builder.modifiedTime( modifiedTime );
            builder.createdTime( modifiedTime );

            builder.name( MixinName.from( schemaParams.getName().getApplicationKey(), schemaParams.getName().getLocalName() ) );

            parser.builder( builder );
            parser.source( schemaParams.getResource() );
            parser.currentApplication( schemaParams.getName().getApplicationKey() );

            parser.parse();

            final Resource resource = mock( Resource.class );
            when( resource.readString() ).thenReturn( schemaParams.getResource() );

            return new DynamicSchemaResult<Mixin>( builder.build(), resource );
        } );

        runScript( "/lib/xp/examples/schema/createMixin.js" );
    }

    @Test
    public void testXData()
    {
        when( dynamicSchemaService.createContentSchema( isA( CreateDynamicContentSchemaParams.class ) ) ).thenAnswer( params -> {
            final CreateDynamicContentSchemaParams schemaParams = params.getArgument( 0, CreateDynamicContentSchemaParams.class );

            final XmlXDataParser parser = new XmlXDataParser();

            XData.Builder builder = XData.create();

            final Instant modifiedTime = Instant.parse( "2021-09-25T10:00:00.00Z" );
            builder.modifiedTime( modifiedTime );
            builder.createdTime( modifiedTime );

            builder.name( XDataName.from( schemaParams.getName().getApplicationKey(), schemaParams.getName().getLocalName() ) );

            parser.builder( builder );
            parser.source( schemaParams.getResource() );
            parser.currentApplication( schemaParams.getName().getApplicationKey() );

            parser.parse();

            final Resource resource = mock( Resource.class );
            when( resource.readString() ).thenReturn( schemaParams.getResource() );

            return new DynamicSchemaResult<XData>( builder.build(), resource );
        } );

        runScript( "/lib/xp/examples/schema/createXData.js" );
    }

    @Test
    public void testInvalidContentSchemaType()
    {
        runFunction( "/test/CreateDynamicContentSchemaHandlerTest.js", "createInvalidContentSchemaType" );
    }

    @Test
    public void testInvalidContentSchema()
    {
        runFunction( "/test/CreateDynamicContentSchemaHandlerTest.js", "createInvalidContentSchema" );
    }


}
