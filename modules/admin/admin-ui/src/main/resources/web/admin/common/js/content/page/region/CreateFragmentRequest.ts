module api.content.page.region {

    export class CreateFragmentRequest extends FragmentResourceRequest<api.content.json.ContentJson, api.content.Content> {

        private contentId: api.content.ContentId;

        private config: api.data.PropertyTree;

        private component: api.content.page.region.Component;

        constructor(contentId: api.content.ContentId) {
            super();
            super.setMethod('POST');
            this.contentId = contentId;
        }

        setConfig(config: api.data.PropertyTree): CreateFragmentRequest {
            this.config = config;
            return this;
        }

        setComponent(value: api.content.page.region.Component): CreateFragmentRequest {
            this.component = value;
            return this;
        }

        getParams(): Object {
            return {
                contentId: this.contentId.toString(),
                config: this.config ? this.config.toJson() : null,
                component: this.component != null ? this.component.toJson() : null
            };
        }

        getRequestPath(): api.rest.Path {
            return api.rest.Path.fromParent(super.getResourcePath(), 'create');
        }

        sendAndParse(): wemQ.Promise<api.content.Content> {

            return this.send().then((response: api.rest.JsonResponse<api.content.json.ContentJson>) => {
                return response.isBlank() ? null : this.fromJsonToContent(response.getResult());
            });
        }
    }
}
