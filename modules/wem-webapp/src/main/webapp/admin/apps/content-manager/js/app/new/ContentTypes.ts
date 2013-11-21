module app_new {

    export class ContentTypes {

        private contentTypes:api_schema_content.ContentTypeSummary[];

        static load(callback:(contentTypes:ContentTypes) => void) {
            var request = new api_schema_content.GetAllContentTypesRequest();
            request.send().done( (response:api_rest.JsonResponse<api_schema_content_json.ContentTypeSummaryListJson>) => {
                var contentTypes = api_schema_content.ContentTypeSummary.fromJsonArray(response.getResult().contentTypes);
                callback(new ContentTypes(contentTypes));
            });
        }

        constructor(contentTypes:api_schema_content.ContentTypeSummary[]) {
            this.contentTypes = contentTypes;
        }

        get():api_schema_content.ContentTypeSummary[] {
            return this.contentTypes;
        }

        filter(contentTypeNames:api_schema_content.ContentTypeName[]):ContentTypes {

            var filteredContentTypes:api_schema_content.ContentTypeSummary[] = [];
            this.contentTypes.forEach((contentType:api_schema_content.ContentTypeSummary)=>{
                if( this.existsIn(contentType.getName(), contentTypeNames) ){
                    filteredContentTypes.push(contentType);
                }
            });
            return new ContentTypes(filteredContentTypes);
        }

        private existsIn(contentTypeName:string, contentTypeNames:api_schema_content.ContentTypeName[]) {
            for(var i = 0; i < contentTypeNames.length; i++ ){
                if( contentTypeName == contentTypeNames[i].toString() ){
                    return true;
                }
            }
            return false;
        }
    }
}