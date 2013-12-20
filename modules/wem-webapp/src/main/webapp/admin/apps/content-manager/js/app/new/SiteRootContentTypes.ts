module app_new {

    export class SiteRootContentTypes {

        private contentTypeByName: boolean[];

        private siteTemplates: api_content_site_template.SiteTemplateSummary[];

        public static load(callback: (siteRootContentTypes: SiteRootContentTypes) => void) {
            var contentTypeByName: boolean[] = [];

            new api_content_site_template.GetAllSiteTemplatesRequest().sendAndParse()
                .done((siteTemplates: api_content_site_template.SiteTemplateSummary[]) => {

                    siteTemplates.forEach((siteTemplate: api_content_site_template.SiteTemplateSummary) => {
                        contentTypeByName[siteTemplate.getRootContentType().toString()] = true;
                    });

                    callback(new SiteRootContentTypes(contentTypeByName, siteTemplates));
                });
        }

        constructor(contentTypeByName: boolean[], siteTemplates:api_content_site_template.SiteTemplateSummary[]) {
            this.contentTypeByName = contentTypeByName;
            this.siteTemplates = siteTemplates;
        }

        isSiteRoot(name: string): boolean {
            return this.contentTypeByName[name] == true;
        }

        getSiteTemplates(): api_content_site_template.SiteTemplateSummary[] {
            return this.siteTemplates;
        }
    }
}