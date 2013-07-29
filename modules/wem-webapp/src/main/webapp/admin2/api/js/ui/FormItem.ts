module api_ui {
    export class FormItem extends api_dom.DivEl {
        private label:api_dom.LabelEl;
        private item:api_dom.Element;

        constructor(label:string, item:api_dom.Element) {
            super(null, "form-item");
            this.item = item;
            this.label = new api_dom.LabelEl(label, item);
            this.appendChild(this.label);
            this.appendChild(item);
        }

        getLabel():api_dom.LabelEl {
            return this.label;
        }

        getItem():api_dom.Element {
            return this.item;
        }

        appendTo(el:api_dom.Element) {
            el.appendChild(this);
            //el.appendChild(this.label);
            //el.appendChild(this.item);
        }
    }
}