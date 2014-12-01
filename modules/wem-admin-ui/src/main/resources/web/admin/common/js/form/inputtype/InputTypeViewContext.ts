module api.form.inputtype {

    export interface InputTypeViewContext<INPUT_CONFIG> {

        input: api.form.Input;

        inputConfig:INPUT_CONFIG;

        parentDataPath: api.data.PropertyPath;
    }
}