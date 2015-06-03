module api.ui.selector {

    export class DropdownExpandedEvent {

        private expanded: boolean;
        private dropDownElement: api.dom.Element;

        constructor(dropDownElement: api.dom.Element, expanded: boolean) {
            this.dropDownElement = dropDownElement;
            this.expanded = expanded;
        }

        isExpanded(): boolean {
            return this.expanded;
        }

        getDropdownElement(): api.dom.Element {
            return this.dropDownElement;
        }
    }
}
