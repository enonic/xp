module api.dom {

    export class LinkEl extends api.dom.Element {

        constructor(href: string, rel: string = "import", className?: string) {
            super(new NewElementBuilder().
                setTagName("link").
                setClassName(className));

            this.setHref(href).setRel(rel);
        }

        private setHref(href: string): api.dom.LinkEl {
            this.getEl().setAttribute("href", href);
            return this;
        }

        private setRel(rel: string): api.dom.LinkEl {
            this.getEl().setAttribute("rel", rel);
            return this;
        }
    }
}
