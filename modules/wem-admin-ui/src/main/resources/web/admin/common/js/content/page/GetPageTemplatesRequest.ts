module api.content.page {

    import ContentJson = api.content.json.ContentJson;
    import ListContentResult = api.content.ListContentResult;

    export class GetPageTemplatesRequest extends PageTemplateResourceRequest<ListContentResult<ContentJson>, PageTemplate[]> {

        private siteTemplateKey: api.content.site.template.SiteTemplateKey;

        constructor(siteTemplateKey: api.content.site.template.SiteTemplateKey) {
            super();
            super.setMethod("GET");
            this.siteTemplateKey = siteTemplateKey;
        }

        getParams(): Object {
            return {
                key: this.siteTemplateKey.toString()
            };
        }

        getRequestPath(): api.rest.Path {
            return api.rest.Path.fromParent(super.getResourcePath(), "list");
        }

        sendAndParse(): wemQ.Promise<PageTemplate[]> {

            return this.send().then((response: api.rest.JsonResponse<ListContentResult<ContentJson>>) => {
                return response.getResult().contents.map((contentJson: ContentJson) => {
                    return this.fromJsonToContent(contentJson);
                });
            });
        }
    }
}
