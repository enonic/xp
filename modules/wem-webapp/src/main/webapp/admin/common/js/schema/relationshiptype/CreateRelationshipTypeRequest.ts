module api.schema.relationshiptype {

    export class CreateRelationshipTypeRequest extends RelationshipTypeResourceRequest<api.schema.relationshiptype.json.RelationshipTypeJson> {

        private name: RelationshipTypeName;

        private config: string;

        private icon: api.icon.Icon;

        constructor(name: RelationshipTypeName, config: string, icon: api.icon.Icon) {
            super();
            super.setMethod("POST");
            this.name = name;
            this.config = config;
            this.icon = icon;
        }

        getParams(): Object {
            return {
                "name": this.name.toString(),
                "config": this.config,
                "icon": this.icon != null ? this.icon.toJson() : null
            }
        }

        getRequestPath(): api.rest.Path {
            return api.rest.Path.fromParent(super.getResourcePath(), 'create');
        }

        sendAndParse(): Q.Promise<RelationshipType> {

            var deferred = Q.defer<RelationshipType>();

            this.send().then((response: api.rest.JsonResponse<api.schema.relationshiptype.json.RelationshipTypeJson>) => {
                    var json = response.getJson();
                    if ( json.result ) {
                        deferred.resolve(this.fromJsonToReleationshipType(response.getResult()));
                    } else if ( json.error ) {
                        deferred.reject(new api.rest.RequestError(null, null, null, json.error.msg));
                    } else {
                        deferred.reject(null);
                    }
                }).catch((response: api.rest.RequestError) => {
                    deferred.reject(null);
                }).done();

            return deferred.promise;
        }
    }
}