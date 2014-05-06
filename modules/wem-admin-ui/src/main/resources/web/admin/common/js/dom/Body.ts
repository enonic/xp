module api.dom {

    export class Body extends Element {

        private static instance:Body = new Body();

        static get():Body {
            return Body.instance;
        }

        constructor() {
            super(new ElementProperties().setHelper(new ElementHelper(document.body)));

            this.init();
        }

    }

}