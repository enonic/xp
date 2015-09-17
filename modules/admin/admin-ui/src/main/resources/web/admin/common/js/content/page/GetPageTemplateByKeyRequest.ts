module api.content.page {

    import ContentJson = api.content.json.ContentJson;

    export class GetPageTemplateByKeyRequest extends PageTemplateResourceRequest<ContentJson, PageTemplate> {

        private pageTemplateKey: PageTemplateKey;

        constructor(pageTemplateKey: PageTemplateKey) {
            super();
            super.setMethod("GET");
            this.pageTemplateKey = pageTemplateKey;
        }

        validate() {
            api.util.assertNotNull(this.pageTemplateKey, "pageTemplateKey cannot be null");
        }

        getParams(): Object {
            return {
                key: this.pageTemplateKey.toString()
            };
        }

        getRequestPath(): api.rest.Path {
            return super.getResourcePath();
        }

        sendAndParse(): wemQ.Promise<PageTemplate> {

            return this.send().then((response: api.rest.JsonResponse<ContentJson>) => {
                return this.fromJsonToContent(response.getResult());
            });
        }
    }
}