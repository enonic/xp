module api.content.form.inputtype.image {

    import LoadMask = api.ui.mask.LoadMask;

    export class SelectedOptionView extends api.ui.selector.combobox.SelectedOptionView<ImageSelectorDisplayValue> {

        private static IMAGE_SIZE: number = 270;

        private icon: api.dom.ImgEl;

        private label: api.dom.DivEl;

        private check: api.ui.Checkbox;

        private progress: api.ui.ProgressBar;

        private loadMask: LoadMask;

        private selectionChangeListeners: {(option: SelectedOptionView, checked: boolean): void;}[] = [];

        constructor(option: api.ui.selector.Option<ImageSelectorDisplayValue>) {
            super(option);
        }

        setOption(option: api.ui.selector.Option<ImageSelectorDisplayValue>) {
            super.setOption(option);

            var content: ImageSelectorDisplayValue = this.getOption().displayValue;

            if (content.getContentSummary()) {
                if (this.isVisible()) {
                    this.showSpinner();
                }
                this.icon.setSrc(content.getImageUrl() + "?thumbnail=false&size=" + SelectedOptionView.IMAGE_SIZE);
                this.label.getEl().setInnerHtml(content.getLabel());
            } else {
                this.showProgress();
            }
        }

        setProgress(value: number) {
            this.progress.setValue(value);
            if (value == 100) {
                this.showSpinner();
            }
        }

        layout() {
            this.icon = new api.dom.ImgEl();
            this.appendChild(this.icon);

            this.label = new api.dom.DivEl("label");
            this.appendChild(this.label);

            this.check = new api.ui.Checkbox();
            this.appendChild(this.check);

            this.progress = new api.ui.ProgressBar();
            this.appendChild(this.progress);

            this.loadMask = new LoadMask(this);
            this.appendChild(this.loadMask);

            this.check.onClicked((event: MouseEvent) => {
                this.check.toggleChecked();
                event.preventDefault();
                // swallow event to prevent scaling when clicked on checkbox
                event.stopPropagation();
            });

            this.check.onMouseDown((event: MouseEvent) => {
                // swallow event and prevent checkbox focus on click
                event.stopPropagation();
                event.preventDefault();
            });

            this.check.onValueChanged((event: api.ui.ValueChangedEvent) => {
                this.notifyChecked(event.getNewValue() == 'true');
            });

            this.onShown(() => {
                if (this.getOption().displayValue.getContentSummary()) {
                    if (this.icon.isLoaded()) {
                        // refresh image in case it was changed by external wizard
                        this.icon.refresh();
                    }
                    this.showSpinner();
                }
            });
            this.icon.onLoaded((event: UIEvent) => {
                if (this.getOption().displayValue.getContentSummary()) {
                    this.removeSpinner();
                }
            });
        }

        private showProgress() {
            this.check.hide();
            this.icon.getEl().setVisibility('hidden');
            this.loadMask.hide();
            this.progress.show();
        }

        private showSpinner() {
            this.progress.hide();
            this.check.hide();
            this.icon.getEl().setVisibility('hidden');
            this.loadMask.show();
        }

        private removeSpinner() {
            this.loadMask.hide();
            this.icon.getEl().setVisibility('visible');
            this.check.show();
        }

        updateProportions(optionHeight: number) {
            this.getEl().setHeightPx(optionHeight);
            var iconHeight = this.icon.getEl().getHeightWithBorder();
            var contentHeight = optionHeight - this.getEl().getBorderTopWidth() - this.getEl().getBorderBottomWidth();
            if (iconHeight <= contentHeight && iconHeight !== 0) {
                this.icon.getEl().setMarginTop((contentHeight - iconHeight) / 2 + 'px');
            }
            this.progress.getEl().setMarginTop((contentHeight - this.progress.getEl().getHeight()) / 2 + 'px');
        }

        getIcon(): api.dom.ImgEl {
            return this.icon;
        }

        getCheckbox(): api.ui.Checkbox {
            return this.check;
        }

        toggleChecked() {
            this.check.toggleChecked();
        }

        private notifyChecked(checked: boolean) {
            this.selectionChangeListeners.forEach((listener) => {
                listener(this, checked);
            });
        }

        onChecked(listener: {(option: SelectedOptionView, checked: boolean): void;}) {
            this.selectionChangeListeners.push(listener);
        }

        unChecked(listener: {(option: SelectedOptionView, checked: boolean): void;}) {
            this.selectionChangeListeners = this.selectionChangeListeners.filter(function (curr) {
                return curr != listener;
            });
        }

    }
}