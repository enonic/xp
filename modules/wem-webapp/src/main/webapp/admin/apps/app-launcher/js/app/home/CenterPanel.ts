module app_home {

    export class CenterPanel extends api_dom.DivEl {

        private leftColumn:api_dom.DivEl;
        private rightColumn:api_dom.DivEl;

        constructor() {
            super(null, 'center-panel');

            this.leftColumn = new api_dom.DivEl(null, 'left-column');
            this.rightColumn = new api_dom.DivEl(null, 'right-column');
            this.appendChild(this.leftColumn);
            this.appendChild(this.rightColumn);
        }

        appendLeftColumn(component:api_dom.Element) {
            this.leftColumn.appendChild(component);
        }

        appendRightColumn(component:api_dom.Element) {
            this.rightColumn.appendChild(component);
        }
    }

}
