module app_wizard {

    export class DisplayNameScriptExecutor implements api_app_wizard.DisplayNameGenerator {

        private formView: api_form.FormView;

        private script: string;

        setFormView(value: api_form.FormView): DisplayNameScriptExecutor {
            this.formView = value;
            return this;
        }

        setScript(value: string): DisplayNameScriptExecutor {
            this.script = value;
            return this;
        }

        hasScript(): boolean {
            return this.script != null;
        }

        execute(): string {
            return this.safeEval(this.script, this.formView);
        }

        private safeEval(script: string, formView: api_form.FormView): string {

            function $(...paths: string[]) {

                var strValues: string [] = [];
                paths.forEach((path: string) => {

                    var value = formView.getValueAtPath(api_data.DataPath.fromString(path));
                    if (value != null) {
                        var strValue = value.asString();
                        if (!api_util.isStringBlank(strValue)) {
                            strValues.push(strValue);
                        }
                    }
                });

                var strValue = "";
                strValues.forEach((s: string, index: number) => {
                    strValue += s;
                    if (index < strValues.length - 1) {
                        strValue += " ";
                    }
                });

                return strValue;
            }

            var result = '';

            try {
                // hide eval, Function, document, window and other things from the script.
                result = eval('var eval; var Function; var document; var location; var Ext; ' +
                              'var window; var parent; var self; var top; ' +
                              script);
            } catch (e) {
                console.error('cannot evaluate [' + script + '] function.');
            }

            return result;
        }
    }
}
