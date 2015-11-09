module api.content.form.inputtype.image {

    import LoadMask = api.ui.mask.LoadMask;

    export class ImageSelectorSelectedOptionView extends api.ui.selector.combobox.BaseSelectedOptionView<ImageSelectorDisplayValue> {

        private static IMAGE_SIZE: number = 270;

        private icon: api.dom.ImgEl;

        private label: api.dom.DivEl;

        private check: api.ui.Checkbox;

        private progress: api.ui.ProgressBar;

        private error: api.dom.DivEl;

        private loadMask: LoadMask;

        private selectionChangeListeners: {(option: ImageSelectorSelectedOptionView, checked: boolean): void;}[] = [];

        constructor(option: api.ui.selector.Option<ImageSelectorDisplayValue>) {
            super(option);
        }

        setOption(option: api.ui.selector.Option<ImageSelectorDisplayValue>) {
            super.setOption(option);

            var content: ImageSelectorDisplayValue = this.getOption().displayValue;

            if (content.getContentSummary()) {
                this.updateIconSrc(content);
                this.label.getEl().setInnerHtml(content.getLabel());
            } else {
                this.showProgress();
            }
        }

        private updateIconSrc(content: ImageSelectorDisplayValue) {
            var newIconSrc = content.getImageUrl() + "?thumbnail=false&size=" + ImageSelectorSelectedOptionView.IMAGE_SIZE;

            if (this.icon.getSrc().indexOf(newIconSrc) == -1) {
                if (this.isVisible()) {
                    this.showSpinner();
                }
                this.icon.setSrc(newIconSrc);
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
            this.label = new api.dom.DivEl("label");
            this.check = new api.ui.Checkbox();
            this.progress = new api.ui.ProgressBar();
            this.error = new api.dom.DivEl("error");
            this.loadMask = new LoadMask(this);

            var squaredContent = new api.dom.DivEl('squared-content');
            squaredContent.appendChildren(this.icon, this.label, this.check, this.progress, this.error, this.loadMask);

            this.appendChild(squaredContent);

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
                    if (!this.icon.isLoaded()) {
                        this.showSpinner();
                    }
                }
            });
            this.icon.onLoaded((event: UIEvent) => {
                if (this.getOption().displayValue.getContentSummary()) {
                    this.showResult();
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

        private showResult() {
            this.loadMask.hide();
            this.icon.getEl().setVisibility('visible');
            this.check.show();
            this.progress.hide();
        }

        showError(text: string) {
            this.progress.hide();
            this.error.setHtml(text).show();
            this.check.show();
        }

        updateProportions() {
            var contentHeight = this.getEl().getHeightWithBorder() -
                                this.getEl().getBorderTopWidth() -
                                this.getEl().getBorderBottomWidth();

            this.centerVertically(this.icon, contentHeight);
            this.centerVertically(this.progress, contentHeight);
            this.centerVertically(this.error, contentHeight);
        }

        private centerVertically(el: api.dom.Element, contentHeight: number) {
            el.getEl().setMarginTop(Math.max(0, (contentHeight - el.getEl().getHeight()) / 2) + 'px');
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

        onChecked(listener: {(option: ImageSelectorSelectedOptionView, checked: boolean): void;}) {
            this.selectionChangeListeners.push(listener);
        }

        unChecked(listener: {(option: ImageSelectorSelectedOptionView, checked: boolean): void;}) {
            this.selectionChangeListeners = this.selectionChangeListeners.filter(function (curr) {
                return curr != listener;
            });
        }

    }
}