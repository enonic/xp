module api_ui {
    export class Fieldset extends api_dom.FieldsetEl {

        private legend:api_dom.LegendEl;

        private form:api_ui.Form;

        constructor(legend?:string) {
            super();
            if (legend) {
                this.legend = new api_dom.LegendEl(legend);
                this.appendChild(this.legend);
            }
        }

        add(formItem:api_ui.FormItem) {
            formItem.appendTo(this);
            this.form.addInput(formItem.getItem());
        }

        setForm(form:Form) {
            this.form = form;
        }
    }
}