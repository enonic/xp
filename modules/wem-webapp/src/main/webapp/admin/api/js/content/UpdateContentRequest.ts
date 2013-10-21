module api_content {

    export class UpdateContentRequest extends ContentResourceRequest<any> {

        private id:string;

        private contentName:string;

        private qualifiedContentTypeName:string;

        private contentData:ContentData;

        private displayName:string;

        private attachments:{
            uploadId: string;
            attachmentName: string;
        }[];

        constructor(id:string) {
            super();
            this.id = id;
            this.setMethod("POST");
        }

        setId(id:string):UpdateContentRequest {
            this.id = id;
            return this;
        }

        setContentName(contentName:string):UpdateContentRequest {
            this.contentName = contentName;
            return this;
        }

        setContentType(qualifiedContentTypeName:string):UpdateContentRequest {
            this.qualifiedContentTypeName = qualifiedContentTypeName;
            return this;
        }

        setContentData(contentData:api_content.ContentData):UpdateContentRequest {
            this.contentData = contentData;
            return this;
        }

        setDisplayName(displayName:string):UpdateContentRequest {
            this.displayName = displayName;
            return this;
        }


        setAttachments(attachments:{uploadId: string;attachmentName: string;}[]):UpdateContentRequest {
            this.attachments = attachments;
            return this;
        }

        getParams():Object {
            return {
                contentId: this.id,
                contentName: this.contentName,
                qualifiedContentTypeName: this.qualifiedContentTypeName,
                contentData: this.contentData.toJson(),
                displayName: this.displayName,
                attachments: this.attachments
            };
        }

        getRequestPath():api_rest.Path {
            return api_rest.Path.fromParent(super.getResourcePath(), "update");
        }
    }
}