module api.dom {

    export class Body extends Element {

        private static instance: Body;

        constructor(loadExistingChildren: boolean = false, body?: HTMLElement) {
            if (!body) {
                body = document.body;
            }
            super(new ElementFromHelperBuilder().
                setHelper(new ElementHelper(body)).
                setLoadExistingChildren(loadExistingChildren).
                setParentElement(Element.fromHtmlElement(body.parentElement)));

            this.init();
        }

        static get(): Body {
            if (!Body.instance) {
                Body.instance = new Body();
            }
            return Body.instance;
        }

        static getAndLoadExistingChildren(): Body {
            if (!Body.instance) {
                Body.instance = new Body(true);
            }
            return Body.instance;
        }
    }
}