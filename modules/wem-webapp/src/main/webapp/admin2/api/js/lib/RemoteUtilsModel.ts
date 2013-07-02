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

}