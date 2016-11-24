package com.enonic.xp.tools.gradle;

import org.dm.gradle.plugins.bundle.BundlePlugin;
import org.gradle.api.Project;
import org.gradle.api.internal.project.ProjectInternal;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.Test;

import static org.junit.Assert.*;

public class AppPluginTest
{
    @Test
    public void testPlugin()
    {
        final Project project = ProjectBuilder.builder().build();
        new AppPlugin().apply( project );

        assertTrue( project.getPlugins().hasPlugin( BundlePlugin.class ) );
        assertNotNull( project.getConfigurations().getByName( "include" ) );
        assertNotNull( project.getConfigurations().getByName( "webjar" ) );

        ( (ProjectInternal) project ).evaluate();

        assertNotNull( project.getTasks().getByName( "deploy" ) );
        assertNotNull( project.getTasks().getByName( "unpackWebJars" ) );
    }
}
