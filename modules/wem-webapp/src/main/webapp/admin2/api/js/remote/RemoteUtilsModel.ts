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

    export interface RemoteCallSystemGetSystemInfoParams {
    }

    export interface RemoteCallSystemGetSystemInfoResult extends RemoteCallResultBase {
        installationName: string;
        version: string;
        title: string;
    }

    export interface RemoteCallGetCountriesParams {
    }

    export interface RemoteCallGetCountriesResult extends RemoteCallResultBase {
        total: number;
        countries: Country[];
    }

    export interface RemoteCallGetLocalesParams {
    }

    export interface RemoteCallGetLocalesResult extends RemoteCallResultBase {
        total: number;
        locales: Locale[];
    }

    export interface RemoteCallGetTimeZonesParams {
    }

    export interface RemoteCallGetTimeZonesResult extends RemoteCallResultBase {
        total: number;
        timezones: TimeZone[];
    }
}