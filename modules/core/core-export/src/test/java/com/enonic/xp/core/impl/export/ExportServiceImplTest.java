package com.enonic.xp.core.impl.export;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.ArgumentCaptor;

import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.exception.ForbiddenAccessException;
import com.enonic.xp.export.ExportInfo;
import com.enonic.xp.export.ImportNodesParams;
import com.enonic.xp.export.ListExportsResult;
import com.enonic.xp.export.NodeImportResult;
import com.enonic.xp.node.ImportNodeParams;
import com.enonic.xp.node.ImportNodeResult;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeService;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.User;
import com.enonic.xp.security.auth.AuthenticationInfo;
import com.enonic.xp.vfs.VirtualFiles;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ExportServiceImplTest
{
    private static final Context ADMIN_CONTEXT = ContextBuilder.create()
        .authInfo( AuthenticationInfo.create().principals( RoleKeys.ADMIN ).user( User.anonymous() ).build() )
        .build();

    private static final String NODE_XML = """
        <node>
          <childOrder>_name DESC</childOrder>
          <nodeType>content</nodeType>
          <permissions/>
          <data>
            <string name="controller">com.enonic.apps.genericapp:default</string>
          </data>
          <indexConfigs>
            <defaultConfig>
              <decideByType>false</decideByType>
              <enabled>true</enabled>
              <nGram>true</nGram>
              <fulltext>true</fulltext>
              <includeInAllText>true</includeInAllText>
            </defaultConfig>
          </indexConfigs>
        </node>
        """;

    private static final String TRANSFORM_XSL = """
        <?xml version="1.0"?>
        <xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
          <xsl:output method="xml" indent="yes"/>
          <xsl:param name="applicationId"/>
          <xsl:variable name="placeholderApp" select="'com.enonic.apps.genericapp'"/>

          <xsl:template match="string[starts-with(text(),concat($placeholderApp,':'))]">
            <string>
              <xsl:attribute name="name">
                <xsl:value-of select="@name"/>
              </xsl:attribute>
              <xsl:value-of select="concat($applicationId, substring-after(.,$placeholderApp))"/>
            </string>
          </xsl:template>

          <xsl:template match="@*|node()">
            <xsl:copy>
              <xsl:apply-templates select="@*|node()"/>
            </xsl:copy>
          </xsl:template>
        </xsl:stylesheet>
        """.stripLeading();

    @TempDir
    Path tempDir;

    private Path exportsDir;

    private NodeService nodeService;

    private ExportServiceImpl exportService;

    @BeforeEach
    void setUp()
    {
        exportsDir = tempDir.resolve( "exports" );

        final ExportConfigurationDynamic config = mock( ExportConfigurationDynamic.class );
        when( config.getExportsDir() ).thenReturn( exportsDir );

        nodeService = mock( NodeService.class );
        exportService = new ExportServiceImpl( config, nodeService );
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

    @Test
    void importNodes_appliesXsltVirtualFile_whenGivenDirectly()
        throws IOException
    {
        createExportZip( "my-export" );
        final Path xsltPath = tempDir.resolve( "app-transform.xsl" );
        Files.writeString( xsltPath, TRANSFORM_XSL );

        final ArgumentCaptor<ImportNodeParams> captor = captureImportedNode();

        final NodeImportResult result = exportService.importNodes( ImportNodesParams.create()
                                                                       .exportName( "my-export" )
                                                                       .targetNodePath( NodePath.ROOT )
                                                                       .xslt( VirtualFiles.from( xsltPath ) )
                                                                       .xsltParam( "applicationId", "com.acme.myapp" )
                                                                       .build() );

        assertThat( result.getImportErrors() ).isEmpty();
        assertThat( captor.getValue().getNode().data().getString( "controller" ) ).isEqualTo( "com.acme.myapp:default" );
    }

    @Test
    void importNodes_withoutXslt_importsUntransformed()
        throws IOException
    {
        createExportZip( "my-export" );

        final ArgumentCaptor<ImportNodeParams> captor = captureImportedNode();

        final NodeImportResult result =
            exportService.importNodes( ImportNodesParams.create().exportName( "my-export" ).targetNodePath( NodePath.ROOT ).build() );

        assertThat( result.getImportErrors() ).isEmpty();
        assertThat( result.getAddedNodes().getSize() ).isEqualTo( 1 );
        assertThat( captor.getValue().getNode().data().getString( "controller" ) ).isEqualTo( "com.enonic.apps.genericapp:default" );
    }

    private ArgumentCaptor<ImportNodeParams> captureImportedNode()
    {
        final ArgumentCaptor<ImportNodeParams> captor = ArgumentCaptor.forClass( ImportNodeParams.class );
        when( nodeService.importNode( captor.capture() ) ).thenAnswer( invocation -> {
            final Node node = invocation.<ImportNodeParams>getArgument( 0 ).getNode();
            return ImportNodeResult.create().node( node ).preExisting( false ).build();
        } );
        return captor;
    }

    private void createExportZip( final String exportName )
        throws IOException
    {
        Files.createDirectories( exportsDir );
        try (OutputStream out = Files.newOutputStream( exportsDir.resolve( exportName + ".zip" ) );
             ZipOutputStream zip = new ZipOutputStream( out ))
        {
            addZipEntry( zip, exportName + "/export.properties", "xp.version=1.0" );
            addZipEntry( zip, exportName + "/mynode/_/node.xml", NODE_XML );
        }
    }

    private static void addZipEntry( final ZipOutputStream zip, final String name, final String content )
        throws IOException
    {
        zip.putNextEntry( new ZipEntry( name ) );
        zip.write( content.getBytes( StandardCharsets.UTF_8 ) );
        zip.closeEntry();
    }

}
