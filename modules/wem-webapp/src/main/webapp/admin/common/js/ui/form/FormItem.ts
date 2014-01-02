module api.ui.form {
    export class FormItem extends api.dom.DivEl {
        private label:api.dom.LabelEl;
        private item:api.dom.FormInputEl;

        constructor(label:string, item:api.dom.FormInputEl) {
            super(null, "form-item");
            this.label = new api.dom.LabelEl(label, item);
            this.item = item;
            this.appendChild(this.label);
            this.appendChild(item);
        }

        getLabel():api.dom.LabelEl {
            return this.label;
        }

        getItem():api.dom.FormInputEl {
            return this.item;
        }

        appendTo(el:api.dom.Element) {
            el.appendChild(this);
            //el.appendChild(this.label);
            //el.appendChild(this.item);
        }
    }
}