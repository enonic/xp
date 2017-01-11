module api.dom {

    export class Body extends Element {

        private static instance: Body;

        private childrenLoaded: boolean;

        constructor(loadExistingChildren: boolean = false, body?: HTMLElement) {
            if (!body) {
                body = document.body;
            }
            let html = Element.fromHtmlElement(body.parentElement);

            super(new ElementFromHelperBuilder().
                setHelper(new ElementHelper(body)).
                setLoadExistingChildren(loadExistingChildren));

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

        isShowingModalDialog() {
            return Body.get().hasClass("modal-dialog");
        }
    }
}
