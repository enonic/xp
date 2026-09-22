package com.enonic.xp.lib.schema;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.descriptor.DescriptorKey;
import com.enonic.xp.form.Form;
import com.enonic.xp.region.PartDescriptor;
import com.enonic.xp.resource.CreateDynamicComponentParams;
import com.enonic.xp.resource.CreateDynamicContentSchemaParams;
import com.enonic.xp.resource.DynamicSchemaResult;
import com.enonic.xp.resource.Resource;
import com.enonic.xp.resource.UpdateDynamicCmsParams;
import com.enonic.xp.resource.UpdateDynamicComponentParams;
import com.enonic.xp.resource.UpdateDynamicContentSchemaParams;
import com.enonic.xp.schema.content.ContentType;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.formfragment.FormFragmentDescriptor;
import com.enonic.xp.schema.formfragment.FormFragmentName;
import com.enonic.xp.schema.mixin.MixinDescriptor;
import com.enonic.xp.schema.mixin.MixinName;
import com.enonic.xp.site.CmsDescriptor;

import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * The generic {@code createSchema}, {@code getComponent}, {@code getSite} etc. are deprecated wrappers that dispatch on
 * {@code type} to the per-type functions. The service is mocked to echo the requested name back.
 */
class DeprecatedFunctionsTest
    extends BaseSchemaHandlerTest
{
    @BeforeEach
    void setUpMocks()
    {
        when( dynamicSchemaService.createContentType( isA( CreateDynamicContentSchemaParams.class ) ) ).thenAnswer(
            invocation -> contentTypeResult(
                (ContentTypeName) invocation.getArgument( 0, CreateDynamicContentSchemaParams.class ).getName() ) );
        when( dynamicSchemaService.updateFormFragment( isA( UpdateDynamicContentSchemaParams.class ) ) ).thenAnswer(
            invocation -> formFragmentResult(
                (FormFragmentName) invocation.getArgument( 0, UpdateDynamicContentSchemaParams.class ).getName() ) );
        when( dynamicSchemaService.getMixin( isA( MixinName.class ) ) ).thenAnswer(
            invocation -> mixinResult( invocation.getArgument( 0, MixinName.class ) ) );
        when( dynamicSchemaService.listContentTypes( isA( ApplicationKey.class ) ) ).thenAnswer(
            invocation -> List.of( contentTypeResult( ContentTypeName.from( "myapp:mytype" ) ) ) );
        when( dynamicSchemaService.deleteContentType( isA( ContentTypeName.class ) ) ).thenReturn( true );

        when( dynamicSchemaService.createPart( isA( CreateDynamicComponentParams.class ) ) ).thenAnswer(
            invocation -> partResult( invocation.getArgument( 0, CreateDynamicComponentParams.class ).getKey() ) );
        when( dynamicSchemaService.updatePart( isA( UpdateDynamicComponentParams.class ) ) ).thenAnswer(
            invocation -> partResult( invocation.getArgument( 0, UpdateDynamicComponentParams.class ).getKey() ) );
        when( dynamicSchemaService.getPart( isA( DescriptorKey.class ) ) ).thenAnswer(
            invocation -> partResult( invocation.getArgument( 0, DescriptorKey.class ) ) );
        when( dynamicSchemaService.listParts( isA( ApplicationKey.class ) ) ).thenAnswer(
            invocation -> List.of( partResult( DescriptorKey.from( "myapp:mypart" ) ) ) );
        when( dynamicSchemaService.deletePart( isA( DescriptorKey.class ) ) ).thenReturn( true );

        when( dynamicSchemaService.getCmsDescriptor( isA( ApplicationKey.class ) ) ).thenAnswer(
            invocation -> cmsResult( invocation.getArgument( 0, ApplicationKey.class ) ) );
        when( dynamicSchemaService.updateCms( isA( UpdateDynamicCmsParams.class ) ) ).thenAnswer(
            invocation -> cmsResult( invocation.getArgument( 0, UpdateDynamicCmsParams.class ).getKey() ) );
    }

    @Test
    void createSchema()
    {
        runFunction( "/test/DeprecatedFunctionsTest.js", "createSchema" );
    }

    @Test
    void updateSchema()
    {
        runFunction( "/test/DeprecatedFunctionsTest.js", "updateSchema" );
    }

    @Test
    void getSchema()
    {
        runFunction( "/test/DeprecatedFunctionsTest.js", "getSchema" );
    }

    @Test
    void listSchemas()
    {
        runFunction( "/test/DeprecatedFunctionsTest.js", "listSchemas" );
    }

    @Test
    void deleteSchema()
    {
        runFunction( "/test/DeprecatedFunctionsTest.js", "deleteSchema" );
    }

    @Test
    void unsupportedSchemaType()
    {
        runFunction( "/test/DeprecatedFunctionsTest.js", "unsupportedSchemaType" );
    }

    @Test
    void createComponent()
    {
        runFunction( "/test/DeprecatedFunctionsTest.js", "createComponent" );
    }

    @Test
    void updateComponent()
    {
        runFunction( "/test/DeprecatedFunctionsTest.js", "updateComponent" );
    }

    @Test
    void getComponent()
    {
        runFunction( "/test/DeprecatedFunctionsTest.js", "getComponent" );
    }

    @Test
    void listComponents()
    {
        runFunction( "/test/DeprecatedFunctionsTest.js", "listComponents" );
    }

    @Test
    void deleteComponent()
    {
        runFunction( "/test/DeprecatedFunctionsTest.js", "deleteComponent" );
    }

    @Test
    void unsupportedComponentType()
    {
        runFunction( "/test/DeprecatedFunctionsTest.js", "unsupportedComponentType" );
    }

    @Test
    void getSite()
    {
        runFunction( "/test/DeprecatedFunctionsTest.js", "getSite" );
    }

    @Test
    void updateSite()
    {
        runFunction( "/test/DeprecatedFunctionsTest.js", "updateSite" );
    }

    private static DynamicSchemaResult<ContentType> contentTypeResult( final ContentTypeName name )
    {
        return new DynamicSchemaResult<>( ContentType.create().name( name ).superType( ContentTypeName.structured() ).build(),
                                          resource() );
    }

    private static DynamicSchemaResult<FormFragmentDescriptor> formFragmentResult( final FormFragmentName name )
    {
        return new DynamicSchemaResult<>( FormFragmentDescriptor.create().name( name ).build(), resource() );
    }

    private static DynamicSchemaResult<MixinDescriptor> mixinResult( final MixinName name )
    {
        return new DynamicSchemaResult<>( MixinDescriptor.create().name( name ).build(), resource() );
    }

    private static DynamicSchemaResult<PartDescriptor> partResult( final DescriptorKey key )
    {
        return new DynamicSchemaResult<>( PartDescriptor.create().key( key ).config( Form.empty() ).build(), resource() );
    }

    private static DynamicSchemaResult<CmsDescriptor> cmsResult( final ApplicationKey key )
    {
        return new DynamicSchemaResult<>( CmsDescriptor.create().applicationKey( key ).build(), resource() );
    }

    private static Resource resource()
    {
        final Resource resource = mock( Resource.class );
        when( resource.readString() ).thenReturn( "" );
        return resource;
    }
}
