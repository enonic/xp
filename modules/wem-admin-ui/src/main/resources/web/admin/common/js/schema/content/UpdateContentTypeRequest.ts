module api.schema.content {

    export class UpdateContentTypeRequest extends ContentTypeResourceRequest<json.ContentTypeJson, ContentType> {

        private contentTypeToUpdate: ContentTypeName;

        private name: ContentTypeName;

        private config: string;

        private icon: api.icon.Icon;

        constructor(contentTypeToUpdate: ContentTypeName, name: ContentTypeName, config: string, icon: api.icon.Icon) {
            super();
            super.setMethod('POST');
            this.contentTypeToUpdate = contentTypeToUpdate;
            this.name = name;
            this.config = config;
            this.icon = icon;
        }

        getParams(): Object {
            return {
                contentTypeToUpdate: this.contentTypeToUpdate.toString(),
                name: this.name.toString(),
                config: this.config,
                icon: this.icon != null ? this.icon.toJson() : null
            }
        }

        getRequestPath(): api.rest.Path {
            return api.rest.Path.fromParent(super.getResourcePath(), "update");
        }

        sendAndParse(): wemQ.Promise<ContentType> {

            return this.send().then((response: api.rest.JsonResponse<json.ContentTypeJson>) => {
                var contentType = this.fromJsonToContentType(response.getResult());
                ContentTypeCache.get().put(contentType);
                return  contentType;
            });
        }
    }
}