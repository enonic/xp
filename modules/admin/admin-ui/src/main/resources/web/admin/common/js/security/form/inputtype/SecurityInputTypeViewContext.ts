module api.security.form.inputtype {

    export interface SecurityInputTypeViewContext
        extends api.form.inputtype.InputTypeViewContext {

        formContext: api.security.form.SecurityFormContext;

        contentPath: api.content.ContentPath;
    }
}
