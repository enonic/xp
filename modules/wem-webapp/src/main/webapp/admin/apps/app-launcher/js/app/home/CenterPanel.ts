module app.home {

    export class CenterPanel extends api.dom.DivEl {

        private leftColumn:api.dom.DivEl;
        private rightColumn:api.dom.DivEl;

        constructor() {
            super(null, 'center-panel');

            this.leftColumn = new api.dom.DivEl(null, 'left-column');
            this.rightColumn = new api.dom.DivEl(null, 'right-column');
            this.appendChild(this.leftColumn);
            this.appendChild(this.rightColumn);
        }

        appendLeftColumn(component:api.dom.Element) {
            this.leftColumn.appendChild(component);
        }

        appendRightColumn(component:api.dom.Element) {
            this.rightColumn.appendChild(component);
        }
    }

}
