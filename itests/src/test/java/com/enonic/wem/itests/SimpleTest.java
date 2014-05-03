package com.enonic.wem.itests;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.osgi.metadata.OSGiManifestBuilder;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

@RunWith(Arquillian.class)
public class SimpleTest
{
    @ArquillianResource
    protected BundleContext context;

    @Deployment
    public static JavaArchive createdeployment()
    {
        final JavaArchive archive = ShrinkWrap.create( JavaArchive.class, "test.jar" );
        archive.setManifest( () -> {
            OSGiManifestBuilder builder = OSGiManifestBuilder.newInstance();
            builder.addBundleSymbolicName( archive.getName() );
            builder.addBundleManifestVersion( 2 );
            builder.addImportPackages( Bundle.class );
            return builder.openStream();
        } );

        return archive;
    }

    @Test
    public void testFramework()
    {
        Assert.assertNotNull( this.context );
    }
}
