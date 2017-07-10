module api.content.form.inputtype.image {

    import Button = api.ui.button.Button;
    import i18n = api.util.i18n;

    export class SelectionToolbar extends api.dom.DivEl {

        private editButton: Button;

        private removeButton: Button;

        private removableCount: number;

        private editableCount: number;

        private editClickListeners: {(): void;}[] = [];

        private removeClickListeners: {(): void;}[] = [];

        constructor() {
            super('selection-toolbar');

            this.editButton = new Button(i18n('button.edit'));
            this.editButton.addClass('large edit');
            this.editButton.onClicked((event: MouseEvent) => {
                this.notifyEditClicked();
            });
            this.appendChild(this.editButton);

            this.removeButton = new Button(i18n('button.remove'));
            this.removeButton.addClass('large red');
            this.removeButton.onClicked((event: MouseEvent) => {
                this.notifyRemoveClicked();
            });
            this.appendChild(this.removeButton);
        }

        setSelectionCount(removableCount: number, editableCount: number) {
            this.editableCount = editableCount;
            this.removableCount = removableCount;
            this.refreshUI();
        }

        private refreshUI() {
            this.editButton.setLabel(i18n('button.edit') + ' ' + (this.editableCount > 1 ? ' (' + this.editableCount + ')' : ''));
            this.editButton.setEnabled(this.editableCount > 0);
            this.removeButton.setLabel(i18n('button.edit') + ' ' + (this.removableCount > 1 ? ' (' + this.removableCount + ')' : ''));
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
            this.editClickListeners = this.editClickListeners
                .filter(function (curr: {(): void;}) {
                    return curr !== listener;
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
            this.removeClickListeners = this.removeClickListeners
                .filter(function (curr: {(): void;}) {
                    return curr !== listener;
                });
        }

    }

}
