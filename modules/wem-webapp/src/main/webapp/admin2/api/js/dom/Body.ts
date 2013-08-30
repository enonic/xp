module api_dom {

    export class Body extends Element {

        private static instance:Body = new Body();

        static get():Body {
            return Body.instance;
        }

        constructor() {
            super(null, null, null, new ElementHelper(document.body));
        }

    }

}