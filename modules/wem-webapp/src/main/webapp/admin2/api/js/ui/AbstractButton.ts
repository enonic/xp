module api_ui{

    export class AbstractButton extends api_ui.ButtonEl {

        private label:string;

        constructor(name:string, label:string) {
            super(name);
            this.label = label;

            this.getEl().setInnerHtml(this.label);
        }

        setEnable(value:bool) {
            this.getEl().setDisabled(!value);
        }

    }
}
