module api.content.page {

    import Content = api.content.Content;
    import ContentJson = api.content.json.ContentJson;

    export class CreatePageTemplateRequest
        extends PageTemplateResourceRequest<ContentJson, Content>
        implements PageCUDRequest {

        private controller: api.content.page.DescriptorKey;

        private config: api.data.PropertyTree;

        private regions: api.content.page.region.Regions;

        private displayName: string;

        private name: ContentName;

        private site: ContentPath;

        private supports: ContentTypeName[];

        constructor() {
            super();
            super.setMethod('POST');
        }

        setController(controller: api.content.page.DescriptorKey): CreatePageTemplateRequest {
            this.controller = controller;
            return this;
        }

        setConfig(config: api.data.PropertyTree): CreatePageTemplateRequest {
            this.config = config;
            return this;
        }

        setRegions(value: api.content.page.region.Regions): CreatePageTemplateRequest {
            this.regions = value;
            return this;
        }

        setDisplayName(value: string): CreatePageTemplateRequest {
            this.displayName = value;
            return this;
        }

        setName(value: api.content.ContentName): CreatePageTemplateRequest {
            this.name = value;
            return this;
        }

        setSite(value: ContentPath): CreatePageTemplateRequest {
            this.site = value;
            return this;
        }

        setSupports(...value: api.schema.content.ContentTypeName[]): CreatePageTemplateRequest {
            this.supports = value;
            return this;
        }

        getParams(): Object {
            return {
                controller: this.controller ? this.controller.toString() : null,
                config: this.config ? this.config.toJson() : null,
                regions: this.regions != null ? this.regions.toJson() : null,
                displayName: this.displayName,
                name: this.name.toString(),
                site: this.site.toString(),
                supports: this.supports.map(name => name.toString())
            };
        }

        getRequestPath(): api.rest.Path {
            return api.rest.Path.fromParent(super.getResourcePath(), 'create');
        }

        sendAndParse(): wemQ.Promise<Content> {

            return this.send().then((response: api.rest.JsonResponse<api.content.json.ContentJson>) => {
                return response.isBlank() ? null : this.fromJsonToContent(response.getResult());
            });
        }
    }
}
