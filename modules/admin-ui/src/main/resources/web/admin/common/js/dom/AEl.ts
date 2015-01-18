module api.dom {

    export class AEl extends Element {

        constructor(className?: string) {
            super(new NewElementBuilder().setTagName("a").setClassName(className));

            this.setUrl('#');
        }

        public setUrl(value: string, target?: string): AEl {
            this.getEl().setAttribute('href', value);
            if (target) {
                this.getEl().setAttribute('target', target);
            }
            return this;
        }
    }
}
