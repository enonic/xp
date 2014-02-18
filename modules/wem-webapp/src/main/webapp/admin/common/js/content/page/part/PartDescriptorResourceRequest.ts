module api.content.page.part {

    export class PartDescriptorResourceRequest<JSON> extends api.rest.ResourceRequest<JSON> {

        private resourcePath: api.rest.Path;

        constructor() {
            super();
            this.resourcePath = api.rest.Path.fromParent(super.getRestPath(), "content", "page", "part", "descriptor");
        }

        getResourcePath(): api.rest.Path {
            return this.resourcePath;
        }

        fromJsonToPartDescriptor(json: json.PartDescriptorJson): PartDescriptor {

            return new api.content.page.part.PartDescriptorBuilder().fromJson(json).build();
        }
    }
}