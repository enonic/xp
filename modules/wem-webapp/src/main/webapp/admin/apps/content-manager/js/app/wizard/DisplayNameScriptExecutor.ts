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

            var displayName = this.script;
            var result;
            while ((result = result = DisplayNameScriptExecutor.DISPLAY_NAME_REGEX.exec(this.script)) != null) {
                console.log("handling: " + result);

                var path = api_data.DataPath.fromString(result[1]);

                var inputView:api_form_input.InputView = this.formView.getInputViewByPath(path);

                console.log("inputView", inputView);

                var inputValue = inputView ? inputView.getValue(path.getLastElement().getIndex()).asString() : '';
                // Strips single quotes to avoid breaking
                inputValue = inputValue.replace(/'/g, "\\'");
                displayName = displayName.replace(result[0], "'" + inputValue + "'");
            }
            console.log("displayName: " + displayName);

            var evaluated = eval(displayName);
            return evaluated;
        }
    }
}