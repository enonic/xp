module api.form.inputtype {

    export interface InputTypeViewContext<INPUT_CONFIG> {

        formContext: api.form.FormContext;

        input: api.form.Input;

        inputConfig:INPUT_CONFIG;

        parentDataPath: api.data.PropertyPath;
    }
}