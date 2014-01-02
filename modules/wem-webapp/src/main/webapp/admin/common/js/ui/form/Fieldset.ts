module api.ui.form {
    export class Fieldset extends api.dom.FieldsetEl {

        private legend:api.dom.LegendEl;

        private form:Form;

        constructor(form:Form, legend?:string) {
            super();
            if (legend) {
                this.legend = new api.dom.LegendEl(legend);
                this.appendChild(this.legend);
            }
            this.form = form;
        }

        add(formItem:FormItem) {
            formItem.appendTo(this);
            this.form.registerInput(formItem.getItem());
        }
    }
}