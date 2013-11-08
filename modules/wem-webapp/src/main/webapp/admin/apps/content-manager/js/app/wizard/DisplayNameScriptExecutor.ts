module app_wizard {

    export class DisplayNameScriptExecutor {

        private static DISPLAY_NAME_REGEX:RegExp = /\$\('([a-zA-Z\.]*)'\)/g;

        private formView:api_form.FormView;

        private script:string;

        setFormView(value:api_form.FormView):DisplayNameScriptExecutor {
            this.formView = value;
            return this;
        }

        setScript(value:string):DisplayNameScriptExecutor {
            this.script = value;
            return this;
        }

        execute():string {

            var scriptToEvaluate = this.script;
            var result;
            while ((result = result = DisplayNameScriptExecutor.DISPLAY_NAME_REGEX.exec(this.script)) != null) {
                //console.log("handling result[1]: " + result[1]);
                //console.log("handling result[0]: " + result[0]);

                var path = api_data.DataPath.fromString(result[1]);

                var value:api_data.Value = this.formView.getValueAtPath(path);
                if( value == null ) {
                    scriptToEvaluate = scriptToEvaluate.replace(result[0], "");
                }
                else {
                    var stringValue = value.asString();
                    // Strips single quotes to avoid breaking
                    stringValue = stringValue.replace(/'/g, "\\'");
                    console.log( path.toString() + ": " + stringValue);

                    scriptToEvaluate = scriptToEvaluate.replace(result[0], "'" + stringValue + "'");
                }
            }

            console.log("to be evaluated: " + scriptToEvaluate);

            return eval(scriptToEvaluate);
        }
    }
}