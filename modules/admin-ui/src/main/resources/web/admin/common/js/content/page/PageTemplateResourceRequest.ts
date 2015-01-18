module api.content.page {

    import PropertyIdProvider = api.data.PropertyIdProvider;

    export class PageTemplateResourceRequest<JSON_TYPE, PARSED_TYPE> extends api.rest.ResourceRequest<JSON_TYPE, PARSED_TYPE> {

        private resourcePath: api.rest.Path;

        private propertyIdProvider: PropertyIdProvider;

        constructor() {
            super();
            this.resourcePath = api.rest.Path.fromParent(super.getRestPath(), "content", "page", "template");

            this.propertyIdProvider = api.Client.get().getPropertyIdProvider();
        }

        getResourcePath(): api.rest.Path {
            return this.resourcePath;
        }

        fromJsonToContent(json: api.content.json.ContentJson): PageTemplate {
            return <PageTemplate>api.content.Content.fromJson(json, this.propertyIdProvider);
        }
    }
}