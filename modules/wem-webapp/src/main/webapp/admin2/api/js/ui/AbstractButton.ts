module api_ui{

    export class AbstractButton extends api_dom.ButtonEl {

        private label:string;

        constructor(idPrefix:string, label:string) {
            super(idPrefix);
            this.label = label;

            this.getEl().setInnerHtml(this.label);
        }

        setEnable(value:bool) {
            this.getEl().setDisabled(!value);
        }

    }
}
