module api_schema_relationshiptype {

    export class UpdateRelationshipTypeRequest extends RelationshipTypeResourceRequest
    <any> {

        private relationshipType
:
    string;

private
    iconReference
:
    string;

    constructor(relationshipType
:
    string, iconReference
:
    string
)
{
    super();
    super.setMethod("POST");
    this.relationshipType = relationshipType;
    this.iconReference = iconReference;
}

    getParams()
:
    Object
{
    return {
        "relationshipType": this.relationshipType,
        "iconReference": this.iconReference
    }
}

    getRequestPath()
:
    api_rest.Path
{
    return api_rest.Path.fromParent(super.getResourcePath(), 'update');
}
}
}