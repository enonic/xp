module app_view {

    export class HomeCenterPanel extends api_dom.DivEl {

        private leftColumn:api_dom.DivEl;
        private rightColumn:api_dom.DivEl;

        constructor() {
            super(null, 'admin-home-center');

            this.leftColumn = new api_dom.DivEl(null, 'admin-home-left-column');
            this.rightColumn = new api_dom.DivEl(null, 'admin-home-right-column');
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
