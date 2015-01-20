module api.locale {

    import LocaleListJson = api.locale.json.LocaleListJson;
    import LocaleJson = api.locale.json.LocaleJson;

    export class GetLocalesRequest extends api.rest.ResourceRequest<LocaleListJson, Locale[]> {

        private searchQuery: string;

        constructor() {
            super();
        }

        getParams(): Object {
            return {
                "query": this.searchQuery
            }
        }

        getRequestPath(): api.rest.Path {
            return api.rest.Path.fromParent(super.getRestPath(), 'content', 'locales');
        }

        setSearchQuery(query: string): GetLocalesRequest {
            this.searchQuery = query;
            return this;
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