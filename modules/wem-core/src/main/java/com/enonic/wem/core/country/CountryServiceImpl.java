package com.enonic.wem.core.country;

import java.io.File;
import java.net.URL;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jdom.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.wem.core.config.SystemConfig;
import com.enonic.wem.core.support.util.JdomHelper;

/**
 * This implements the country service. It load country codes from an xml file. It tries to find the first resource that exists and load
 * it.
 */
@Singleton
public final class CountryServiceImpl
    implements CountryService
{
    private final static Logger LOG = LoggerFactory.getLogger( CountryServiceImpl.class );

    private final LinkedHashMap<CountryCode, Country> countriesMapByCode;

    private final File countryFile;

    @Inject
    public CountryServiceImpl( final SystemConfig systemConfig )
        throws Exception
    {
        this.countryFile = new File( systemConfig.getConfigDir(), "countries.xml" );
        this.countriesMapByCode = new LinkedHashMap<>();
        final URL url = findCountryResource();
        for ( final Country country : readCountries( url ) )
        {
            this.countriesMapByCode.put( country.getCode(), country );
        }
    }

    public Collection<Country> getCountries()
    {
        return this.countriesMapByCode.values();
    }

    public Country getCountry( CountryCode countryCode )
    {
        return this.countriesMapByCode.get( countryCode );
    }

    private URL findCountryResource()
        throws Exception
    {
        if ( this.countryFile.exists() && this.countryFile.isFile() )
        {
            return this.countryFile.toURI().toURL();
        }
        else
        {
            return getClass().getResource( "countries.xml" );
        }
    }

    private List<Country> readCountries( final URL url )
        throws Exception
    {
        final Document doc = new JdomHelper().parse( url );
        final List<Country> list = CountryXmlParser.parseCountriesXml( doc );
        LOG.info( "Loaded country codes from [" + url.toExternalForm() + "]" );
        return list;
    }
}
