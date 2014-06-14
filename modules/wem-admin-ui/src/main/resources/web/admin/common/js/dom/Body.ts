module api.dom {

    export class Body extends Element {

        private static instance: Body;

        private withChildren: boolean = false;

        static get(): Body {
            if (!Body.instance) {
                Body.instance = new Body();
            }
            return Body.instance;
        }

        static getAndLoadExistingChildren(): Body {
            if (!Body.instance || !Body.instance.withChildren) {
                Body.instance = new Body(true);
            }
            return Body.instance;
        }

        constructor(loadExistingChildren: boolean = false) {
            super(new ElementFromHelperBuilder().
                setHelper(new ElementHelper(document.body)).
                setLoadExistingChildren(loadExistingChildren).
                setParentElement(Element.fromHtmlElement(document.body.parentElement)));

            this.init();
        }
    }
}