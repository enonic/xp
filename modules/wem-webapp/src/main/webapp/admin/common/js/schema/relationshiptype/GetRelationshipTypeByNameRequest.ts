module api.schema.relationshiptype {

    export class GetRelationshipTypeByNameRequest extends RelationshipTypeResourceRequest<api.schema.relationshiptype.json.RelationshipTypeJson> {

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

        getRequestPath():api.rest.Path {
            return super.getResourcePath();
        }

        sendAndParse(): JQueryPromise<api.schema.relationshiptype.RelationshipType> {

            var deferred = jQuery.Deferred<api.schema.relationshiptype.json.RelationshipTypeJson>();

            this.send().done((response: api.rest.JsonResponse<api.schema.relationshiptype.json.RelationshipTypeJson>) => {
                deferred.resolve(this.fromJsonToReleationshipType(response.getResult()));
            }).fail((response: api.rest.RequestError) => {
                    deferred.reject(null);
                });

            return deferred;
        }
    }
}