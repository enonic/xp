module api.ui.treegrid.actions {

    import i18n = api.util.i18n;
    export class SelectionPanelToggler extends api.ui.button.TogglerButton {

        private tooltip: Tooltip;

        constructor(treeGrid: TreeGrid<any>) {
            super('selection-toggler');

            this.setEnabled(true);

            this.tooltip = new Tooltip(this, '', 1000);

            treeGrid.onSelectionChanged((currentSelection: TreeNode<any>[], fullSelection: TreeNode<any>[]) => {

                let oldLabel = this.getLabel();
                let newLabel = fullSelection.length ? fullSelection.length.toString() : '';

                if (oldLabel == newLabel) {
                    return;
                }
                this.tooltip.setText(this.isActive() ? i18n('field.selection.hide') : i18n('field.selection.show'));

                this.removeClass('any-selected');
                this.removeClass(`size-${oldLabel.length}`);
                this.setLabel(newLabel);
                if (newLabel !== '') {
                    this.addClass(`size-${newLabel.length}`);
                    this.addClass('updated');
                    const itemCount = fullSelection.length;
                    if (itemCount >= 1) {
                        this.addClass('any-selected');
                        this.getEl().setAttribute('data-label', i18n(`field.item.${itemCount === 1 ? 'single' : 'multiple'}`));
                    }
                    setTimeout(() => {
                        this.removeClass('updated');
                    }, 200);
                }

            });

            this.onActiveChanged((isActive: boolean) => {
                let isVisible = this.tooltip.isVisible();
                if (isVisible) {
                    this.tooltip.hide();
                }
                this.tooltip.setText(isActive ? 'Hide selection' : 'Show selection');
                if (isVisible) {
                    this.tooltip.show();
                }
            });
        }
    }
}
