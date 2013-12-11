module api_schema_relationshiptype {

    export class GetRelationshipTypeByNameRequest extends RelationshipTypeResourceRequest<api_schema_relationshiptype_json.RelationshipTypeJson> {

        private name:RelationshipTypeName;

        constructor(name:RelationshipTypeName) {
            super();
            super.setMethod("GET");
            this.name = name;
        }

        getParams():Object {
            return {
                name: this.name.toString()
            };
        }

        getRequestPath():api_rest.Path {
            return super.getResourcePath();
        }

        sendAndParse(): JQueryPromise<api_schema_relationshiptype.RelationshipType> {

            var deferred = jQuery.Deferred<api_schema_relationshiptype_json.RelationshipTypeJson>();

            this.send().done((response: api_rest.JsonResponse<api_schema_relationshiptype_json.RelationshipTypeJson>) => {
                deferred.resolve(this.fromJsonToReleationshipType(response.getResult()));
            }).fail((response: api_rest.RequestError) => {
                    deferred.reject(null);
                });

            return deferred;
        }
    }
}