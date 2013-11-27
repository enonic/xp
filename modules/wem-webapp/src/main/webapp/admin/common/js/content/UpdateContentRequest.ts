module api_content {

    export class UpdateContentRequest extends ContentResourceRequest<any> {

        private id:string;

        private name:string;

        private contentType:api_schema_content.ContentTypeName;

        private form:api_form.Form;

        private contentData:ContentData;

        private displayName:string;

        private attachments:api_content.Attachment[] = [];

        constructor(id:string) {
            super();
            this.id = id;
            this.setMethod("POST");
        }

        setId(id:string):UpdateContentRequest {
            this.id = id;
            return this;
        }

        setContentName(value:string):UpdateContentRequest {
            this.name = value;
            return this;
        }

        setContentType(value:api_schema_content.ContentTypeName):UpdateContentRequest {
            this.contentType = value;
            return this;
        }

        setForm(form:api_form.Form):UpdateContentRequest {
            this.form = form;
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


        addAttachment(attachment:api_content.Attachment):UpdateContentRequest {
            this.attachments.push( attachment );
            return this;
        }

        addAttachments(attachments:api_content.Attachment[]):UpdateContentRequest {
            this.attachments = this.attachments.concat(attachments);
            return this;
        }

        getParams():Object {
            return {
                contentId: this.id,
                contentName: this.name,
                contentType: this.contentType.toString(),
                form: this.form.toJson(),
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