module api_relationship {

    export class Relationship {

        private id:string;
        private createdTime:Date;
        private creator:string;
        private modifiedTime:Date;
        private modifier:string;
        private type:string;
        private fromContent:string;
        private toContent:string;
        private properties:any;

        constructor(json:api_relationship_json.RelationshipJson) {
            this.createdTime = new Date(json.createdTime);
            this.modifiedTime = new Date(json.modifiedTime);
            this.type = json.type;
            this.fromContent = json.fromContent;
            this.toContent = json.toContent;
            this.properties = json.properties;
            this.creator = json.creator;
            this.modifier = json.modifier;
            this.id = json.id;
        }

        getType():string {
            return this.type;
        }

        getCreatedTime():Date {
            return this.createdTime;
        }

        getModifiedTime():Date {
            return this.modifiedTime;
        }

        getCreator():string {
            return this.creator;
        }

        getModifier():string {
            return this.modifier;
        }

        getFromContent():string {
            return this.fromContent;
        }

        getToContent():string {
            return this.toContent;
        }

        getProperties():any {
            return this.properties;
        }

        getId():string {
            return this.id;
        }
    }
}