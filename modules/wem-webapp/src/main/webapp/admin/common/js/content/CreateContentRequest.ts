module api_content {

    export class CreateContentRequest extends ContentResourceRequest<any> {

        private temporary:boolean = false;

        private contentName:string;

        private parentContentPath:string;

        private qualifiedContentTypeName:string;

        private form:api_form.Form;

        private contentData:ContentData;

        private displayName:string;

        private attachments:api_content.Attachment[] = [];

        constructor() {
            super();
            super.setMethod("POST");
        }

        setTemporary(temporary:boolean):CreateContentRequest {
            this.temporary = temporary;
            return this;
        }

        setContentName(contentName:string):CreateContentRequest {
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

        setForm(form:api_form.Form):CreateContentRequest {
            this.form = form;
            return this;
        }

        setContentData(contentData:api_content.ContentData):CreateContentRequest {
            this.contentData = contentData;
            return this;
        }

        setDisplayName(displayName:string):CreateContentRequest {
            this.displayName = displayName;
            return this;
        }

        addAttachment(attachment:api_content.Attachment):CreateContentRequest {
            this.attachments.push( attachment );
            return this;
        }

        addAttachments(attachments:api_content.Attachment[]):CreateContentRequest {
            this.attachments = this.attachments.concat(attachments);
            return this;
        }


        getParams():Object {
            return {
                temporary: this.temporary,
                contentName: this.contentName,
                parentContentPath: this.parentContentPath,
                qualifiedContentTypeName: this.qualifiedContentTypeName,
                form: this.form.toJson(),
                contentData: this.contentData.toJson(),
                displayName: this.displayName,
                attachments: this.attachmentsToJson()
            };
        }

        private attachmentsToJson():any {
            var array:any[] = [];
            this.attachments.forEach((attachment:api_content.Attachment)=>{
                var obj = {
                    "uploadId" : attachment.getUploadId(),
                    "attachmentName" : attachment.getAttachmentName().toString()
                };
                array.push(obj);
            });
            return array;
        }

        getRequestPath():api_rest.Path {
            return api_rest.Path.fromParent(super.getResourcePath(), "create");
        }

    }
}