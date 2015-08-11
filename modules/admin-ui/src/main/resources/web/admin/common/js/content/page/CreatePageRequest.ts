module api.content.page {

    export class CreatePageRequest extends PageResourceRequest<api.content.json.ContentJson, api.content.Content> implements PageCUDRequest {

        private contentId: api.content.ContentId;

        private controller: api.content.page.DescriptorKey;

        private template: api.content.page.PageTemplateKey;

        private config: api.data.PropertyTree;

        private regions: api.content.page.region.Regions;

        private customized: boolean;

        constructor(contentId: api.content.ContentId) {
            super();
            super.setMethod("POST");
            this.contentId = contentId;
        }

        setController(controller: api.content.page.DescriptorKey): CreatePageRequest {
            this.controller = controller;
            return this;
        }

        setPageTemplateKey(pageTemplateKey: api.content.page.PageTemplateKey): CreatePageRequest {
            this.template = pageTemplateKey;
            return this;
        }

        setConfig(config: api.data.PropertyTree): CreatePageRequest {
            this.config = config;
            return this;
        }

        setRegions(value: api.content.page.region.Regions): CreatePageRequest {
            this.regions = value;
            return this;
        }

        setCustomized(value: boolean): CreatePageRequest {
            this.customized = value;
            return this;
        }

        getParams(): Object {
            return {
                contentId: this.contentId.toString(),
                controller: this.controller ? this.controller.toString() : null,
                template: this.template ? this.template.toString() : null,
                config: this.config ? this.config.toJson() : null,
                regions: this.regions != null ? this.regions.toJson() : null,
                customized: this.customized
            };
        }

        getRequestPath(): api.rest.Path {
            return api.rest.Path.fromParent(super.getResourcePath(), "create");
        }

        sendAndParse(): wemQ.Promise<api.content.Content> {

            return this.send().then((response: api.rest.JsonResponse<api.content.json.ContentJson>) => {
                return response.isBlank() ? null : this.fromJsonToContent(response.getResult());
            });
        }
    }
}