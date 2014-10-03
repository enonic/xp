module api.content.form.inputtype {

    export interface ContentInputTypeViewContext<INPUT_CONFIG> extends api.form.inputtype.InputTypeViewContext<INPUT_CONFIG> {

        site: api.content.site.Site;

        contentId: api.content.ContentId;

        contentPath: api.content.ContentPath;

        parentContentPath: api.content.ContentPath;

        attachments:api.content.attachment.Attachments;
    }
}