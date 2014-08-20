module api.schema.content {

    export class CreateContentTypeRequest extends ContentTypeResourceRequest<api.schema.content.json.ContentTypeJson, ContentType> {

        private name: string;

        private config: string;

        private icon: api.icon.Icon;

        constructor(name: string, contentType: string, icon: api.icon.Icon) {
            super();
            super.setMethod('POST');
            this.name = name;
            this.config = contentType;
            this.icon = icon;
        }

        getParams(): Object {
            return {
                name: this.name,
                config: this.config,
                icon: this.icon != null ? this.icon.toJson() : null
            }
        }

        getRequestPath(): api.rest.Path {
            return api.rest.Path.fromParent(super.getResourcePath(), "create");
        }

        sendAndParse(): Q.Promise<ContentType> {

            return this.send().then((response: api.rest.JsonResponse<api.schema.content.json.ContentTypeJson>) => {
                var contentType = this.fromJsonToContentType(response.getResult());
                ContentTypeCache.get().put(contentType);
                return  contentType;
            });
        }
    }

}