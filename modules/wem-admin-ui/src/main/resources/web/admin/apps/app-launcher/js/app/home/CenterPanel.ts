module app.home {

    export class CenterPanel extends api.dom.DivEl {

        private loginPanel:api.dom.DivEl;
        private appSelectorPanel:api.dom.DivEl;

        constructor() {
            super('center-panel');

            this.loginPanel = new api.dom.DivEl('left-column');
            this.appSelectorPanel = new api.dom.DivEl('right-column');
            //this.appendChild(this.leftColumn);
            //this.appendChild(this.rightColumn);
        }
    }

}
