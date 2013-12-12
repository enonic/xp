module app_new {

    export class SiteRootContentTypes {

        private contentTypeByName:boolean[];

        public static load(callback:(siteRootContentTypes:SiteRootContentTypes) => void) {
            var contentTypeByName:boolean[] = [];

            new api_content_site_template.GetAllSiteTemplatesRequest().send()
                .done( (response:api_rest.JsonResponse<api_content_site_template_json.SiteTemplateSummaryListJson>) => {

                //TODO: use getResult() after it is modified to return result
                var responseJson:api_content_site_template_json.SiteTemplateSummaryListJson = response.getJson().result;
                var siteTemplates:api_content_site_template.SiteTemplateSummary[] = api_content_site_template.SiteTemplateSummary.fromJsonArray(responseJson.siteTemplates);

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