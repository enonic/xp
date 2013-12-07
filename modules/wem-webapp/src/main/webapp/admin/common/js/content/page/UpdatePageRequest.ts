module api_content_page {

    export class UpdatePageRequest extends PageResourceRequest<api_content_json.ContentJson> {

        private contentId: string;
        private pageTemplateKey: string;
        private config: api_data.Data[];

        constructor(contentId: string) {
            super();
            super.setMethod("POST");
            this.contentId = contentId;
        }

        setPageTemplateKey(pageTemplateKey: string): UpdatePageRequest {
            this.pageTemplateKey = pageTemplateKey;
            return this;
        }

        setConfig(config: api_data.Data[]): UpdatePageRequest {
            this.config = config;
            return this;
        }

        getParams(): Object {
            return {
                contentId: this.contentId,
                pageTemplateKey: this.pageTemplateKey,
                config: this.config
            };
        }

        getRequestPath(): api_rest.Path {
            return api_rest.Path.fromParent(super.getResourcePath(), "update");
        }
    }
}