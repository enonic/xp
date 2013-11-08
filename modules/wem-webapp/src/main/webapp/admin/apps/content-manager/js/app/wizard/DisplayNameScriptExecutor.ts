module app_wizard {

    export class DisplayNameScriptExecutor {

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
            return this.safeEval(this.script, this.formView);
        }

        private safeEval( script:string, formView:api_form.FormView):string {
            function $ ( path ) {
                var value = formView.getValueAtPath(api_data.DataPath.fromString(path));
                return value != null ? value.asString() : '';
            }

            var result = '';

            try {
                // hide eval, Function, document, window and other things from the script.
                result = eval( 'var eval; var Function; var document; var location; var Ext; ' +
                                   'var window; var parent; var self; var top; ' +
                                   script ) ;
            } catch (e) {
                console.error('cannot evaluate [' + script + '] function.');
            }

            return result;
        }
    }
}
