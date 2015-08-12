module api.form.inputtype {

    export interface InputTypeViewContext {

        formContext: api.form.FormContext;

        input: api.form.Input;

        inputConfig: { [name: string]: string; };

        parentDataPath: api.data.PropertyPath;
    }
}