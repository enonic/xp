module api.content.form.inputtype.image {

    import Button = api.ui.button.Button;

    export class SelectionToolbar extends api.dom.DivEl {

        private editButton: Button;

        private removeButton: Button;

        private selectionCount: number;

        private editClickListeners: {(): void;}[] = [];

        private removeClickListeners: {(): void;}[] = [];

        constructor() {
            super("selection-toolbar");

            this.editButton = new Button("Edit");
            this.editButton.addClass("large");
            this.editButton.onClicked((event: MouseEvent) => {
                this.notifyEditClicked();
            });
            this.appendChild(this.editButton);

            this.removeButton = new Button("Remove");
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

        onEditClicked(listener: {(): void;}) {
            this.editClickListeners.push(listener);
        }

        unEditClicked(listener: {(): void;}) {
            this.editClickListeners = this.editClickListeners.filter(function (curr) {
                return curr != listener;
            });
        }

        notifyRemoveClicked() {
            this.removeClickListeners.forEach((listener) => {
                listener();
            });
        }

        onRemoveClicked(listener: {(): void;}) {
            this.removeClickListeners.push(listener);
        }

        unRemoveClicked(listener: {(): void;}) {
            this.removeClickListeners = this.removeClickListeners.filter(function (curr) {
                return curr != listener;
            });
        }

    }

}