module api_content {

    export class CreateContentRequest extends ContentResourceRequest {

        private temporary:boolean = false;
        private contentName:string;
        private parentContentPath:string;
        private qualifiedContentTypeName:string;
        private contentData:{
            [key:string]: string;
        };
        private displayName:string;
        private attachments:{
            uploadId: string;
            attachmentName: string;
        }[];

        constructor() {
            super();
            super.setMethod("POST");
        }

        isTemporary():boolean {
            return this.temporary;
        }

        setTemporary(temporary:boolean) {
            this.temporary = temporary;
            return this.updateParams();
        }

        getContentName():string {
            return this.contentName;
        }

        setContentName(contentName:string) {
            this.contentName = contentName;
            return this.updateParams();
        }

        getParentContentPath():string {
            return this.parentContentPath;
        }

        setParentContentPath(parentContentPath:string) {
            this.parentContentPath = parentContentPath;
            return this.updateParams();
        }

        getQualifiedContentTypeName():string {
            return this.qualifiedContentTypeName;
        }

        setQualifiedContentTypeName(qualifiedContentTypeName:string) {
            this.qualifiedContentTypeName = qualifiedContentTypeName;
            return this.updateParams();
        }

        getContentData():{ [key:string]:string } {
            return this.contentData;
        }

        setContentData(contentData:{ [key:string]:string }) {
            this.contentData = contentData;
            return this.updateParams();
        }

        getDisplayName():string {
            return this.displayName;
        }

        setDisplayName(displayName:string) {
            this.displayName = displayName;
            return this.updateParams();
        }

        getAttachments():{uploadId: string;attachmentName: string;}[] {
            return this.attachments;
        }

        setAttachments(attachments:{uploadId: string;attachmentName: string;}[]) {
            this.attachments = attachments;
            return this.updateParams();
        }

        getUrl() {
            return super.getResourceUrl() + "/create";
        }

        updateParams() {
            var params = {
                temporary: this.temporary,
                contentName: this.contentName,
                parentContentPath: this.parentContentPath,
                qualifiedContentTypeName: this.qualifiedContentTypeName,
                contentData: this.contentData,
                displayName: this.displayName,
                attachments: this.attachments
            };
            super.setParams(params);
            return this;
        }
    }
}