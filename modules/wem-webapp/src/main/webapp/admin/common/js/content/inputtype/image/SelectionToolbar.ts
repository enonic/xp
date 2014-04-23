module api.content.inputtype.image {

    export class SelectionToolbar extends api.dom.DivEl {

        private editButton: api.ui.Button;

        private removeButton: api.ui.Button;

        private selectionCount: number;

        private editClickListeners: {(): void;}[] = [];

        private removeClickListeners: {(): void;}[] = [];

        constructor() {
            super("selection-toolbar");

            this.editButton = new api.ui.Button("Edit");
            this.editButton.addClass("large");
            this.editButton.onClicked((event: MouseEvent) => {
                this.notifyEditClicked();
            });
            this.appendChild(this.editButton);

            this.removeButton = new api.ui.Button("Remove");
            this.removeButton.addClass("large red");
            this.removeButton.onClicked((event: MouseEvent) => {
                this.notifyRemoveClicked();
            });
            this.appendChild(this.removeButton);
        }

        setSelectionCount(count: number) {
            this.selectionCount = count;
            this.refreshUI();
        }

        private refreshUI() {
            this.editButton.setLabel("Edit" + (this.selectionCount > 0 ? " (" + this.selectionCount + ")" : ""));
            this.removeButton.setLabel("Remove " + (this.selectionCount > 0 ? " (" + this.selectionCount + ")" : ""));
        }

        notifyEditClicked() {
            this.editClickListeners.forEach((listener) => {
                listener();
            });
        }

        addEditClickListener(listener: {(): void;}) {
            this.editClickListeners.push(listener);
        }

        removeEditClickListener(listener: {(): void;}) {
            this.editClickListeners = this.editClickListeners.filter(function (curr) {
                return curr != listener;
            });
        }

        notifyRemoveClicked() {
            this.removeClickListeners.forEach((listener) => {
                listener();
            });
        }

        addRemoveClickListener(listener: {(): void;}) {
            this.removeClickListeners.push(listener);
        }

        removeRemoveClickListener(listener: {(): void;}) {
            this.removeClickListeners = this.removeClickListeners.filter(function (curr) {
                return curr != listener;
            });
        }

    }

}