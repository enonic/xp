package com.enonic.xp.core.impl.app;

import java.util.Objects;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import com.google.common.io.ByteSource;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.audit.AuditLogService;
import com.enonic.xp.audit.LogAuditLogParams;
import com.enonic.xp.descriptor.DescriptorKey;
import com.enonic.xp.icon.IconUpdate;
import com.enonic.xp.macro.MacroKey;
import com.enonic.xp.resource.CreateOrUpdateCmsParams;
import com.enonic.xp.resource.CreateOrUpdateComponentParams;
import com.enonic.xp.resource.CreateOrUpdateContentSchemaParams;
import com.enonic.xp.resource.CreateOrUpdateMacroParams;
import com.enonic.xp.resource.CreateOrUpdatePhrasesParams;
import com.enonic.xp.resource.CreateOrUpdateStylesParams;
import com.enonic.xp.resource.DeleteDynamicPhrasesParams;
import com.enonic.xp.resource.DynamicComponentType;
import com.enonic.xp.resource.DynamicContentSchemaType;
import com.enonic.xp.resource.DynamicSchemaResult;
import com.enonic.xp.resource.Resource;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.schema.content.ContentTypeName;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class DynamicSchemaAuditLogSupportImplTest
{
    private static final String RESOURCE = "resource-body";

    private DynamicSchemaAuditLogSupportImpl instance;

    private AuditLogService auditLogService;

    @BeforeEach
    void setUp()
    {
        this.auditLogService = mock( AuditLogService.class );

        final SchemaConfig config = mock( SchemaConfig.class, invocation -> invocation.getMethod().getDefaultValue() );

        this.instance = new DynamicSchemaAuditLogSupportImpl( auditLogService );
        this.instance.activate( config );
    }

    private static Resource resourceWithKey( final String resourceKey )
    {
        final Resource resource = mock( Resource.class );
        when( resource.getKey() ).thenReturn( ResourceKey.from( resourceKey ) );
        return resource;
    }

    private static <T> DynamicSchemaResult<T> resultWithKey( final String resourceKey )
    {
        return new DynamicSchemaResult<>( null, resourceWithKey( resourceKey ) );
    }

    private LogAuditLogParams captureLog()
    {
        final ArgumentCaptor<LogAuditLogParams> captor = ArgumentCaptor.forClass( LogAuditLogParams.class );
        verify( auditLogService ).log( captor.capture() );
        return captor.getValue();
    }

    private static String firstUri( final LogAuditLogParams value )
    {
        return value.getObjectUris().stream().map( Objects::toString ).findFirst().orElseThrow();
    }

    @Test
    void createComponent()
    {
        final CreateOrUpdateComponentParams params =
            CreateOrUpdateComponentParams.create().key( DescriptorKey.from( "myapp:mypart" ) ).resource( RESOURCE ).build();

        instance.createComponent( params, DynamicComponentType.PART, resultWithKey( "myapp:/cms/parts/mypart/mypart.yaml" ) );

        final LogAuditLogParams value = captureLog();
        assertEquals( "system.schema.component.create", value.getType() );
        assertEquals( "com.enonic.xp.schema", value.getSource() );
        assertEquals( "myapp:/cms/parts/mypart/mypart.yaml", firstUri( value ) );
        assertEquals( "myapp:mypart", value.getData().getSet( "params" ).getString( "key" ) );
        assertEquals( "PART", value.getData().getSet( "params" ).getString( "type" ) );
        assertEquals( RESOURCE, value.getData().getSet( "params" ).getString( "resource" ) );
        assertNull( value.getData().getSet( "params" ).getSet( "icon" ) );
        assertNull( value.getData().getSet( "params" ).getBoolean( "iconRemoved" ) );
    }

    @Test
    void updateComponent()
    {
        final CreateOrUpdateComponentParams params =
            CreateOrUpdateComponentParams.create().key( DescriptorKey.from( "myapp:mypart" ) ).resource( RESOURCE ).build();

        instance.updateComponent( params, DynamicComponentType.PART, resultWithKey( "myapp:/cms/parts/mypart/mypart.yaml" ) );

        final LogAuditLogParams value = captureLog();
        assertEquals( "system.schema.component.update", value.getType() );
        assertEquals( "myapp:/cms/parts/mypart/mypart.yaml", firstUri( value ) );
    }

    // the icon set along with the descriptor is logged by its mime type and size; the resource is left out when only the icon changes
    @Test
    void updateComponentIcon()
    {
        final CreateOrUpdateComponentParams params = CreateOrUpdateComponentParams.create()
            .key( DescriptorKey.from( "myapp:mypart" ) )
            .icon( IconUpdate.replace( ByteSource.wrap( new byte[6] ), "image/png" ) )
            .build();

        instance.updateComponent( params, DynamicComponentType.PART, resultWithKey( "myapp:/cms/parts/mypart/mypart.yaml" ) );

        final LogAuditLogParams value = captureLog();
        assertEquals( "system.schema.component.update", value.getType() );
        assertNull( value.getData().getSet( "params" ).getString( "resource" ) );
        assertEquals( "image/png", value.getData().getSet( "params" ).getSet( "icon" ).getString( "mimeType" ) );
        assertEquals( 6L, value.getData().getSet( "params" ).getSet( "icon" ).getLong( "size" ) );
    }

    @Test
    void updateComponentRemovingIcon()
    {
        final CreateOrUpdateComponentParams params =
            CreateOrUpdateComponentParams.create().key( DescriptorKey.from( "myapp:mypart" ) ).icon( IconUpdate.REMOVE ).build();

        instance.updateComponent( params, DynamicComponentType.PART, resultWithKey( "myapp:/cms/parts/mypart/mypart.yaml" ) );

        final LogAuditLogParams value = captureLog();
        assertNull( value.getData().getSet( "params" ).getSet( "icon" ) );
        assertEquals( Boolean.TRUE, value.getData().getSet( "params" ).getBoolean( "iconRemoved" ) );
    }

    @Test
    void deleteComponent()
    {
        instance.deleteComponent( DescriptorKey.from( "myapp:mypart" ), DynamicComponentType.PART );

        final LogAuditLogParams value = captureLog();
        assertEquals( "system.schema.component.delete", value.getType() );
        assertEquals( "myapp:mypart", firstUri( value ) );
        assertEquals( "PART", value.getData().getSet( "params" ).getString( "type" ) );
    }

    @Test
    void createContentSchema()
    {
        final CreateOrUpdateContentSchemaParams params = CreateOrUpdateContentSchemaParams.create()
            .name( ContentTypeName.from( "myapp:mytype" ) )
            .resource( RESOURCE )
            .icon( IconUpdate.replace( ByteSource.wrap( new byte[42] ), "image/svg+xml" ) )
            .build();

        instance.createContentSchema( params, DynamicContentSchemaType.CONTENT_TYPE,
                                      resultWithKey( "myapp:/cms/content-types/mytype/mytype.yaml" ) );

        final LogAuditLogParams value = captureLog();
        assertEquals( "system.schema.contentSchema.create", value.getType() );
        assertEquals( "myapp:/cms/content-types/mytype/mytype.yaml", firstUri( value ) );
        assertEquals( "myapp:mytype", value.getData().getSet( "params" ).getString( "name" ) );
        assertEquals( "CONTENT_TYPE", value.getData().getSet( "params" ).getString( "type" ) );
        assertEquals( RESOURCE, value.getData().getSet( "params" ).getString( "resource" ) );
        assertEquals( "image/svg+xml", value.getData().getSet( "params" ).getSet( "icon" ).getString( "mimeType" ) );
        assertEquals( 42L, value.getData().getSet( "params" ).getSet( "icon" ).getLong( "size" ) );
    }

    @Test
    void updateContentSchema()
    {
        final CreateOrUpdateContentSchemaParams params =
            CreateOrUpdateContentSchemaParams.create().name( ContentTypeName.from( "myapp:mytype" ) ).resource( RESOURCE ).build();

        instance.updateContentSchema( params, DynamicContentSchemaType.CONTENT_TYPE,
                                      resultWithKey( "myapp:/cms/content-types/mytype/mytype.yaml" ) );

        assertEquals( "system.schema.contentSchema.update", captureLog().getType() );
    }

    @Test
    void deleteContentSchema()
    {
        instance.deleteContentSchema( ContentTypeName.from( "myapp:mytype" ), DynamicContentSchemaType.CONTENT_TYPE );

        final LogAuditLogParams value = captureLog();
        assertEquals( "system.schema.contentSchema.delete", value.getType() );
        assertEquals( "myapp:mytype", firstUri( value ) );
    }

    @Test
    void createCms()
    {
        final CreateOrUpdateCmsParams params =
            CreateOrUpdateCmsParams.create().key( ApplicationKey.from( "myapp" ) ).resource( RESOURCE ).build();

        instance.createCms( params, resultWithKey( "myapp:/cms/cms.yaml" ) );

        final LogAuditLogParams value = captureLog();
        assertEquals( "system.schema.cms.create", value.getType() );
        assertEquals( "myapp:/cms/cms.yaml", firstUri( value ) );
        assertEquals( "myapp", value.getData().getSet( "params" ).getString( "application" ) );
        assertEquals( RESOURCE, value.getData().getSet( "params" ).getString( "resource" ) );
    }

    @Test
    void updateCms()
    {
        final CreateOrUpdateCmsParams params =
            CreateOrUpdateCmsParams.create().key( ApplicationKey.from( "myapp" ) ).resource( RESOURCE ).build();

        instance.updateCms( params, resultWithKey( "myapp:/cms/cms.yaml" ) );

        assertEquals( "system.schema.cms.update", captureLog().getType() );
    }

    @Test
    void createApplicationDescriptor()
    {
        instance.createApplicationDescriptor( ApplicationKey.from( "myapp" ), RESOURCE, "image/svg+xml", 42 );

        final LogAuditLogParams value = captureLog();
        assertEquals( "system.schema.application.create", value.getType() );
        assertEquals( "myapp:/enonic.yaml", firstUri( value ) );
        assertEquals( "myapp", value.getData().getSet( "params" ).getString( "application" ) );
        assertEquals( RESOURCE, value.getData().getSet( "params" ).getString( "resource" ) );
        assertEquals( "image/svg+xml", value.getData().getSet( "params" ).getSet( "icon" ).getString( "mimeType" ) );
        assertEquals( 42L, value.getData().getSet( "params" ).getSet( "icon" ).getLong( "size" ) );
    }

    @Test
    void updateApplicationDescriptor()
    {
        instance.updateApplicationDescriptor( ApplicationKey.from( "myapp" ), RESOURCE, null, 0, true );

        final LogAuditLogParams value = captureLog();
        assertEquals( "system.schema.application.update", value.getType() );
        assertEquals( "myapp:/enonic.yaml", firstUri( value ) );
        assertEquals( RESOURCE, value.getData().getSet( "params" ).getString( "resource" ) );
        assertNull( value.getData().getSet( "params" ).getSet( "icon" ) );
        assertEquals( Boolean.TRUE, value.getData().getSet( "params" ).getBoolean( "iconRemoved" ) );
    }

    @Test
    void deleteCms()
    {
        instance.deleteCms( ApplicationKey.from( "myapp" ) );

        final LogAuditLogParams value = captureLog();
        assertEquals( "system.schema.cms.delete", value.getType() );
        assertEquals( "myapp", firstUri( value ) );
    }

    @Test
    void createStyles()
    {
        final CreateOrUpdateStylesParams params =
            CreateOrUpdateStylesParams.create().key( ApplicationKey.from( "myapp" ) ).resource( RESOURCE ).build();

        instance.createStyles( params, resultWithKey( "myapp:/cms/styles/styles.yaml" ) );

        final LogAuditLogParams value = captureLog();
        assertEquals( "system.schema.styles.create", value.getType() );
        assertEquals( "myapp:/cms/styles/styles.yaml", firstUri( value ) );
        assertEquals( "myapp", value.getData().getSet( "params" ).getString( "application" ) );
        assertEquals( RESOURCE, value.getData().getSet( "params" ).getString( "resource" ) );
    }

    @Test
    void updateStyles()
    {
        final CreateOrUpdateStylesParams params =
            CreateOrUpdateStylesParams.create().key( ApplicationKey.from( "myapp" ) ).resource( RESOURCE ).build();

        instance.updateStyles( params, resultWithKey( "myapp:/cms/styles/styles.yaml" ) );

        assertEquals( "system.schema.styles.update", captureLog().getType() );
    }

    @Test
    void deleteStyles()
    {
        instance.deleteStyles( ApplicationKey.from( "myapp" ) );

        final LogAuditLogParams value = captureLog();
        assertEquals( "system.schema.styles.delete", value.getType() );
        assertEquals( "myapp", firstUri( value ) );
    }

    @Test
    void createMacro()
    {
        final CreateOrUpdateMacroParams params =
            CreateOrUpdateMacroParams.create().key( MacroKey.from( "myapp:mymacro" ) ).resource( RESOURCE ).build();

        instance.createMacro( params, resultWithKey( "myapp:/cms/macros/mymacro/mymacro.yaml" ) );

        final LogAuditLogParams value = captureLog();
        assertEquals( "system.schema.macro.create", value.getType() );
        assertEquals( "myapp:/cms/macros/mymacro/mymacro.yaml", firstUri( value ) );
        assertEquals( "myapp:mymacro", value.getData().getSet( "params" ).getString( "key" ) );
        assertEquals( RESOURCE, value.getData().getSet( "params" ).getString( "resource" ) );
    }

    @Test
    void updateMacroIcon()
    {
        final CreateOrUpdateMacroParams params = CreateOrUpdateMacroParams.create()
            .key( MacroKey.from( "myapp:mymacro" ) )
            .icon( IconUpdate.replace( ByteSource.wrap( new byte[6] ), "image/png" ) )
            .build();

        instance.updateMacro( params, resultWithKey( "myapp:/cms/macros/mymacro/mymacro.yaml" ) );

        final LogAuditLogParams value = captureLog();
        assertEquals( "system.schema.macro.update", value.getType() );
        assertNull( value.getData().getSet( "params" ).getString( "resource" ) );
        assertEquals( "image/png", value.getData().getSet( "params" ).getSet( "icon" ).getString( "mimeType" ) );
        assertEquals( 6L, value.getData().getSet( "params" ).getSet( "icon" ).getLong( "size" ) );
    }

    @Test
    void deleteMacro()
    {
        instance.deleteMacro( MacroKey.from( "myapp:mymacro" ) );

        final LogAuditLogParams value = captureLog();
        assertEquals( "system.schema.macro.delete", value.getType() );
        assertEquals( "myapp:mymacro", firstUri( value ) );
    }

    @Test
    void createPhrases()
    {
        final CreateOrUpdatePhrasesParams params =
            CreateOrUpdatePhrasesParams.create().key( ApplicationKey.from( "myapp" ) ).name( "phrases_en" ).resource( RESOURCE ).build();

        instance.createPhrases( params, resourceWithKey( "myapp:/cms/i18n/phrases/phrases_en.properties" ) );

        final LogAuditLogParams value = captureLog();
        assertEquals( "system.schema.phrases.create", value.getType() );
        assertEquals( "myapp:/cms/i18n/phrases/phrases_en.properties", firstUri( value ) );
        assertEquals( "myapp", value.getData().getSet( "params" ).getString( "application" ) );
        assertEquals( "phrases_en", value.getData().getSet( "params" ).getString( "name" ) );
        assertEquals( RESOURCE, value.getData().getSet( "params" ).getString( "resource" ) );
    }

    @Test
    void updatePhrases()
    {
        final CreateOrUpdatePhrasesParams params =
            CreateOrUpdatePhrasesParams.create().key( ApplicationKey.from( "myapp" ) ).name( "phrases_en" ).resource( RESOURCE ).build();

        instance.updatePhrases( params, resourceWithKey( "myapp:/cms/i18n/phrases/phrases_en.properties" ) );

        assertEquals( "system.schema.phrases.update", captureLog().getType() );
    }

    @Test
    void deletePhrases()
    {
        final DeleteDynamicPhrasesParams params =
            DeleteDynamicPhrasesParams.create().key( ApplicationKey.from( "myapp" ) ).name( "phrases_en" ).build();

        instance.deletePhrases( params );

        final LogAuditLogParams value = captureLog();
        assertEquals( "system.schema.phrases.delete", value.getType() );
        assertEquals( "myapp", firstUri( value ) );
        assertEquals( "phrases_en", value.getData().getSet( "params" ).getString( "name" ) );
    }

    @Test
    void auditLogDisabled()
    {
        final SchemaConfig disabledConfig = mock( SchemaConfig.class, invocation -> invocation.getMethod().getDefaultValue() );
        when( disabledConfig.auditlog_enabled() ).thenReturn( false );
        instance.activate( disabledConfig );

        instance.deleteCms( ApplicationKey.from( "myapp" ) );

        verify( auditLogService, never() ).log( any() );
    }
}