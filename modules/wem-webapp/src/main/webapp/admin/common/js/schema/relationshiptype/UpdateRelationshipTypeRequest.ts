module api_schema_relationshiptype {

    export class UpdateRelationshipTypeRequest extends RelationshipTypeResourceRequest<any> {

        private relationshipTypeToUpdate:RelationshipTypeName

        private name:RelationshipTypeName

        private config:string;

        private iconReference:string;

        constructor(relationshipTypeToUpdate:RelationshipTypeName, name:RelationshipTypeName, config:string,
                    iconReference:string) {
            super();
            super.setMethod("POST");
            this.relationshipTypeToUpdate = relationshipTypeToUpdate;
            this.name = name;
            this.config = config;
            this.iconReference = iconReference;
        }

        getParams():Object {
            return {
                "relationshipTypeToUpdate": this.relationshipTypeToUpdate.toString(),
                "name": this.name.toString(),
                "config": this.config,
                "iconReference": this.iconReference
            }
        }

        getRequestPath():api_rest.Path {
            return api_rest.Path.fromParent(super.getResourcePath(), 'update');
        }
    }
}