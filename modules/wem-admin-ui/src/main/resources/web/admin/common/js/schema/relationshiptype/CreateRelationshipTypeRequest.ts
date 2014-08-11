module api.schema.relationshiptype {

    export class CreateRelationshipTypeRequest extends RelationshipTypeResourceRequest<json.RelationshipTypeJson, RelationshipType> {

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

            return this.send().then((response: api.rest.JsonResponse<json.RelationshipTypeJson>) => {
                return this.fromJsonToReleationshipType(response.getResult());
            });
        }
    }
}