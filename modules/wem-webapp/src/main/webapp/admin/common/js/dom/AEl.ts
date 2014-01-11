module api.dom {

    export class AEl extends Element {

        constructor(generateId?: boolean, className?: string) {
            super(new ElementProperties().setTagName("a").setGenerateId(generateId).setClassName(className));
        }

        public setText(value: string) {
            this.getEl().setInnerHtml(value);
        }

        public setUrl(value: string) {
            this.getEl().setAttribute('href', value);
        }

        setClickListener(f: (event) => void) {
            this.getEl().addEventListener("click", f);
        }
    }
}
