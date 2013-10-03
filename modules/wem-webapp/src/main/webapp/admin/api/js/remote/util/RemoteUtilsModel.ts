module api_remote_util {

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

    export interface SystemGetSystemInfoResult {
        installationName: string;
        version: string;
        title: string;
    }

    export interface GetCountriesParams {
    }

    export interface GetCountriesResult {
        total: number;
        countries: Country[];
    }

    export interface GetLocalesParams {
    }

    export interface GetLocalesResult {
        total: number;
        locales: Locale[];
    }

    export interface GetTimeZonesParams {
    }

    export interface GetTimeZonesResult {
        total: number;
        timezones: TimeZone[];
    }
}