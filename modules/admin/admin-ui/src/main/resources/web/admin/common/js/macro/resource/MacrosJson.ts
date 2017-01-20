module api.macro.resource {

    export interface MacrosJson {
        macros: MacroJson[];
    }

    export interface MacroJson {
        key: string;
        name: string;
        displayName: string;
        description: string;
        form: api.form.json.FormJson;
        iconUrl: string;
    }
}
