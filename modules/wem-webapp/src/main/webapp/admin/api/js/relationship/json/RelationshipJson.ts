module api_relationship_json {

    export class RelationshipJson {

        type:string;
        fromContent:string;
        toContent:string;
        managingData:string;
        properties:any;
        editable:boolean;
        deletable:boolean;

        modifiedTime:string;
        createdTime:string;
        creator:string;
        modifier:string;
        id:string;
    }
}
