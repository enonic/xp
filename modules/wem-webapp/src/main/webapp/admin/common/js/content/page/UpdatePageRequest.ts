module api_content_page {

    export class UpdatePageRequest extends PageResourceRequest<api_content_json.ContentJson> {

        private contentId: api_content.ContentId;
        private pageTemplateKey: api_content_page.PageTemplateKey;
        private config: api_data.Data[];

        constructor(contentId: api_content.ContentId) {
            super();
            super.setMethod("POST");
            this.contentId = contentId;
        }

        setPageTemplateKey(pageTemplateKey: api_content_page.PageTemplateKey): UpdatePageRequest {
            this.pageTemplateKey = pageTemplateKey;
            return this;
        }

        setConfig(config: api_data.Data[]): UpdatePageRequest {
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

        getRequestPath(): api_rest.Path {
            return api_rest.Path.fromParent(super.getResourcePath(), "update");
        }
    }
}