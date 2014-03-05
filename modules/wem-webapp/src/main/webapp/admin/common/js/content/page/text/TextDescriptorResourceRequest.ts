module api.content.page.text {

    export class TextDescriptorResourceRequest<JSON> extends api.rest.ResourceRequest<JSON> {

        private resourcePath: api.rest.Path;

        constructor() {
            super();
            this.resourcePath = api.rest.Path.fromParent(super.getRestPath(), "content", "page", "text", "descriptor");
        }

        getResourcePath(): api.rest.Path {
            return this.resourcePath;
        }

        fromJsonToTextDescriptor(json: json.TextDescriptorJson): TextDescriptor {

            return new api.content.page.text.TextDescriptorBuilder().fromJson(json).build();
        }
    }
}