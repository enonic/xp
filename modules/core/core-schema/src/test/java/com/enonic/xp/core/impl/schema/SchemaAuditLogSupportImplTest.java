package com.enonic.xp.core.impl.schema;

import java.util.Objects;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.CreateNamespaceParams;
import com.enonic.xp.app.Namespace;
import com.enonic.xp.app.UpdateNamespaceParams;
import com.enonic.xp.audit.AuditLogService;
import com.enonic.xp.audit.LogAuditLogParams;
import com.enonic.xp.descriptor.DescriptorKey;
import com.enonic.xp.macro.MacroKey;
import com.enonic.xp.schema.CreateCmsParams;
import com.enonic.xp.schema.CreateComponentParams;
import com.enonic.xp.schema.CreateContentSchemaParams;
import com.enonic.xp.schema.CreateMacroParams;
import com.enonic.xp.schema.CreatePhrasesParams;
import com.enonic.xp.schema.CreateStylesParams;
import com.enonic.xp.schema.DeleteMacroParams;
import com.enonic.xp.schema.DeletePhrasesParams;
import com.enonic.xp.schema.SchemaResult;
import com.enonic.xp.resource.Resource;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.schema.UpdateCmsParams;
import com.enonic.xp.schema.UpdateComponentParams;
import com.enonic.xp.schema.UpdateContentSchemaParams;
import com.enonic.xp.schema.UpdateMacroParams;
import com.enonic.xp.schema.UpdatePhrasesParams;
import com.enonic.xp.schema.UpdateStylesParams;
import com.enonic.xp.schema.content.ContentTypeName;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;

class SchemaAuditLogSupportImplTest
{
    private static final String RESOURCE = "resource-body";

    private SchemaAuditLogSupportImpl instance;

    private AuditLogService auditLogService;

    @BeforeEach
    void setUp()
    {
        this.auditLogService = mock( AuditLogService.class );

        final SchemaConfig config = mock( SchemaConfig.class, invocation -> invocation.getMethod().getDefaultValue() );

        this.instance = new SchemaAuditLogSupportImpl( auditLogService );
        this.instance.activate( config );
    }

    @SuppressWarnings("unchecked")
    private <T> SchemaResult<T> resultWithKey( final String resourceKey )
    {
        final Resource resource = mock( Resource.class );
        when( resource.getKey() ).thenReturn( ResourceKey.from( resourceKey ) );

        final SchemaResult<T> result = mock( SchemaResult.class );
        when( result.getResource() ).thenReturn( resource );

        return result;
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
        final CreateComponentParams params = CreateComponentParams.create()
            .descriptorKey( DescriptorKey.from( "myapp:mypart" ) )
            .resource( RESOURCE )
            .build();

        instance.createComponent( params, ComponentType.PART, resultWithKey( "myapp:/cms/parts/mypart/mypart.yaml" ) );

        final LogAuditLogParams value = captureLog();
        assertEquals( "system.schema.component.create", value.getType() );
        assertEquals( "myapp:/cms/parts/mypart/mypart.yaml", firstUri( value ) );
        assertEquals( "myapp:mypart", value.getData().getSet( "params" ).getString( "key" ) );
        assertEquals( "PART", value.getData().getSet( "params" ).getString( "type" ) );
        assertEquals( RESOURCE, value.getData().getSet( "params" ).getString( "resource" ) );
    }

    @Test
    void updateComponent()
    {
        final UpdateComponentParams params = UpdateComponentParams.create()
            .descriptorKey( DescriptorKey.from( "myapp:mypart" ) )
            .resource( RESOURCE )
            .build();

        instance.updateComponent( params, ComponentType.PART, resultWithKey( "myapp:/cms/parts/mypart/mypart.yaml" ) );

        final LogAuditLogParams value = captureLog();
        assertEquals( "system.schema.component.update", value.getType() );
        assertEquals( "myapp:/cms/parts/mypart/mypart.yaml", firstUri( value ) );
    }

    @Test
    void deleteComponent()
    {
        instance.deleteComponent( DescriptorKey.from( "myapp:mypart" ), ComponentType.PART );

        final LogAuditLogParams value = captureLog();
        assertEquals( "system.schema.component.delete", value.getType() );
        assertEquals( "myapp:mypart", firstUri( value ) );
        assertEquals( "PART", value.getData().getSet( "params" ).getString( "type" ) );
    }

    @Test
    void createContentSchema()
    {
        final CreateContentSchemaParams params = CreateContentSchemaParams.create()
            .name( ContentTypeName.from( "myapp:mytype" ) )
            .resource( RESOURCE )
            .build();

        instance.createContentSchema( params, ContentSchemaType.CONTENT_TYPE, resultWithKey( "myapp:/cms/content-types/mytype/mytype.yaml" ) );

        final LogAuditLogParams value = captureLog();
        assertEquals( "system.schema.contentSchema.create", value.getType() );
        assertEquals( "myapp:/cms/content-types/mytype/mytype.yaml", firstUri( value ) );
        assertEquals( "myapp:mytype", value.getData().getSet( "params" ).getString( "name" ) );
        assertEquals( "CONTENT_TYPE", value.getData().getSet( "params" ).getString( "type" ) );
    }

    @Test
    void updateContentSchema()
    {
        final UpdateContentSchemaParams params = UpdateContentSchemaParams.create()
            .name( ContentTypeName.from( "myapp:mytype" ) )
            .resource( RESOURCE )
            .build();

        instance.updateContentSchema( params, ContentSchemaType.CONTENT_TYPE, resultWithKey( "myapp:/cms/content-types/mytype/mytype.yaml" ) );

        assertEquals( "system.schema.contentSchema.update", captureLog().getType() );
    }

    @Test
    void deleteContentSchema()
    {
        instance.deleteContentSchema( ContentTypeName.from( "myapp:mytype" ), ContentSchemaType.CONTENT_TYPE );

        final LogAuditLogParams value = captureLog();
        assertEquals( "system.schema.contentSchema.delete", value.getType() );
        assertEquals( "myapp:mytype", firstUri( value ) );
    }

    @Test
    void createCms()
    {
        final CreateCmsParams params =
            CreateCmsParams.create().key( ApplicationKey.from( "myapp" ) ).resource( RESOURCE ).build();

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
        final UpdateCmsParams params =
            UpdateCmsParams.create().key( ApplicationKey.from( "myapp" ) ).resource( RESOURCE ).build();

        instance.updateCms( params, resultWithKey( "myapp:/cms/cms.yaml" ) );

        assertEquals( "system.schema.cms.update", captureLog().getType() );
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
        final CreateStylesParams params =
            CreateStylesParams.create().key( ApplicationKey.from( "myapp" ) ).resource( RESOURCE ).build();

        instance.createStyles( params, resultWithKey( "myapp:/cms/styles/styles.yaml" ) );

        final LogAuditLogParams value = captureLog();
        assertEquals( "system.schema.styles.create", value.getType() );
        assertEquals( "myapp:/cms/styles/styles.yaml", firstUri( value ) );
    }

    @Test
    void updateStyles()
    {
        final UpdateStylesParams params =
            UpdateStylesParams.create().key( ApplicationKey.from( "myapp" ) ).resource( RESOURCE ).build();

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
        final CreateMacroParams params =
            CreateMacroParams.create().key( MacroKey.from( "myapp:mymacro" ) ).resource( RESOURCE ).build();

        instance.createMacro( params, resultWithKey( "myapp:/cms/macros/mymacro/mymacro.yaml" ) );

        final LogAuditLogParams value = captureLog();
        assertEquals( "system.schema.macro.create", value.getType() );
        assertEquals( "myapp:/cms/macros/mymacro/mymacro.yaml", firstUri( value ) );
        assertEquals( "myapp:mymacro", value.getData().getSet( "params" ).getString( "key" ) );
    }

    @Test
    void updateMacro()
    {
        final UpdateMacroParams params =
            UpdateMacroParams.create().key( MacroKey.from( "myapp:mymacro" ) ).resource( RESOURCE ).build();

        instance.updateMacro( params, resultWithKey( "myapp:/cms/macros/mymacro/mymacro.yaml" ) );

        assertEquals( "system.schema.macro.update", captureLog().getType() );
    }

    @Test
    void deleteMacro()
    {
        final DeleteMacroParams params = DeleteMacroParams.create().key( MacroKey.from( "myapp:mymacro" ) ).build();

        instance.deleteMacro( params );

        final LogAuditLogParams value = captureLog();
        assertEquals( "system.schema.macro.delete", value.getType() );
        assertEquals( "myapp:mymacro", firstUri( value ) );
    }

    @Test
    void createPhrases()
    {
        final CreatePhrasesParams params = CreatePhrasesParams.create()
            .key( ApplicationKey.from( "myapp" ) )
            .name( "phrases_en" )
            .resource( RESOURCE )
            .build();

        final Resource resource = mock( Resource.class );
        when( resource.getKey() ).thenReturn( ResourceKey.from( "myapp:/cms/i18n/phrases/phrases_en.properties" ) );

        instance.createPhrases( params, resource );

        final LogAuditLogParams value = captureLog();
        assertEquals( "system.schema.phrases.create", value.getType() );
        assertEquals( "myapp:/cms/i18n/phrases/phrases_en.properties", firstUri( value ) );
        assertEquals( "phrases_en", value.getData().getSet( "params" ).getString( "name" ) );
    }

    @Test
    void updatePhrases()
    {
        final UpdatePhrasesParams params = UpdatePhrasesParams.create()
            .key( ApplicationKey.from( "myapp" ) )
            .name( "phrases_en" )
            .resource( RESOURCE )
            .build();

        final Resource resource = mock( Resource.class );
        when( resource.getKey() ).thenReturn( ResourceKey.from( "myapp:/cms/i18n/phrases/phrases_en.properties" ) );

        instance.updatePhrases( params, resource );

        assertEquals( "system.schema.phrases.update", captureLog().getType() );
    }

    @Test
    void deletePhrases()
    {
        final DeletePhrasesParams params =
            DeletePhrasesParams.create().key( ApplicationKey.from( "myapp" ) ).name( "phrases_en" ).build();

        instance.deletePhrases( params );

        final LogAuditLogParams value = captureLog();
        assertEquals( "system.schema.phrases.delete", value.getType() );
        assertEquals( "myapp", firstUri( value ) );
        assertEquals( "phrases_en", value.getData().getSet( "params" ).getString( "name" ) );
    }

    @Test
    void createNamespace()
    {
        final CreateNamespaceParams params =
            CreateNamespaceParams.create().key( ApplicationKey.from( "myapp" ) ).description( "my namespace" ).build();
        final Namespace namespace = Namespace.create().key( ApplicationKey.from( "myapp" ) ).description( "my namespace" ).build();

        instance.createNamespace( params, namespace );

        final LogAuditLogParams value = captureLog();
        assertEquals( "system.schema.namespace.create", value.getType() );
        assertEquals( "myapp", firstUri( value ) );
        assertEquals( "my namespace", value.getData().getSet( "params" ).getString( "description" ) );
    }

    @Test
    void updateNamespace()
    {
        final UpdateNamespaceParams params = UpdateNamespaceParams.create().key( ApplicationKey.from( "myapp" ) ).build();
        final Namespace namespace = Namespace.create().key( ApplicationKey.from( "myapp" ) ).build();

        instance.updateNamespace( params, namespace );

        final LogAuditLogParams value = captureLog();
        assertEquals( "system.schema.namespace.update", value.getType() );
        assertEquals( "myapp", firstUri( value ) );
    }

    @Test
    void deleteNamespace()
    {
        instance.deleteNamespace( ApplicationKey.from( "myapp" ) );

        final LogAuditLogParams value = captureLog();
        assertEquals( "system.schema.namespace.delete", value.getType() );
        assertEquals( "myapp", firstUri( value ) );
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
