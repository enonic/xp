module api.dom {

    export class Body extends Element {

        private static instance: Body;

        private childrenLoaded: boolean;

        constructor(loadExistingChildren: boolean = false, body?: HTMLElement) {
            if (!body) {
                body = document.body;
            }
            var html = Element.fromHtmlElement(body.parentElement);

            super(new ElementFromHelperBuilder().
                setHelper(new ElementHelper(body)).
                setLoadExistingChildren(loadExistingChildren).
                setParentElement(html));

            html.appendChild(this);

            this.init();
            this.childrenLoaded = loadExistingChildren;
        }

        static get(): Body {
            if (!Body.instance) {
                Body.instance = new Body();
            }
            return Body.instance;
        }

        isChildrenLoaded(): boolean {
            return this.childrenLoaded;
        }

        loadExistingChildren(): Body {
            if (!this.isChildrenLoaded()) {
                super.loadExistingChildren();
                this.childrenLoaded = true;
            }
            return this;
        }
    }
}