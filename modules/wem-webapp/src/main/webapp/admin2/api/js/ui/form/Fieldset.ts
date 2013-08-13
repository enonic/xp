module api_ui_form {
    export class Fieldset extends api_dom.FieldsetEl {

        private legend:api_dom.LegendEl;

        private form:Form;

        constructor(form:Form, legend?:string) {
            super();
            if (legend) {
                this.legend = new api_dom.LegendEl(legend);
                this.appendChild(this.legend);
            }
            this.form = form;
        }

        add(formItem:FormItem) {
            formItem.appendTo(this);
            this.form.addInput(formItem.getItem());
        }
    }
}