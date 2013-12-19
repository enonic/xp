package com.enonic.wem.core.content.site

import com.enonic.wem.api.content.site.Site
import com.enonic.wem.api.content.site.SiteTemplateKey
import com.enonic.wem.api.content.site.SiteTemplateName
import com.enonic.wem.api.content.site.SiteTemplateVersion
import com.enonic.wem.api.data.DataSet
import com.enonic.wem.api.data.RootDataSet
import com.enonic.wem.api.module.ModuleKey
import spock.lang.Specification

import static com.enonic.wem.api.content.site.ModuleConfig.newModuleConfig

class SiteSerializerTest
        extends Specification
{


    def "Site -> toData -> toSite -> Site2 should be equal"()
    {
        given:
        SiteSerializer siteSerializer = new SiteSerializer();

        Site site =
                Site.newSite().template( SiteTemplateKey.from( new SiteTemplateName( "unchanged" ), new SiteTemplateVersion( "1.0.0" ) ) ).
                        addModuleConfig(
                                newModuleConfig().module( ModuleKey.from( "unchanged-1.1.1" ) ).config( new RootDataSet() ).build() ).
                        addModuleConfig(
                                newModuleConfig().module( ModuleKey.from( "unchanged-1.1.2" ) ).config( new RootDataSet() ).build() ).
                        build();

        when:
        DataSet siteAsData = siteSerializer.toData( site, "mySiteDataSet" );
        Site newSite = siteSerializer.toSite( siteAsData );

        then:
        newSite.equals( site );


    }


}
