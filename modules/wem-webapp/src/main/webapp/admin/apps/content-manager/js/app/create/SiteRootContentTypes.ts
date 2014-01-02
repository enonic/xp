module app.create {

    export class SiteRootContentTypes {

        private contentTypeByName: boolean[];

        private siteTemplates: api.content.site.template.SiteTemplateSummary[];

        public static load(callback: (siteRootContentTypes: SiteRootContentTypes) => void) {
            var contentTypeByName: boolean[] = [];

            new api.content.site.template.GetAllSiteTemplatesRequest().sendAndParse()
                .done((siteTemplates: api.content.site.template.SiteTemplateSummary[]) => {

                    siteTemplates.forEach((siteTemplate: api.content.site.template.SiteTemplateSummary) => {
                        contentTypeByName[siteTemplate.getRootContentType().toString()] = true;
                    });

                    callback(new SiteRootContentTypes(contentTypeByName, siteTemplates));
                });
        }

        constructor(contentTypeByName: boolean[], siteTemplates:api.content.site.template.SiteTemplateSummary[]) {
            this.contentTypeByName = contentTypeByName;
            this.siteTemplates = siteTemplates;
        }

        isSiteRoot(name: string): boolean {
            return this.contentTypeByName[name] == true;
        }

        getSiteTemplates(): api.content.site.template.SiteTemplateSummary[] {
            return this.siteTemplates;
        }
    }
}