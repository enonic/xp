package com.enonic.wem.core.country

import com.enonic.wem.core.config.SystemConfig
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification
import spock.lang.Unroll

@Unroll
class CountryServiceImplTest extends Specification
{
    @Rule
    TemporaryFolder tempFolder = new TemporaryFolder()

    def CountryServiceImpl service

    def setup( )
    {
        def config = Mock( SystemConfig )
        config.getConfigDir() >> this.tempFolder.newFolder()
        this.service = new CountryServiceImpl( config )
    }

    def "unknown country should return null"( )
    {
        when:
        def country = this.service.getCountry( new CountryCode( "XX" ) )

        then:
        country == null
    }

    def "country code #code should return country #name"( )
    {
        when:
        def country = this.service.getCountry( new CountryCode( code ) )

        then:
        country != null
        country.getCode().toString() == code
        country.getEnglishName() == name

        where:
        code | name
        "BB" | "BARBADOS"
        "NO" | "NORWAY"
    }

    def "test getting all countries"( )
    {
        when:
        def countries = this.service.getCountries()

        then:
        countries != null
        countries.size() == 246
        countries.iterator().next().code.toString() == "AF"
    }
}
