module app_new {

    export class SiteRootContentTypes {

        private contentTypeByName:boolean[];

        public static load(callback:(siteRootContentTypes:SiteRootContentTypes) => void) {
            var contentTypeByName:boolean[] = [];

            // uncomment after backend is ready
            if( false ) {
                new api_content_site_template.GetAllSiteTemplatesRequest().send()
                    .done( (response:api_rest.JsonResponse<api_content_site_template_json.SiteTemplateSummaryListJson>) => {

                    var siteTemplates:api_content_site_template.SiteTemplateSummary[] = api_content_site_template.SiteTemplateSummary.fromJsonArray(response.getResult().siteTemplates);

                    siteTemplates.forEach((siteTemplate:api_content_site_template.SiteTemplateSummary) => {
                        contentTypeByName[siteTemplate.getRootContentType()] = true;
                    });

                    callback(new SiteRootContentTypes(contentTypeByName));
                });
            }
            else {
                contentTypeByName["space"] = true;
                callback(new SiteRootContentTypes(contentTypeByName));
            }

        }

        constructor(contentTypeByName:boolean[]) {
            this.contentTypeByName = contentTypeByName;
        }

        isSiteRoot(name:string):boolean {
            return this.contentTypeByName[name] == true;
        }
    }
}