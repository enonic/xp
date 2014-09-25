package com.enonic.wem.core.content.site

import com.enonic.wem.api.content.site.Site
import com.enonic.wem.api.content.site.SiteTemplateKey
import com.enonic.wem.api.data.DataSet
import com.enonic.wem.api.data.RootDataSet
import com.enonic.wem.api.module.ModuleKey
import spock.lang.Specification

import static com.enonic.wem.api.content.site.ModuleConfig.newModuleConfig

class SiteDataSerializerTest
        extends Specification
{
    def "Site -> toData -> toSite -> Site2 should be equal"()
    {
        given:
        SiteDataSerializer siteSerializer = new SiteDataSerializer( "mySiteDataSet" );

        Site site =
                Site.newSite().template( SiteTemplateKey.from( "unchanged" ) ).
                        addModuleConfig(
                                newModuleConfig().module( ModuleKey.from( "unchanged-1.1.1" ) ).config( new RootDataSet() ).build() ).
                        addModuleConfig(
                                newModuleConfig().module( ModuleKey.from( "unchanged-1.1.2" ) ).config( new RootDataSet() ).build() ).
                        build();

        when:
        DataSet siteAsData = siteSerializer.toData( site );
        Site newSite = siteSerializer.fromData( siteAsData );

        then:
        newSite.equals( site );


    }


}
