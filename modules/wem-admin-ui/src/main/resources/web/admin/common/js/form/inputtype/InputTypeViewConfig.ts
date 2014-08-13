module api.form.inputtype {

    export interface InputTypeViewConfig<INPUT_CONFIG> {

        input: api.form.Input;

        inputConfig:INPUT_CONFIG;

        parentDataPath: api.data.DataPath;
    }
}