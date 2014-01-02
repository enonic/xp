module api.content.page {

    export class UpdatePageRequest extends PageResourceRequest<api.content.json.ContentJson> {

        private contentId: api.content.ContentId;
        private pageTemplateKey: api.content.page.PageTemplateKey;
        private config: api.data.Data[];

        constructor(contentId: api.content.ContentId) {
            super();
            super.setMethod("POST");
            this.contentId = contentId;
        }

        setPageTemplateKey(pageTemplateKey: api.content.page.PageTemplateKey): UpdatePageRequest {
            this.pageTemplateKey = pageTemplateKey;
            return this;
        }

        setConfig(config: api.data.Data[]): UpdatePageRequest {
            this.config = config;
            return this;
        }

        getParams(): Object {
            return {
                contentId: this.contentId.toString(),
                pageTemplateKey: this.pageTemplateKey.toString(),
                config: this.config
            };
        }

        getRequestPath(): api.rest.Path {
            return api.rest.Path.fromParent(super.getResourcePath(), "update");
        }
    }
}