module api_ui {
    export class Fieldset extends api_dom.FieldsetEl {

        private legend:api_dom.LegendEl;

        private form:api_ui.Form;

        constructor(form:api_ui.Form, legend?:string) {
            super();
            if (legend) {
                this.legend = new api_dom.LegendEl(legend);
                this.appendChild(this.legend);
            }
            this.form = form;
        }

        add(formItem:api_ui.FormItem) {
            formItem.appendTo(this);
            this.form.addInput(formItem.getItem());
        }
    }
}