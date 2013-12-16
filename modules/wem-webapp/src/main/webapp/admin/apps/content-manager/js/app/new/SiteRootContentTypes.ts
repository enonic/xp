module app_new {

    export class SiteRootContentTypes {

        private contentTypeByName:boolean[];

        public static load(callback:(siteRootContentTypes:SiteRootContentTypes) => void) {
            var contentTypeByName:boolean[] = [];

            new api_content_site_template.GetAllSiteTemplatesRequest().sendAndParse()
                .done( (siteTemplates:api_content_site_template.SiteTemplateSummary[]) => {

                siteTemplates.forEach((siteTemplate:api_content_site_template.SiteTemplateSummary) => {
                    contentTypeByName[siteTemplate.getRootContentType().toString()] = true;
                });

                callback(new SiteRootContentTypes(contentTypeByName));
            });
        }

        constructor(contentTypeByName:boolean[]) {
            this.contentTypeByName = contentTypeByName;
        }

        isSiteRoot(name:string):boolean {
            return this.contentTypeByName[name] == true;
        }
    }
}