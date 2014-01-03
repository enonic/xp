module app.create {

    export class ContentTypes {

        private contentTypes: api.schema.content.ContentTypeSummary[];

        static load(callback: (contentTypes: ContentTypes) => void) {
            var request = new api.schema.content.GetAllContentTypesRequest();
            request.send().done((response: api.rest.JsonResponse<api.schema.content.json.ContentTypeSummaryListJson>) => {
                var contentTypes = api.schema.content.ContentTypeSummary.fromJsonArray(response.getResult().contentTypes);
                callback(new ContentTypes(contentTypes));
            });
        }

        constructor(contentTypes: api.schema.content.ContentTypeSummary[]) {
            this.contentTypes = contentTypes;
        }

        get(): api.schema.content.ContentTypeSummary[] {
            return this.contentTypes;
        }

        getByName(contentTypeName: api.schema.content.ContentTypeName): api.schema.content.ContentTypeSummary {
            var contentType: api.schema.content.ContentTypeSummary;
            for (var i = 0; i < this.contentTypes.length; i++) {
                contentType = this.contentTypes[i];
                if (contentTypeName.toString() == contentType.getName()) {
                    return contentType;
                }
            }
            return undefined;
        }

        filter(contentTypeNames: api.schema.content.ContentTypeName[]): ContentTypes {

            var filteredContentTypes: api.schema.content.ContentTypeSummary[] = [];
            this.contentTypes.forEach((contentType: api.schema.content.ContentTypeSummary)=> {
                if (this.existsIn(contentType.getName(), contentTypeNames)) {
                    filteredContentTypes.push(contentType);
                }
            });
            return new ContentTypes(filteredContentTypes);
        }

        private existsIn(contentTypeName: string, contentTypeNames: api.schema.content.ContentTypeName[]) {
            for (var i = 0; i < contentTypeNames.length; i++) {
                if (contentTypeName == contentTypeNames[i].toString()) {
                    return true;
                }
            }
            return false;
        }
    }
}