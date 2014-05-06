module api.form.inputtype {

    export interface InputTypeViewConfig<C> {

        contentId: api.content.ContentId;

        contentPath: api.content.ContentPath;

        parentContentPath: api.content.ContentPath;

        inputConfig:C;

        attachments:api.content.attachment.Attachments;
    }
}