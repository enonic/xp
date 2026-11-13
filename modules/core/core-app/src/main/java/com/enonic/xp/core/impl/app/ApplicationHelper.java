package com.enonic.xp.core.impl.app;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.jar.Manifest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.osgi.framework.Bundle;
import org.osgi.framework.Version;
import org.osgi.framework.VersionRange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableSet;

import com.enonic.xp.app.ApplicationDescriptor;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.ApplicationRepoConstants;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.core.impl.app.resolver.ApplicationUrlResolver;
import com.enonic.xp.core.internal.ApplicationBundleUtils;
import com.enonic.xp.exception.ForbiddenAccessException;
import com.enonic.xp.node.Node;
import com.enonic.xp.resource.Resource;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.auth.AuthenticationInfo;

import static com.enonic.xp.core.impl.app.ApplicationManifestConstants.X_CAPABILITY;
import static com.enonic.xp.core.impl.app.ApplicationManifestConstants.X_SOURCE_PATHS;
import static com.google.common.base.Strings.isNullOrEmpty;

public final class ApplicationHelper
{
    private static final Logger LOG = LoggerFactory.getLogger( ApplicationHelper.class );

    private static final String LOCAL_BUNDLE_LOCATION_PREFIX = "local:";

    // https://semver.org: MAJOR.MINOR.PATCH, an optional pre-release after a hyphen, an optional build metadata after a plus
    private static final Pattern SEMVER = Pattern.compile( "(0|[1-9]\\d*)\\.(0|[1-9]\\d*)\\.(0|[1-9]\\d*)" +
                                                               "(?:-((?:0|[1-9]\\d*|\\d*[a-zA-Z-][0-9a-zA-Z-]*)" +
                                                               "(?:\\.(?:0|[1-9]\\d*|\\d*[a-zA-Z-][0-9a-zA-Z-]*))*))?" +
                                                               "(?:\\+([0-9a-zA-Z-]+(?:\\.[0-9a-zA-Z-]+)*))?" );

    /**
     * The application shipping the built-in schema: the resources of the reserved keys ({@code base}, {@code portal}, {@code media},
     * {@code system}) are served through it.
     */
    public static final ApplicationKey SYSTEM_APPLICATION_KEY = ApplicationKey.from( "com.enonic.xp.app.system" );

    public static ApplicationKey getApplicationKey( final Bundle bundle )
    {
        return ApplicationKey.from( ApplicationBundleUtils.getApplicationName( bundle ) );
    }

    /**
     * A codeless application owns its schema ({@code cms/cms.yaml}), is installed globally (its schema is persisted in nodes)
     * and is not built with gradle: the XP gradle plugin builds the manifest with Bnd, which always stamps it with
     * {@code Bnd-LastModified} and {@code Tool}, while the manifest of a codeless application (built by Enonic CLI) has neither.
     */
    static boolean isCodelessApplication( final Bundle bundle )
    {
        return hasCmsDescriptor( bundle ) && !isLocalApplication( bundle ) && !isBuiltWithBnd( bundle );
    }

    /**
     * {@code true} when the bundle was built with gradle: the XP gradle plugin builds the manifest with Bnd, which always stamps
     * it with {@code Bnd-LastModified} and {@code Tool}.
     */
    static boolean isBuiltWithBnd( final Bundle bundle )
    {
        final String tool = bundle.getHeaders().get( ApplicationManifestConstants.TOOL );
        return !isNullOrEmpty( bundle.getHeaders().get( ApplicationManifestConstants.BND_LAST_MODIFIED ) ) ||
            ( tool != null && tool.startsWith( ApplicationManifestConstants.BND_TOOL_PREFIX ) );
    }

    /**
     * {@code true} when the bundle ships a cms descriptor ({@code cms/cms.yaml}), i.e. the application owns its schema.
     */
    static boolean hasCmsDescriptor( final Bundle bundle )
    {
        return SchemaResourcePaths.CMS_DESCRIPTOR_PATHS.stream().anyMatch( path -> bundle.getEntry( path ) != null );
    }

    /**
     * Location of an application bundle. Local applications are marked by a location prefix,
     * the only channel that reaches the bundle tracker creating the application.
     */
    static String toBundleLocation( final ApplicationKey applicationKey, final boolean local )
    {
        return ( local ? LOCAL_BUNDLE_LOCATION_PREFIX : "" ) + applicationKey.getName();
    }

    /**
     * {@code true} when the bundle was installed as a local application, see {@link #toBundleLocation(ApplicationKey, boolean)}.
     */
    static boolean isLocalApplication( final Bundle bundle )
    {
        final String location = bundle.getLocation();
        return location != null && location.startsWith( LOCAL_BUNDLE_LOCATION_PREFIX );
    }

    static String getAttribute( final Manifest manifest, final String name, final String defValue )
    {
        if ( manifest == null )
        {
            return defValue;
        }

        return manifest.getMainAttributes().getValue( name ) != null ? manifest.getMainAttributes().getValue( name ) : defValue;
    }

    static String getHeader( final Bundle bundle, final String name, final String defValue )
    {
        final String value = bundle.getHeaders().get( name );
        return isNullOrEmpty( value ) ? defValue : value;
    }

    static Set<String> getCapabilities( final Bundle bundle )
    {
        final String value = getHeader( bundle, X_CAPABILITY, "" );
        return ImmutableSet.copyOf( Splitter.on( ',' ).omitEmptyStrings().trimResults().split( value ) );
    }

    static List<String> getSourcePaths( final Bundle bundle )
    {
        final String value = getHeader( bundle, X_SOURCE_PATHS, "" );
        if ( isNullOrEmpty( value ) )
        {
            return new ArrayList<>();
        }
        return Splitter.on( ',' ).trimResults().splitToList( value );
    }

    static VersionRange parseVersionRange( final String value )
    {
        if ( value == null )
        {
            return null;
        }

        try
        {
            return VersionRange.valueOf( value );
        }
        catch ( final Exception e )
        {
            return null;
        }
    }

    public static void checkSystemVersion( final Bundle bundle, final Version systemVersion )
    {
        final String systemVersionRange = getHeader( bundle, ApplicationManifestConstants.X_SYSTEM_VERSION, "" );

        if ( !systemVersionRange.isEmpty() )
        {
            final VersionRange appVersionRange = parseVersionRange( systemVersionRange );
            if ( appVersionRange == null || !appVersionRange.includes( systemVersion ) )
            {
                throw new ApplicationInvalidVersionException( systemVersionRange, systemVersion );
            }
        }
    }

    /**
     * Checks the system version against the range declared by the descriptor of the application, as the resolver of the
     * application serves it: the descriptor persisted on the application node for a globally installed application, the one the
     * bundle ships for a local or a system application.
     *
     * @throws ApplicationInvalidVersionException when the system version is out of the range
     */
    static void checkDescriptorSystemVersion( final ApplicationKey key, final ApplicationUrlResolver resolver, final Version systemVersion )
    {
        final ApplicationDescriptor descriptor = readDescriptor( key, resolver );
        if ( descriptor != null )
        {
            checkSystemVersion( descriptor.getMinSystemVersion(), descriptor.getMaxSystemVersion(), systemVersion );
        }
    }

    /**
     * Checks the system version against the range the application descriptor declares with {@code minSystemVersion} (inclusive)
     * and {@code maxSystemVersion} (exclusive), either of them optional.
     *
     * @throws IllegalArgumentException           when either of the versions is not a valid version
     * @throws ApplicationInvalidVersionException when the system version is out of the range
     */
    static void checkSystemVersion( final String minSystemVersion, final String maxSystemVersion, final Version systemVersion )
    {
        final VersionRange range = toSystemVersionRange( minSystemVersion, maxSystemVersion );
        if ( range != null && !range.includes( systemVersion ) )
        {
            throw new ApplicationInvalidVersionException( range.toString(), systemVersion );
        }
    }

    /**
     * The system version range an application descriptor declares with {@code minSystemVersion} (inclusive) and
     * {@code maxSystemVersion} (exclusive), either of them optional.
     *
     * @return the range, or {@code null} when the descriptor declares neither
     * @throws IllegalArgumentException when either of the versions is not a valid version
     */
    static VersionRange toSystemVersionRange( final String minSystemVersion, final String maxSystemVersion )
    {
        if ( isNullOrEmpty( minSystemVersion ) && isNullOrEmpty( maxSystemVersion ) )
        {
            return null;
        }
        final Version min =
            isNullOrEmpty( minSystemVersion ) ? Version.emptyVersion : toBundleVersion( "minSystemVersion", minSystemVersion );
        final Version max = isNullOrEmpty( maxSystemVersion ) ? null : toBundleVersion( "maxSystemVersion", maxSystemVersion );
        return new VersionRange( VersionRange.LEFT_CLOSED, min, max, VersionRange.RIGHT_OPEN );
    }

    /**
     * A version declared by both the descriptor and the manifest of an application is the same version in both ({@code 1.2} and
     * {@code 1.2.0} are). One declared by one of them alone has nothing to be compared with: a manifest without
     * {@code Bundle-Version}, read as {@code 0.0.0}, declares none.
     *
     * @return what differs, {@code null} when nothing does
     */
    static String versionMismatch( final ApplicationKey key, final String field, final String declared, final String header,
                                   final String manifestValue )
    {
        if ( isNullOrEmpty( declared ) || isNullOrEmpty( manifestValue ) )
        {
            return null;
        }
        final Version manifestVersion = toVersion( manifestValue );
        if ( manifestVersion == null || Version.emptyVersion.equals( manifestVersion ) )
        {
            return null;
        }
        if ( sameVersion( manifestVersion, declaredVersion( declared ) ) )
        {
            return null;
        }
        return String.format( "Application [%s] declares %s [%s] in its descriptor, but [%s] in its manifest (%s)", key, field, declared,
                              manifestValue, header );
    }

    static Version toVersion( final String value )
    {
        try
        {
            return Version.parseVersion( value );
        }
        catch ( final IllegalArgumentException e )
        {
            return null;
        }
    }

    private static Version declaredVersion( final String declared )
    {
        try
        {
            return toBundleVersion( "version", declared );
        }
        catch ( final IllegalArgumentException e )
        {
            return null;
        }
    }

    // the build tools do not agree on how the separators of a pre-release end up in the qualifier of a bundle version
    // (rc.1 as rc_1, rc-1 or rc1): the letters and digits tell whether two qualifiers are the same
    private static boolean sameVersion( final Version manifestVersion, final Version declaredVersion )
    {
        return declaredVersion != null && manifestVersion.getMajor() == declaredVersion.getMajor() &&
            manifestVersion.getMinor() == declaredVersion.getMinor() && manifestVersion.getMicro() == declaredVersion.getMicro() &&
            manifestVersion.getQualifier().replaceAll( "[^0-9A-Za-z]", "" )
                .equals( declaredVersion.getQualifier().replaceAll( "[^0-9A-Za-z]", "" ) );
    }

    /**
     * Converts a version written as a semantic version (<a href="https://semver.org">semver.org</a>), the way the application
     * descriptor declares versions, to the version the platform works with, the one of a bundle: {@code 1.2.3-rc.1} is
     * {@code 1.2.3.rc_1}. The pre-release, and the build metadata after it, make the qualifier, every character a qualifier
     * does not take replaced by an underscore. The versions are compared as bundle versions from there on.
     *
     * @return the version, the empty version ({@code 0.0.0}) for no value
     * @throws IllegalArgumentException when the value is not a semantic version
     */
    static Version semVerToOsgi( final String value )
    {
        if ( isNullOrEmpty( value ) )
        {
            return Version.emptyVersion;
        }
        final Matcher matcher = SEMVER.matcher( value );
        if ( !matcher.matches() )
        {
            throw new IllegalArgumentException( "invalid semantic version \"" + value + "\"" );
        }
        final String preRelease = matcher.group( 4 );
        final String build = matcher.group( 5 );
        final String qualifier;
        if ( preRelease == null )
        {
            qualifier = build == null ? "" : build;
        }
        else
        {
            qualifier = build == null ? preRelease : preRelease + "_" + build;
        }
        try
        {
            return new Version( Integer.parseInt( matcher.group( 1 ) ), Integer.parseInt( matcher.group( 2 ) ),
                                Integer.parseInt( matcher.group( 3 ) ), qualifier.replaceAll( "[^0-9A-Za-z_-]", "_" ) );
        }
        catch ( final NumberFormatException e )
        {
            throw new IllegalArgumentException( "invalid semantic version \"" + value + "\"", e );
        }
    }

    /**
     * Writes the version of a bundle as a semantic version, the way the application descriptor file declares versions:
     * {@code 1.2.3.SNAPSHOT} is {@code 1.2.3-SNAPSHOT}, {@code 1.2.3.rc_1} is {@code 1.2.3-rc.1}.
     *
     * @return the semantic version
     */
    static String osgiToSemVer( final Version version )
    {
        final String core = version.getMajor() + "." + version.getMinor() + "." + version.getMicro();
        final String qualifier = version.getQualifier();
        if ( qualifier.isEmpty() )
        {
            return core;
        }
        // the inverse of semVerToOsgi: an underscore of the qualifier is the dot separating the identifiers of the pre-release
        final String dotted = core + "-" + qualifier.replace( '_', '.' );
        if ( SEMVER.matcher( dotted ).matches() )
        {
            return dotted;
        }
        // an empty identifier (a__b): the underscores are written as hyphens
        final String hyphenated = core + "-" + qualifier.replace( '_', '-' );
        if ( SEMVER.matcher( hyphenated ).matches() )
        {
            return hyphenated;
        }
        // a number with a leading zero (007) is no pre-release, it is a build metadata
        return core + "+" + qualifier.replace( '_', '-' );
    }

    /**
     * The version held by an application descriptor, which is written the way the version of a bundle is ({@code 1.2.3.SNAPSHOT}),
     * as the platform reports versions. A semantic version ({@code 1.2.3-SNAPSHOT}), the way the descriptor file declares it, is
     * taken as well: an editor of the descriptor may write either.
     *
     * @return the version, the empty version ({@code 0.0.0}) for no value
     * @throws IllegalArgumentException when the value is a version in neither form
     */
    static Version toBundleVersion( final String field, final String value )
    {
        if ( isNullOrEmpty( value ) )
        {
            return Version.emptyVersion;
        }
        if ( SEMVER.matcher( value ).matches() )
        {
            return semVerToOsgi( value );
        }
        final Version version = toVersion( value );
        if ( version == null )
        {
            throw new IllegalArgumentException(
                String.format( "Invalid %s in application descriptor: invalid version \"%s\"", field, value ) );
        }
        return version;
    }

    /**
     * A version of an application descriptor as the descriptor holds it: written the way the version of a bundle is, whichever
     * way it was given, see {@link #toBundleVersion(String, String)}.
     *
     * @return the version, {@code null} for no value
     */
    static String normalizeVersion( final String field, final String value )
    {
        return isNullOrEmpty( value ) ? null : toBundleVersion( field, value ).toString();
    }

    /**
     * A version of an application descriptor as the descriptor file ({@code enonic.yaml}) declares it: a semantic version.
     *
     * @return the semantic version, {@code null} for no value
     */
    static String toSemVer( final String field, final String value )
    {
        return isNullOrEmpty( value ) ? null : osgiToSemVer( toBundleVersion( field, value ) );
    }

    /**
     * Parses a version declared by the application descriptor: a semantic version, converted to the version of a bundle, see
     * {@link #semVerToOsgi(String)}.
     *
     * @throws IllegalArgumentException when the value is not a semantic version
     */
    static Version parseVersion( final String field, final String value )
    {
        try
        {
            return semVerToOsgi( value );
        }
        catch ( final IllegalArgumentException e )
        {
            throw new IllegalArgumentException( String.format( "Invalid %s in application descriptor: %s", field, e.getMessage() ), e );
        }
    }

    // a descriptor that cannot be read or parsed declares nothing to check: the application has no descriptor then
    private static ApplicationDescriptor readDescriptor( final ApplicationKey key, final ApplicationUrlResolver resolver )
    {
        try
        {
            return loadDescriptor( key, resolver );
        }
        catch ( final Exception e )
        {
            LOG.debug( "Unable to read application descriptor of {} to check the system version", key, e );
            return null;
        }
    }

    /**
     * The descriptor of the application as the resolver serves it, {@code null} when it serves none.
     */
    static ApplicationDescriptor loadDescriptor( final ApplicationKey key, final ApplicationUrlResolver resolver )
    {
        if ( resolver == null )
        {
            return null;
        }
        final Resource resource = ApplicationBundleUtils.DESCRIPTOR_PATHS.stream()
            .map( path -> resolver.findResource( "/" + path ) )
            .filter( candidate -> candidate != null && candidate.exists() )
            .findFirst()
            .orElse( null );
        return resource != null ? YmlApplicationDescriptorParser.parse( resource.readString(), key ).build() : null;
    }

    /**
     * Writing the persisted schema or descriptor of an application takes the {@code system.admin} or {@code system.schema.admin} role.
     */
    static void requireSchemaAdminRole()
    {
        final AuthenticationInfo authInfo = ContextAccessor.current().getAuthInfo();
        final boolean hasAdminRole = authInfo.hasRole( RoleKeys.ADMIN ) || authInfo.hasRole( RoleKeys.SCHEMA_ADMIN );
        if ( !hasAdminRole )
        {
            throw new ForbiddenAccessException( authInfo.getUser() );
        }
    }

    /**
     * {@code true} for a key the platform owns: a reserved key or the key of the application shipping the built-in schema.
     */
    static boolean isSystemApplicationKey( final ApplicationKey key )
    {
        return ApplicationKey.SYSTEM_RESERVED_APPLICATION_KEYS.contains( key ) || SYSTEM_APPLICATION_KEY.equals( key );
    }

    /**
     * The persisted schema and descriptor of a system application are never written: neither for a key the platform owns, see
     * {@link #isSystemApplicationKey(ApplicationKey)}, nor for an installed bundle of the system type ({@code X-Bundle-Type: system}).
     *
     * @param key          the application written
     * @param systemBundle {@code true} when the application is installed from a bundle of the system type
     */
    static void requireNonSystemApplication( final ApplicationKey key, final boolean systemBundle )
    {
        if ( systemBundle || isSystemApplicationKey( key ) )
        {
            throw new IllegalArgumentException( "Cannot modify system application: " + key );
        }
    }

    static void requireNonLocalApplication( final ApplicationKey key, final boolean local )
    {
        if ( local )
        {
            throw new IllegalArgumentException( "Cannot modify local application: " + key );
        }
    }

    /**
     * {@code true} when the application node holds the flag preventing the application from being installed over and
     * uninstalled.
     */
    static boolean isLocked( final Node applicationNode )
    {
        return applicationNode != null && applicationNode.data() != null &&
            Boolean.TRUE.equals( applicationNode.data().getBoolean( ApplicationPropertyNames.LOCKED ) );
    }

    /**
     * {@code true} when the definition of the application was changed since its install or creation: its node counts the changes.
     */
    static boolean isSchemaModified( final Node applicationNode )
    {
        if ( applicationNode == null || applicationNode.data() == null )
        {
            return false;
        }
        final Long revision = applicationNode.data().getLong( ApplicationPropertyNames.SCHEMA_REVISION );
        return revision != null && revision > 0;
    }

    /**
     * A locked application is neither installed over nor uninstalled until the flag on its node is reset through the
     * application descriptor service.
     *
     * @param key             the application
     * @param applicationNode the node of the application, {@code null} when it has none
     * @param action          what is refused, as a verb: {@code install} or {@code uninstall}
     */
    static void requireNotLocked( final ApplicationKey key, final Node applicationNode, final String action )
    {
        if ( isLocked( applicationNode ) )
        {
            throw new ApplicationBundleException(
                String.format( "Cannot %s application %s: it is locked", action, key ) );
        }
    }

    /**
     * The descriptor of an application built with gradle is never written: it is owned by the build, see
     * {@link #isBuiltWithBnd(Bundle)}. The flag kept on its node alone is set and reset.
     *
     * @param builtWithGradle {@code true} when the descriptor of an application built with gradle is about to be edited
     */
    static void requireNonGradleApplication( final ApplicationKey key, final boolean builtWithGradle )
    {
        if ( builtWithGradle )
        {
            throw new IllegalArgumentException( "Cannot modify application built with Gradle: " + key );
        }
    }

    public static <T> T runAsAdmin( final Callable<T> callable )
    {
        return createAdminContext().callWith( callable );
    }

    public static void runAsAdmin( final Runnable runnable )
    {
        createAdminContext().runWith( runnable );
    }

    static Context createAdminContext()
    {
        return ContextBuilder.create()
            .branch( ApplicationRepoConstants.APP_REPO_BRANCH )
            .repositoryId( ApplicationRepoConstants.APP_REPO_ID )
            .authInfo( ApplicationConstants.APPLICATION_SU_AUTH_INFO )
            .build();
    }

    static <T> T callWithContext( Callable<T> runnable )
    {
        return createContext().callWith( runnable );
    }

    static void runWithContext( Runnable runnable )
    {
        createContext().runWith( runnable );
    }

    private static Context createContext()
    {
        return ContextBuilder.create()
            .branch( ApplicationRepoConstants.APP_REPO_BRANCH )
            .repositoryId( ApplicationRepoConstants.APP_REPO_ID )
            .authInfo( ContextAccessor.current().getAuthInfo() )
            .build();
    }
}
