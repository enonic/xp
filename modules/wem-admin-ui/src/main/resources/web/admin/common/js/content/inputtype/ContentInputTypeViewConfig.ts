module api.content.inputtype {

    export interface ContentInputTypeViewConfig<INPUT_CONFIG> extends api.form.inputtype.InputTypeViewConfig<INPUT_CONFIG> {

        contentId: api.content.ContentId;

        contentPath: api.content.ContentPath;

        parentContentPath: api.content.ContentPath;

        attachments:api.content.attachment.Attachments;
    }
}