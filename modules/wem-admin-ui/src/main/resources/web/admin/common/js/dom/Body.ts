module api.dom {

    export class Body extends Element {

        private static instance: Body = new Body();

        static get(): Body {
            return Body.instance;
        }

        static getAndLoadExistingChildren(): Body {
            return new Body(true);
        }

        constructor(loadExistingChildren: boolean = false) {
            super(new ElementProperties().
                setHelper(new ElementHelper(document.body)).
                setLoadExistingChildren(loadExistingChildren));

            this.init();
        }

    }

}