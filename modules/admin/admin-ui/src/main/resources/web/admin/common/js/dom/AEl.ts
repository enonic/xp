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

        public setTitle(value: string): AEl {
            this.getEl().setTitle(value);
            return this;
        }

        public getTitle(): string {
            return this.getEl().getTitle();
        }

        public getHref(): string {
            return this.getEl().getAttribute('href');
        }

        public getTarget(): string {
            return this.getEl().getAttribute('target');
        }

        public getText(): string {
            return this.getEl().getText();
        }
    }
}
