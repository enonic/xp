package com.enonic.wem.core.country;

import java.io.File;
import java.net.URL;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;

import javax.inject.Inject;

import org.jdom.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.wem.core.config.SystemConfig;
import com.enonic.wem.core.lifecycle.InitializingBean;
import com.enonic.wem.core.support.util.JdomHelper;

/**
 * This implements the country service. It load country codes from an xml file. It tries to find the first resource that exists and load
 * it.
 */

public final class CountryServiceImpl
    implements CountryService, InitializingBean
{
    private final static Logger LOG = LoggerFactory.getLogger( CountryServiceImpl.class );

    private final LinkedHashMap<CountryCode, Country> countriesMapByCode;

    private SystemConfig systemConfig;

    public CountryServiceImpl()
    {
        this.countriesMapByCode = new LinkedHashMap<>();
    }

    public Collection<Country> getCountries()
    {
        return this.countriesMapByCode.values();
    }

    public Country getCountry( CountryCode countryCode )
    {
        return this.countriesMapByCode.get( countryCode );
    }

    public void afterPropertiesSet()
        throws Exception
    {
        final URL url = findCountryResource();
        for ( final Country country : readCountries( url ) )
        {
            this.countriesMapByCode.put( country.getCode(), country );
        }
    }

    private URL findCountryResource()
        throws Exception
    {
        final File countryFile = new File( this.systemConfig.getConfigDir(), "countries.xml" );
        if ( countryFile.exists() && countryFile.isFile() )
        {
            return countryFile.toURI().toURL();
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

    @Inject
    public void setSystemConfig( final SystemConfig systemConfig )
    {
        this.systemConfig = systemConfig;
    }
}
