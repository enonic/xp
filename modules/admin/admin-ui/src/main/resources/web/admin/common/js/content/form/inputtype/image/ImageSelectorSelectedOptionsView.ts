module api.content.form.inputtype.image {

    import Option = api.ui.selector.Option;
    import SelectedOption = api.ui.selector.combobox.SelectedOption;
    import ContentSummary = api.content.ContentSummary;
    import Property = api.data.Property;
    import Value = api.data.Value;
    import ValueType = api.data.ValueType;
    import ValueTypes = api.data.ValueTypes;
    import ValueChangedEvent = api.form.inputtype.ValueChangedEvent;
    import LoadMask = api.ui.mask.LoadMask;
    import Tooltip = api.ui.Tooltip;

    export class ImageSelectorSelectedOptionsView extends api.ui.selector.combobox.BaseSelectedOptionsView<ImageSelectorDisplayValue> {

        private numberOfOptionsPerRow: number = 3;

        private activeOption: SelectedOption<ImageSelectorDisplayValue>;

        private selection: SelectedOption<ImageSelectorDisplayValue>[] = [];

        private toolbar: SelectionToolbar;

        private editSelectedOptionsListeners: {(option: SelectedOption<ImageSelectorDisplayValue>[]): void}[] = [];

        private removeSelectedOptionsListeners: {(option: SelectedOption<ImageSelectorDisplayValue>[]): void}[] = [];

        private mouseClickListener: {(MouseEvent): void};

        constructor() {
            super();

            this.setOccurrencesSortable(true);

            this.toolbar = new SelectionToolbar();
            this.toolbar.hide();
            this.toolbar.onEditClicked(() => {
                this.notifyEditSelectedOptions(this.selection);
            });
            this.toolbar.onRemoveClicked(() => {
                this.removeSelectedOptions(this.selection);
            });
            this.appendChild(this.toolbar);
        }

        removeSelectedOptions(options: SelectedOption<ImageSelectorDisplayValue>[]) {
            this.notifyRemoveSelectedOptions(options);
            // clear the selection;
            this.selection.length = 0;
            this.updateSelectionToolbarLayout();
            this.hideImageSelectorDialog();
        }

        createSelectedOption(option: Option<ImageSelectorDisplayValue>): SelectedOption<ImageSelectorDisplayValue> {
            return new SelectedOption<ImageSelectorDisplayValue>(new ImageSelectorSelectedOptionView(option), this.count());
        }

        addOption(option: Option<ImageSelectorDisplayValue>, silent: boolean = false): boolean {

            var selectedOption = this.getByOption(option);
            if (!selectedOption) {
                this.addNewOption(option, silent);
                return true;
            } else if (selectedOption) {
                var displayValue = selectedOption.getOption().displayValue;
                if (displayValue.getContentSummary() == null && option.displayValue.getContentSummary() != null) {
                    this.updateUploadedOption(option);
                    return true;
                }
            }
            return false;
        }

        private addNewOption(option: Option<ImageSelectorDisplayValue>, silent: boolean) {
            var selectedOption: SelectedOption<ImageSelectorDisplayValue> = this.createSelectedOption(option);
            this.getSelectedOptions().push(selectedOption);

            var optionView: ImageSelectorSelectedOptionView = <ImageSelectorSelectedOptionView>selectedOption.getOptionView();

            optionView.onClicked((event: MouseEvent) => {

                this.uncheckOthers(selectedOption);

                if (document.activeElement == optionView.getEl().getHTMLElement() || this.activeOption == selectedOption) {
                    optionView.getCheckbox().toggleChecked();
                } else {
                    optionView.getCheckbox().setChecked(true);
                }
                optionView.getCheckbox().giveFocus();
            });

            optionView.getCheckbox().onKeyDown((event: KeyboardEvent) => {
                var checkbox = optionView.getCheckbox();

                switch (event.which) {
                case 32: // Spacebar
                    var isChecked = !checkbox.isChecked();
                    checkbox.setChecked(isChecked, isChecked);
                    break;
                case 8: // Backspace
                    checkbox.setChecked(false);
                    this.removeOptionViewAndRefocus(selectedOption);
                    event.preventDefault();
                    break;
                case 46: // Delete
                    checkbox.setChecked(false);
                    this.removeOptionViewAndRefocus(selectedOption);
                    break;
                case 13: // Enter
                    this.notifyEditSelectedOptions([selectedOption]);
                    break;
                case 9: // tab
                    this.hideImageSelectorDialog();
                    break;
                }
                event.stopPropagation();
            });

            optionView.getCheckbox().onFocus((event: FocusEvent) => this.showImageSelectorDialog(selectedOption));

            optionView.onChecked((view: ImageSelectorSelectedOptionView, checked: boolean) => {
                if (checked) {
                    if (this.selection.indexOf(selectedOption) < 0) {
                        this.selection.push(selectedOption);
                    }
                } else {
                    var index = this.selection.indexOf(selectedOption);
                    if (index > -1) {
                        this.selection.splice(index, 1);
                    }
                }

                this.updateSelectionToolbarLayout();
            });

            var optionImg = optionView.getIcon();
            optionImg.onLoaded((event: UIEvent) => {
                var loadedListener = () => {
                    optionView.updateProportions();
                    this.refreshSortable();
                };
                if (optionImg.isVisible()) {
                    loadedListener();
                } else {
                    // execute listener on shown in case it's hidden now to correctly calc proportions
                    var shownListener = () => {
                        loadedListener();
                        optionImg.unShown(shownListener);
                    };
                    optionImg.onShown(shownListener);
                }
            });

            optionView.insertBeforeEl(this.toolbar);

            if (!silent) {
                this.notifyOptionSelected(selectedOption);
            }

            new Tooltip(optionView, option.displayValue.getPath(), 1000);
        }

        updateUploadedOption(option: Option<ImageSelectorDisplayValue>) {
            var selectedOption = this.getByOption(option);
            var content = option.displayValue.getContentSummary();

            var newOption = <Option<ImageSelectorDisplayValue>>{
                value: content.getId(),
                displayValue: ImageSelectorDisplayValue.fromContentSummary(content)
            };

            selectedOption.getOptionView().setOption(newOption);
        }

        private uncheckOthers(option: SelectedOption<ImageSelectorDisplayValue>) {
            var selectedOptions = this.getSelectedOptions();
            for (var i = 0; i < selectedOptions.length; i++) {
                var view = <ImageSelectorSelectedOptionView>selectedOptions[i].getOptionView();
                if (i != option.getIndex()) {
                    view.getCheckbox().setChecked(false);
                }
            }
        }

        private removeOptionViewAndRefocus(option: SelectedOption<ImageSelectorDisplayValue>) {
            var index = this.isLast(option.getIndex()) ?
                        (this.isFirst(option.getIndex()) ? -1 : option.getIndex() - 1) :
                        option.getIndex();

            this.notifyRemoveSelectedOptions([option]);
            this.hideImageSelectorDialog();

            if (index > -1) {
                (<ImageSelectorSelectedOptionView>this.getByIndex(index).getOptionView()).getCheckbox().giveFocus();
            }
        }

        private showImageSelectorDialog(option: SelectedOption<ImageSelectorDisplayValue>) {

            if (this.activeOption) {
                this.activeOption.getOptionView().removeClass("editing");
            }
            this.activeOption = option;
            option.getOptionView().addClass("editing");

            this.setOutsideClickListener();

            wemjq(this.getHTMLElement()).sortable("disable");
        }

        private updateSelectionToolbarLayout() {
            var showToolbar = this.selection.length > 0;
            this.toolbar.setVisible(showToolbar);
            if (showToolbar) {
                this.toolbar.setSelectionCount(this.selection.length);
            }
        }

        private hideImageSelectorDialog() {
            if (this.activeOption) {
                this.activeOption.getOptionView().removeClass('editing first-in-row last-in-row');
                this.activeOption = null;
            }
            wemjq(this.getHTMLElement()).sortable("enable");

            api.dom.Body.get().unClicked(this.mouseClickListener);
        }

        private setOutsideClickListener() {
            this.mouseClickListener = (event: MouseEvent) => {
                for (var element = event.target; element; element = (<any>element).parentNode) {
                    if (element == this.getHTMLElement()) {
                        return;
                    }
                }
                this.hideImageSelectorDialog();
            };

            api.dom.Body.get().onClicked(this.mouseClickListener);
        }

        private isFirstInRow(index: number): boolean {
            return index % this.numberOfOptionsPerRow == 0;
        }

        private isLastInRow(index: number): boolean {
            return index % this.numberOfOptionsPerRow == 2;
        }

        private isFirst(index: number): boolean {
            return index == 0;
        }

        private isLast(index: number): boolean {
            return index == this.getSelectedOptions().length - 1;
        }

        private notifyRemoveSelectedOptions(option: SelectedOption<ImageSelectorDisplayValue>[]) {
            this.removeSelectedOptionsListeners.forEach((listener) => {
                listener(option);
            });
        }

        onRemoveSelectedOptions(listener: (option: SelectedOption<ImageSelectorDisplayValue>[]) => void) {
            this.removeSelectedOptionsListeners.push(listener);
        }

        unRemoveSelectedOptions(listener: (option: SelectedOption<ImageSelectorDisplayValue>[]) => void) {
            this.removeSelectedOptionsListeners = this.removeSelectedOptionsListeners.filter(function (curr) {
                return curr != listener;
            });
        }

        private notifyEditSelectedOptions(option: SelectedOption<ImageSelectorDisplayValue>[]) {
            this.editSelectedOptionsListeners.forEach((listener) => {
                listener(option);
            });
        }

        onEditSelectedOptions(listener: (option: SelectedOption<ImageSelectorDisplayValue>[]) => void) {
            this.editSelectedOptionsListeners.push(listener);
        }

        unEditSelectedOptions(listener: (option: SelectedOption<ImageSelectorDisplayValue>[]) => void) {
            this.editSelectedOptionsListeners = this.editSelectedOptionsListeners.filter(function (curr) {
                return curr != listener;
            });
        }

    }

}
