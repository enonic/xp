module app.home {

    export class ProductHeader extends api.dom.H1El {

        constructor() {
            super();
            this.setHtml("Enonic <span>DXP</span> 5 ee");
        }

    }

}
