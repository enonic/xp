module api_schema_relationshiptype {

    export class CreateRelationshipTypeRequest extends RelationshipTypeResourceRequest<any> {

        private name:RelationshipTypeName;

        private config:string;

        private iconReference:string;

        constructor(name:RelationshipTypeName, config:string, iconReference:string) {
            super();
            super.setMethod("POST");
            this.name = name;
            this.config = config;
            this.iconReference = iconReference;
        }

        getParams():Object {
            return {
                "name": this.name.toString(),
                "config": this.config,
                "iconReference": this.iconReference
            }
        }

        getRequestPath():api_rest.Path {
            return api_rest.Path.fromParent(super.getResourcePath(), 'create');
        }
    }
}