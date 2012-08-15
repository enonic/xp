package com.enonic.wem.web.rest2.resource.country;

import java.util.Collection;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;

import com.enonic.wem.web.rest2.common.JsonResult;

import com.enonic.cms.core.country.Country;
import com.enonic.cms.core.country.Region;

public final class CountryResult
    extends JsonResult
{
    private final Collection<Country> list;

    public CountryResult( final Collection<Country> list )
    {
        this.list = list;
    }

    @Override
    public JsonNode toJson()
    {
        final ObjectNode json = objectNode();
        json.put( "total", this.list.size() );

        final ArrayNode array = json.putArray( "countries" );
        for ( final Country model : this.list )
        {
            array.add( toJson( model ) );
        }

        return json;
    }

    private ObjectNode toJson( final Country model )
    {
        final ObjectNode json = objectNode();
        json.put( "code", model.getCode().toString() );
        json.put( "englishName", model.getEnglishName() );
        json.put( "localName", model.getLocalName() );
        json.put( "regionsEnglishName", model.getRegionsEnglishName() );
        json.put( "regionsLocalName", model.getRegionsLocalName() );

        final ArrayNode codes = json.putArray( "callingCodes" );
        codes.add( toJson( model.getCallingCode() ) );

        final ArrayNode regions = json.putArray( "regions" );
        for ( final Region region : model.getRegions() )
        {
            regions.add( toJson( region ) );
        }

        return json;
    }

    private JsonNode toJson( final String callingCode )
    {
        final ObjectNode json = objectNode();
        json.put( "callingCode", callingCode );
        return json;
    }

    private ObjectNode toJson( final Region model )
    {
        final ObjectNode json = objectNode();
        json.put( "regionCode", model.getCode() );
        json.put( "englishName", model.getEnglishName() );
        json.put( "localName", model.getLocalName() );
        return json;
    }
}
