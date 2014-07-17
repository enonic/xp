module api.content.site.template {

    import GetAllSiteTemplatesRequest = api.content.site.template.GetAllSiteTemplatesRequest;
    import SiteTemplateSummary = api.content.site.template.SiteTemplateSummary;

    export class SiteTemplateLoader extends api.util.loader.BaseLoader<api.content.site.template.SiteTemplateSummaryListJson, SiteTemplateSummary> {

        private preservedSearchString: string;

        constructor() {
            super(new GetAllSiteTemplatesRequest());
        }

        search(searchString: string) {

            if (this.loading()) {
                this.preservedSearchString = searchString;
                return;
            }

            this.load();
        }

        load() {
            this.loading(true)
            this.notifyLoadingData();

            this.sendRequest()
                .done((siteTemplates: SiteTemplateSummary[]) => {

                    this.loading(false);
                    this.notifyLoadedData(siteTemplates);
                    if (this.preservedSearchString) {
                        this.search(this.preservedSearchString);
                        this.preservedSearchString = null;
                    }
                });
            return null;
        }
    }
}