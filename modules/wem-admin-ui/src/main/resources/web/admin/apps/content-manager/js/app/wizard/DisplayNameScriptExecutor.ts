module app.wizard {

    import PropertyPath = api.data2.PropertyPath;
    import Property = api.data2.Property;
    import Value = api.data2.Value;
    import ValueType = api.data2.ValueType;
    import ValueTypes = api.data2.ValueTypes;
    import PropertyTree = api.data2.PropertyTree;

    export class DisplayNameScriptExecutor implements api.app.wizard.DisplayNameGenerator {

        private formView: api.form.FormView;

        private script: string;

        setFormView(value: api.form.FormView): DisplayNameScriptExecutor {
            this.formView = value;
            return this;
        }

        setScript(value: string): DisplayNameScriptExecutor {
            this.script = value;
            return this;
        }

        hasScript(): boolean {
            return !api.util.StringHelper.isBlank(this.script);
        }

        execute(): string {
            api.util.assertNotNull(this.formView, "formView not set");
            api.util.assertNotNull(this.script, "script not set");

            return this.safeEval(this.script, this.formView);
        }

        private safeEval(script: string, formView: api.form.FormView): string {

            function $(...paths: string[]) {

                var strValues: string [] = [];
                paths.forEach((path: string) => {

                    var strValue = formView.getData().getString(path);
                    if (!api.util.StringHelper.isBlank(strValue)) {
                        strValues.push(strValue);
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
                result = eval('var eval; var Function; var document; var location; ' +
                              'var window; var parent; var self; var top; ' +
                              script);
            } catch (e) {
                console.error('Cannot evaluate script [' + script + '].', e);
            }

            return result;
        }
    }
}
