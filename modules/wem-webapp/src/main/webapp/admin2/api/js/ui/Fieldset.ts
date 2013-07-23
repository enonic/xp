module api_ui {
    export class Fieldset extends api_dom.FieldsetEl {

        private legend:api_dom.LegendEl;

        constructor(legend?:string) {
            super();
            if (legend) {
                this.legend = new api_dom.LegendEl(legend);
                this.appendChild(this.legend);
            }
        }

        add(formItem:api_ui.FormItem) {
            formItem.appendTo(this);
        }

    }
}