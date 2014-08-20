module api.schema.relationshiptype {

    export class UpdateRelationshipTypeRequest extends RelationshipTypeResourceRequest<json.RelationshipTypeJson, RelationshipType> {

        private relationshipTypeToUpdate:RelationshipTypeName

        private name:RelationshipTypeName

        private config:string;

        private icon:api.icon.Icon;

        constructor(relationshipTypeToUpdate:RelationshipTypeName, name:RelationshipTypeName, config:string,
                    icon:api.icon.Icon) {
            super();
            super.setMethod("POST");
            this.relationshipTypeToUpdate = relationshipTypeToUpdate;
            this.name = name;
            this.config = config;
            this.icon = icon;
        }

        getParams():Object {
            return {
                "relationshipTypeToUpdate": this.relationshipTypeToUpdate.toString(),
                "name": this.name.toString(),
                "config": this.config,
                "icon": this.icon != null ? this.icon.toJson() : null
            }
        }

        getRequestPath():api.rest.Path {
            return api.rest.Path.fromParent(super.getResourcePath(), 'update');
        }

        sendAndParse(): wemQ.Promise<RelationshipType> {

            return this.send().then((response: api.rest.JsonResponse<json.RelationshipTypeJson>) => {
                return this.fromJsonToReleationshipType(response.getResult());
            });
        }
    }
}