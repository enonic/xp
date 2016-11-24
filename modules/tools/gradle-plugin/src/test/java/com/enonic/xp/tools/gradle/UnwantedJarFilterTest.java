package com.enonic.xp.tools.gradle;

import java.util.Set;

import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.ExcludeRule;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.Test;

import static org.junit.Assert.*;

public class UnwantedJarFilterTest
{
    @Test
    public void testFilter()
    {
        final Project project = ProjectBuilder.builder().build();
        final Configuration config = project.getConfigurations().create( "other" );

        final UnwantedJarFilter filter = new UnwantedJarFilter( config );
        final Configuration filteredConfig = filter.filter();

        assertNotSame( config, filteredConfig );

        final Set<ExcludeRule> rules = filteredConfig.getExcludeRules();
        assertEquals( 7, rules.size() );
    }
}
