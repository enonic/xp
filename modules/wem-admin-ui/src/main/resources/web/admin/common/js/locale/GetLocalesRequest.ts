module api.locale {

    import LocaleListJson = api.locale.json.LocaleListJson;
    import LocaleJson = api.locale.json.LocaleJson;

    export class GetLocalesRequest extends api.rest.ResourceRequest<LocaleListJson, Locale[]> {

        constructor() {
            super();
        }

        getParams(): Object {
            return {}
        }

        getRequestPath(): api.rest.Path {
            return api.rest.Path.fromParent(super.getRestPath(), 'content', 'locales');
        }

        sendAndParse(): wemQ.Promise<Locale[]> {
            return this.send().
                then((response: api.rest.JsonResponse<LocaleListJson>) => {
                    return response.getResult().locales.map((localeJson: LocaleJson) => {
                        return Locale.fromJson(localeJson);
                    }).sort(this.sortFunction);
                });
        }

        private sortFunction(a: Locale, b: Locale) {
            return a.getDisplayName().localeCompare(b.getDisplayName());
        }
    }
}