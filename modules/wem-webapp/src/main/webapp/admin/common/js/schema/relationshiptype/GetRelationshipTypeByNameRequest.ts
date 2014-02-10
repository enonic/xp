module api.schema.relationshiptype {

    export class GetRelationshipTypeByNameRequest extends RelationshipTypeResourceRequest<json.RelationshipTypeJson> {

        private name: RelationshipTypeName;

        constructor(name: RelationshipTypeName) {
            super();
            super.setMethod("GET");
            this.name = name;
        }

        getParams(): Object {
            return {
                name: this.name.toString()
            };
        }

        getRequestPath(): api.rest.Path {
            return super.getResourcePath();
        }

        sendAndParse(): Q.Promise<RelationshipType> {

            var deferred = Q.defer<RelationshipType>();

            this.send().
                done((response: api.rest.JsonResponse<json.RelationshipTypeJson>) => {

                    deferred.resolve(this.fromJsonToReleationshipType(response.getResult()));
            });

            return deferred.promise;
        }
    }
}