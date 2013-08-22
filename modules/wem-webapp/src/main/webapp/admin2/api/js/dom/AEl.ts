module api_dom {

    export class AEl extends Element {

        constructor(idPrefix?:string, className?:string) {
            super("a", idPrefix, className);
        }

        public setText(value: string) {
            this.getEl().setInnerHtml(value);
        }

        public setUrl(value: string) {
            this.getEl().setAttribute('href', value);
        }
    }
}
