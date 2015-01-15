module api.locale.json {

    export interface LocaleJson {
        tag: string;
        displayName: string;
        language: string;
        displayLanguage: string;
        variant: string;
        displayVariant: string;
        country: string;
        displayCountry: string;
    }

}