package com.enonic.wem.core.country;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.jdom.Document;
import org.jdom.Element;

final class CountryXmlParser
{
    private static final String ATTRNAME_COUNTRY_CODE = "code";

    private static final String ATTRNAME_REGION_CODE = "code";

    private static final String ATTRNAME_ENGLISH_NAME = "english-name";

    private static final String ATTRNAME_LOCAL_NAME = "local-name";

    private static final String ATTRNAME_CALLING_CODE = "calling-code";

    public static List<Country> parseCountriesXml( final Document countriesDoc )
    {
        return parseCountriesXml( countriesDoc.getRootElement() );
    }

    public static List<Country> parseCountriesXml( final Element countriesEl )
    {
        List<Country> countries = new ArrayList<Country>();

        List<Element> countryElements = getChildren( countriesEl, "country" );
        int position = 0;
        for ( Element countryEl : countryElements )
        {
            position++;
            final Country country = parseCountry( countryEl, position );
            countries.add( country );
        }

        return countries;
    }

    private static Country parseCountry( Element countryEl, int position )
    {
        CountryCode code = parseCountryCode( countryEl, position );
        String englishName = parseEnglishName( countryEl, code );
        String localName = parseLocalName( countryEl, code );
        String callingCode = parseCallingCode( countryEl );

        final Country country = new Country( code, englishName, localName, callingCode );
        parseRegions( country, countryEl );
        return country;
    }


    private static CountryCode parseCountryCode( Element countryEl, int position )
    {
        String value = countryEl.getAttributeValue( ATTRNAME_COUNTRY_CODE );
        if ( StringUtils.isEmpty( value ) )
        {
            throw new InvalidCountryXmlException( "Missing " + ATTRNAME_COUNTRY_CODE + " for country #" + position );
        }
        return new CountryCode( value );
    }

    private static String parseEnglishName( Element countryEl, CountryCode countryCode )
    {
        String value = countryEl.getChildText( ATTRNAME_ENGLISH_NAME );
        if ( StringUtils.isEmpty( value ) )
        {
            throw new InvalidCountryXmlException( "Missing " + ATTRNAME_ENGLISH_NAME + " for country: " + countryCode );
        }
        return value;
    }

    private static String parseLocalName( Element countryEl, CountryCode countryCode )
    {
        String value = countryEl.getChildText( ATTRNAME_LOCAL_NAME );
        if ( StringUtils.isEmpty( value ) )
        {
            throw new InvalidCountryXmlException( "Missing " + ATTRNAME_LOCAL_NAME + " for country: " + countryCode );
        }
        return value;
    }

    private static String parseCallingCode( Element countryEl )
    {
        String value = countryEl.getChildText( ATTRNAME_CALLING_CODE );
        if ( null == value )
        {
            return "";
        }
        return value;
    }

    private static void parseRegions( Country country, Element countryEl )
    {
        Element regionsEl = countryEl.getChild( "regions" );
        if ( regionsEl == null )
        {
            return;
        }
        country.setRegionsEnglishName( parseRegionsEnglishName( regionsEl ) );
        country.setRegionsLocalName( parseRegionsLocalName( regionsEl ) );

        int position = 0;
        List<Element> regionElements = getChildren( regionsEl, "region" );
        for ( Element regionEl : regionElements )
        {
            position++;
            Region region = parseRegion( regionEl, country.getCode(), position );
            country.addRegion( region );
        }
    }

    private static String parseRegionsEnglishName( Element regionsEl )
    {
        return regionsEl.getChildText( ATTRNAME_ENGLISH_NAME );
    }

    private static String parseRegionsLocalName( Element regionsEl )
    {
        return regionsEl.getChildText( ATTRNAME_LOCAL_NAME );
    }

    private static Region parseRegion( Element regionEl, CountryCode countryCode, int position )
    {
        String code = parseRegionCode( regionEl, countryCode, position );
        String englishName = parseRegionEnglishName( regionEl, code );
        String localName = parseRegionLocalName( regionEl, code );
        return new Region( code, englishName, localName );
    }

    private static String parseRegionCode( Element regionEl, CountryCode countryCode, int position )
    {
        String value = regionEl.getAttributeValue( ATTRNAME_REGION_CODE );
        if ( StringUtils.isEmpty( value ) )
        {
            throw new InvalidCountryXmlException(
                "Missing " + ATTRNAME_REGION_CODE + " for region #" + position + " in country: " + countryCode );
        }
        return value;
    }

    private static String parseRegionEnglishName( Element regionEl, String regionCode )
    {
        String value = regionEl.getChildText( ATTRNAME_ENGLISH_NAME );
        if ( StringUtils.isEmpty( value ) )
        {
            throw new InvalidCountryXmlException( "Missing " + ATTRNAME_ENGLISH_NAME + " for region: " + regionCode );
        }
        return value;
    }

    private static String parseRegionLocalName( Element regionEl, String regionCode )
    {
        String value = regionEl.getChildText( ATTRNAME_LOCAL_NAME );
        if ( StringUtils.isEmpty( value ) )
        {
            throw new InvalidCountryXmlException( "Missing " + ATTRNAME_LOCAL_NAME + " for region: " + regionCode );
        }
        return value;
    }

    @SuppressWarnings({"unchecked"})
    private static List<Element> getChildren( final Element parentEl, final String name )
    {
        final List list = parentEl.getChildren( name );
        if ( list == null )
        {
            return new ArrayList<Element>();
        }
        return list;
    }
}
