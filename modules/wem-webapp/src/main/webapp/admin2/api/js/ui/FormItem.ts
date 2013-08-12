module api_ui {
    export class FormItem extends api_dom.DivEl {
        private label:api_dom.LabelEl;
        private item:api_dom.FormInputEl;

        constructor(label:string, item:api_dom.FormInputEl) {
            super(null, "form-item");
            this.label = new api_dom.LabelEl(label, item);
            this.item = item;
            this.appendChild(this.label);
            this.appendChild(item);
        }

        getLabel():api_dom.LabelEl {
            return this.label;
        }

        getItem():api_ui.FormInput {
            return this.item;
        }

        appendTo(el:api_dom.Element) {
            el.appendChild(this);
            //el.appendChild(this.label);
            //el.appendChild(this.item);
        }
    }
}