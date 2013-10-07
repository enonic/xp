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

        setTemporary(temporary:boolean):CreateContentRequest {
            this.temporary = temporary;
            return this;
        }

        setContentName(contentName:string):CreateContentRequest  {
            this.contentName = contentName;
            return this;
        }

        setParentContentPath(parentContentPath:string):CreateContentRequest {
            this.parentContentPath = parentContentPath;
            return this;
        }

        setContentType(qualifiedContentTypeName:string):CreateContentRequest {
            this.qualifiedContentTypeName = qualifiedContentTypeName;
            return this;
        }

        setContentData(contentData:{ [key:string]:string }):CreateContentRequest {
            this.contentData = contentData;
            return this;
        }

        setDisplayName(displayName:string):CreateContentRequest {
            this.displayName = displayName;
            return this;
        }


        setAttachments(attachments:{uploadId: string;attachmentName: string;}[]):CreateContentRequest {
            this.attachments = attachments;
            return this;
        }


        getParams():Object {
            return {
                temporary: this.temporary,
                contentName: this.contentName,
                parentContentPath: this.parentContentPath,
                qualifiedContentTypeName: this.qualifiedContentTypeName,
                contentData: this.contentData,
                displayName: this.displayName,
                attachments: this.attachments
            };
        }

        getRequestPath():api_rest.Path {
            return api_rest.Path.fromParent(super.getResourcePath(), "create");
        }

    }
}