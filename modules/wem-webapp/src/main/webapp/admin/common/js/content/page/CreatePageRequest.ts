module api.content.page {

    export class CreatePageRequest extends PageResourceRequest<api.content.json.ContentJson> {

        private contentId: api.content.ContentId;

        private pageTemplateKey: api.content.page.PageTemplateKey;

        private config: api.data.RootDataSet;

        constructor(contentId: api.content.ContentId) {
            super();
            super.setMethod("POST");
            this.contentId = contentId;
        }

        setPageTemplateKey(pageTemplateKey: api.content.page.PageTemplateKey): CreatePageRequest {
            this.pageTemplateKey = pageTemplateKey;
            return this;
        }

        setConfig(config: api.data.Data[]): CreatePageRequest {
            this.config = config;
            return this;
        }

        getParams(): Object {
            return {
                contentId: this.contentId.toString(),
                pageTemplateKey: this.pageTemplateKey.toString(),
                config: this.config.toJson()
            };
        }

        getRequestPath(): api.rest.Path {
            return api.rest.Path.fromParent(super.getResourcePath(), "create");
        }
    }
}