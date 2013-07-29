module api_remote {

    export interface CallingCode {
        callingCodeId: string;
        callingCode: string;
        englishName: string;
        localName: string;
    }


    export interface Region {
        regionCode: string;
        englishName: string;
        localName: string;
    }

    export interface Country {
        code: string;
        englishName: string;
        localName: string;
        regionsEnglishName: string;
        regionsLocalName: string;
        callingCodes: CallingCode[];
        regions: Region[];
    }

    export interface Locale {
        id: string;
        displayName: string;
    }

    export interface TimeZone {
        id: string;
        humanizedId: string;
        shortName: string;
        name: string;
        offset: string;
    }

    export interface SystemGetSystemInfoParams {
    }

    export interface SystemGetSystemInfoResult extends ResultBase {
        installationName: string;
        version: string;
        title: string;
    }

    export interface GetCountriesParams {
    }

    export interface GetCountriesResult extends ResultBase {
        total: number;
        countries: Country[];
    }

    export interface GetLocalesParams {
    }

    export interface GetLocalesResult extends ResultBase {
        total: number;
        locales: Locale[];
    }

    export interface GetTimeZonesParams {
    }

    export interface GetTimeZonesResult extends ResultBase {
        total: number;
        timezones: TimeZone[];
    }
}