module api.locale {

    export class LocaleLoader extends api.util.loader.BaseLoader<api.locale.json.LocaleListJson, api.locale.Locale> {

        constructor() {
            super(new GetLocalesRequest());
        }

        search(searchString: string) {
            this.load();
        }

    }

}