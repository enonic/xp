package com.enonic.wem.core.country;

import java.io.File;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;

import javax.inject.Inject;

import org.jdom.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import com.enonic.wem.core.config.SystemConfig;
import com.enonic.wem.core.support.util.JdomHelper;

/**
 * This implements the country service. It load country codes from an xml file. It tries to find the first resource that exists and load
 * it.
 */
@Component
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
        final Resource res = findCountryResource();
        for ( final Country country : readCountries( res ) )
        {
            this.countriesMapByCode.put( country.getCode(), country );
        }
    }

    private Resource findCountryResource()
    {
        final File countryFile = new File( this.systemConfig.getConfigDir(), "countries.xml" );
        if ( countryFile.exists() && countryFile.isFile() )
        {
            return new FileSystemResource( countryFile );
        }
        else
        {
            return new ClassPathResource( "countries.xml", getClass() );
        }
    }

    private List<Country> readCountries( Resource resource )
        throws Exception
    {
        if ( !resource.exists() )
        {
            throw new IllegalArgumentException( "Country code resource [" + resource.getDescription() + "] was not found" );
        }

        Document doc = new JdomHelper().parse( resource.getInputStream() );
        List<Country> list = CountryXmlParser.parseCountriesXml( doc );
        LOG.info( "Loaded country codes from [" + resource.getDescription() + "]" );
        return list;
    }

    @Inject
    public void setSystemConfig( final SystemConfig systemConfig )
    {
        this.systemConfig = systemConfig;
    }
}
