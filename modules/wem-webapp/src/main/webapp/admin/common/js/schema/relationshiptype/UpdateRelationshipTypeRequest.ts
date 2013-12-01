module api_schema_relationshiptype {

    export class UpdateRelationshipTypeRequest extends RelationshipTypeResourceRequest<any> {

        private config:string;

        private iconReference:string;

        constructor(relationshipType:string, iconReference:string) {
            super();
            super.setMethod("POST");
            this.config = relationshipType;
            this.iconReference = iconReference;
        }

        getParams():Object {
            return {
                "config": this.config,
                "iconReference": this.iconReference
            }
        }

        getRequestPath():api_rest.Path {
            return api_rest.Path.fromParent(super.getResourcePath(), 'update');
        }
    }
}