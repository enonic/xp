module api.form.inputtype {

    export interface InputTypeViewContext {

        formContext: api.form.FormContext;

        input: api.form.Input;

        inputConfig: { [element: string]: { [name: string]: string }[]; };

        parentDataPath: api.data.PropertyPath;
    }
}