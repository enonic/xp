package com.enonic.wem.tools.maven;

import java.io.File;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

/**
 * Create a WEM plugin from Maven project.
 */
@Mojo(name = "package", defaultPhase = LifecyclePhase.PACKAGE)
public final class PackagePlugin
    extends AbstractMojo
{
    @Parameter(alias = "manifestLocation", defaultValue = "${project.build.outputDirectory}/META-INF")
    private File manifestLocation = null;

    @Component
    private MavenProject project = null;

    @Override
    public void execute()
        throws MojoExecutionException, MojoFailureException
    {
        System.out.println( "==> " + this.project );
        System.out.println( "==> " + this.manifestLocation );
    }
}
