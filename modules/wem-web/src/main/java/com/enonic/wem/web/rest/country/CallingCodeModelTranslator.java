package com.enonic.wem.web.rest.country;

import com.enonic.cms.core.country.Country;

/**
 * Class for converting Country to CallingCodeModel
 *
 * @see Country
 * @see CallingCodeModel
 */
public class CallingCodeModelTranslator
{

    static public CallingCodeModel toModel( Country country )
    {
        CallingCodeModel callingCode = new CallingCodeModel();
        callingCode.setCountryCode( country.getCode().toString() );
        callingCode.setCallingCode( "+" + country.getCallingCode() );
        callingCode.setEnglishName( country.getEnglishName() );
        callingCode.setLocalName( country.getLocalName() );

        return callingCode;
    }
}
