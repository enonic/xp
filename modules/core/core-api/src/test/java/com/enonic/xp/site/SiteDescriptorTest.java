package com.enonic.xp.site;

import org.junit.jupiter.api.Test;

import com.enonic.xp.app.ApplicationKey;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class SiteDescriptorTest
{
    @Test
    void create_empty_site_descriptor()
    {
        //Builds an empty SiteDescriptor
        SiteDescriptor siteDescriptor = SiteDescriptor.create().applicationKey( ApplicationKey.from( "myapplication" ) ).build();
        assertNotNull( siteDescriptor.getResponseProcessors() );
    }

}
