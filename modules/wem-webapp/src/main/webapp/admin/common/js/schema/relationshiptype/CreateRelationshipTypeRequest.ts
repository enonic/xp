module api_schema_relationshiptype {

    export class CreateRelationshipTypeRequest extends RelationshipTypeResourceRequest<api_schema_relationshiptype_json.RelationshipTypeJson> {

        private name: RelationshipTypeName;

        private config: string;

        private icon: api_icon.Icon;

        constructor(name: RelationshipTypeName, config: string, icon: api_icon.Icon) {
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

        getRequestPath(): api_rest.Path {
            return api_rest.Path.fromParent(super.getResourcePath(), 'create');
        }

        sendAndParse(): JQueryPromise<RelationshipType> {

            var deferred = jQuery.Deferred<RelationshipType>();

            this.send().done((response: api_rest.JsonResponse<api_schema_relationshiptype_json.RelationshipTypeJson>) => {
                deferred.resolve(this.fromJsonToReleationshipType(response.getResult()));
            }).fail((response: api_rest.RequestError) => {
                    deferred.reject(null);
                });

            return deferred;
        }
    }
}