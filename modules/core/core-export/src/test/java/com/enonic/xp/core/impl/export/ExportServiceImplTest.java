package com.enonic.xp.core.impl.export;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.exception.ForbiddenAccessException;
import com.enonic.xp.export.ExportInfo;
import com.enonic.xp.export.ListExportsResult;
import com.enonic.xp.node.NodeService;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.User;
import com.enonic.xp.security.auth.AuthenticationInfo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ExportServiceImplTest
{
    private static final Context ADMIN_CONTEXT = ContextBuilder.create()
        .authInfo( AuthenticationInfo.create().principals( RoleKeys.ADMIN ).user( User.anonymous() ).build() )
        .build();

    @TempDir
    Path tempDir;

    private Path exportsDir;

    private ExportServiceImpl exportService;

    @BeforeEach
    void setUp()
    {
        exportsDir = tempDir.resolve( "exports" );

        final ExportConfigurationDynamic config = mock( ExportConfigurationDynamic.class );
        when( config.getExportsDir() ).thenReturn( exportsDir );

        exportService = new ExportServiceImpl( config, mock( NodeService.class ) );
    }

    @Test
    void list_returnsEmpty_whenExportsDirDoesNotExist()
    {
        final ListExportsResult result = listAsAdmin();

        assertThat( result.isEmpty() ).isTrue();
        assertThat( result.getSize() ).isZero();
    }

    @Test
    void list_returnsEmpty_whenExportsDirIsEmpty()
        throws IOException
    {
        Files.createDirectories( exportsDir );

        assertThat( listAsAdmin().isEmpty() ).isTrue();
    }

    @Test
    void list_stripsZipExtensionFromNames()
        throws IOException
    {
        Files.createDirectories( exportsDir );
        Files.createFile( exportsDir.resolve( "site-backup.zip" ) );

        assertThat( listAsAdmin().getList() ).extracting( ExportInfo::name ).containsExactly( "site-backup" );
    }

    @Test
    void list_ignoresNonZipFiles()
        throws IOException
    {
        Files.createDirectories( exportsDir );
        Files.createFile( exportsDir.resolve( "real.zip" ) );
        Files.createFile( exportsDir.resolve( "notes.txt" ) );
        Files.createFile( exportsDir.resolve( "archive.tar" ) );

        assertThat( listAsAdmin().getList() ).extracting( ExportInfo::name ).containsExactly( "real" );
    }

    @Test
    void list_ignoresSubdirectories()
        throws IOException
    {
        Files.createDirectories( exportsDir );
        Files.createDirectories( exportsDir.resolve( "nested.zip" ) );
        Files.createFile( exportsDir.resolve( "actual.zip" ) );

        assertThat( listAsAdmin().getList() ).extracting( ExportInfo::name ).containsExactly( "actual" );
    }

    @Test
    void list_ignoresHiddenFiles()
        throws IOException
    {
        Files.createDirectories( exportsDir );
        Files.createFile( exportsDir.resolve( ".hidden.zip" ) );
        Files.createFile( exportsDir.resolve( "visible.zip" ) );

        assertThat( listAsAdmin().getList() ).extracting( ExportInfo::name ).containsExactly( "visible" );
    }

    @Test
    void list_resultIsIterable()
        throws IOException
    {
        Files.createDirectories( exportsDir );
        Files.createFile( exportsDir.resolve( "one.zip" ) );
        Files.createFile( exportsDir.resolve( "two.zip" ) );

        final long count = listAsAdmin().stream().count();
        assertThat( count ).isEqualTo( 2L );
    }

    @Test
    void list_requires_the_administrator_role()
    {
        assertThrows( ForbiddenAccessException.class, () -> exportService.list() );
    }

    @Test
    void list_requires_the_administrator_role_for_authenticated_user()
    {
        final Context context = ContextBuilder.create()
            .authInfo( AuthenticationInfo.create().principals( RoleKeys.AUTHENTICATED ).user( User.anonymous() ).build() )
            .build();

        assertThrows( ForbiddenAccessException.class, () -> context.callWith( () -> exportService.list() ) );
    }

    private ListExportsResult listAsAdmin()
    {
        return ADMIN_CONTEXT.callWith( () -> exportService.list() );
    }
}
