module api.form.inputtype.text {

    declare var CONFIG;

    import support = api.form.inputtype.support;
    import Property = api.data.Property;
    import Value = api.data.Value;
    import ValueType = api.data.ValueType;
    import ValueTypes = api.data.ValueTypes;
    import ContentSummary = api.content.ContentSummary;
    import Element = api.dom.Element;
    import OptionSelectedEvent = api.ui.selector.OptionSelectedEvent;

    export class TinyMCE extends support.BaseInputTypeNotManagingAdd<any,string> {

        private editor: TinyMceEditor;

        constructor(config: api.form.inputtype.InputTypeViewContext<any>) {
            super(config);
            this.addClass("tinymce-editor");
        }

        getValueType(): ValueType {
            return ValueTypes.HTML_PART;
        }

        newInitialValue(): Value {
            return ValueTypes.HTML_PART.newValue("");
        }

        createInputOccurrenceElement(index: number, property: Property): api.dom.Element {
            var focusedEditorCls = "tinymce-editor-focused";
            var textAreaEl = new api.ui.text.TextArea(this.getInput().getName() + "-" + index);

            var clazz = textAreaEl.getId().replace(/\./g, '_');
            textAreaEl.addClass(clazz);
            var baseUrl = CONFIG.assetsUri;
            var textAreaWrapper = new api.dom.DivEl();

            textAreaEl.onRendered(() => {
                tinymce.init({
                    selector: 'textarea.' + clazz,
                    document_base_url: baseUrl + '/common/lib/tinymce/',
                    skin_url: baseUrl + '/common/lib/tinymce/skins/lightgray',
                    theme_url: 'modern',

                    toolbar: [
                        "styleselect | cut copy pastetext | bullist numlist outdent indent | charmap link unlink | table | code"
                    ],
                    menubar: false,
                    statusbar: false,
                    paste_as_text: true,
                    plugins: ['autoresize', 'table', 'paste', 'charmap', 'code'],
                    external_plugins: {
                        "link": baseUrl + "/common/js/form/inputtype/text/plugins/link.js"
                    },
                    autoresize_min_height: 100,
                    autoresize_bottom_margin: 0,
                    height: 100,

                    setup: (editor) => {
                        editor.addCommand("initSelectors", this.initSelectors, this);
                        editor.on('change', (e) => {
                            var value = this.newValue(this.editor.getContent());
                            property.setValue(value);
                        });
                        editor.on('focus', (e) => {
                            this.resetInputHeight();
                            textAreaWrapper.addClass(focusedEditorCls);
                        });
                        editor.on('blur', (e) => {
                            this.setStaticInputHeight();
                            textAreaWrapper.removeClass(focusedEditorCls);
                        });
                        editor.on('keydown', (e) => {
                            if ((e.metaKey || e.ctrlKey) && e.keyCode === 83) {
                                e.preventDefault();
                                var value = this.newValue(this.editor.getContent());
                                property.setValue(value); // ensure that entered value is stored

                                wemjq(this.getEl().getHTMLElement()).simulate(e.type, { // as editor resides in a frame - propagate event via wrapping element
                                    bubbles: e.bubbles,
                                    cancelable: e.cancelable,
                                    view: parent,
                                    ctrlKey: e.ctrlKey,
                                    altKey: e.altKey,
                                    shiftKey: e.shiftKey,
                                    metaKey: e.metaKey,
                                    keyCode: e.keyCode,
                                    charCode: e.charCode
                                });
                            }
                        });
                    },
                    init_instance_callback: (editor) => {
                        this.editor = this.getEditor(textAreaEl.getId(), property);
                        this.setupStickyEditorToolbarForInputOccurence(textAreaWrapper);
                    }
                });

            });

            textAreaWrapper.appendChild(textAreaEl);
            return textAreaWrapper;
        }

        private setupStickyEditorToolbarForInputOccurence(inputOccurence: Element) {
            wemjq(this.getHTMLElement()).closest(".form-panel").on("scroll", () => this.updateStickyEditorToolbar(inputOccurence));

            api.ui.responsive.ResponsiveManager.onAvailableSizeChanged(this, () => {
                this.updateEditorToolbarWidth();
                this.updateEditorToolbarPos(inputOccurence);
            });

            this.onOccurrenceAdded(() => {
                this.resetInputHeight();
                this.updateEditorToolbarWidth();
            });

            this.onOccurrenceRemoved(() => {
                this.resetInputHeight();
                this.updateEditorToolbarWidth();
            });
        }

        private updateStickyEditorToolbar(inputOccurence: Element) {
            if (!this.editorTopEdgeIsVisible(inputOccurence) && this.editorLowerEdgeIsVisible(inputOccurence)) {
                inputOccurence.addClass("sticky-toolbar");
                this.updateEditorToolbarWidth();
                this.updateEditorToolbarPos(inputOccurence);
            }
            else {
                inputOccurence.removeClass("sticky-toolbar")
            }
        }

        private updateEditorToolbarWidth() {
            wemjq(this.getHTMLElement()).find(".mce-toolbar-grp").width(wemjq(this.getHTMLElement()).find(".mce-edit-area").innerWidth());
        }

        private updateEditorToolbarPos(inputOccurence: Element) {
            wemjq(inputOccurence.getHTMLElement()).find(".mce-toolbar-grp").css({top: this.getToolbarOffsetTop(1)});
        }

        private editorTopEdgeIsVisible(inputOccurence: Element): boolean {
            return this.calcDistToTopOfScrlbleArea(inputOccurence) > 0;
        }

        private editorLowerEdgeIsVisible(inputOccurence: Element): boolean {
            var distToTopOfScrlblArea = this.calcDistToTopOfScrlbleArea(inputOccurence);
            var editorToolbarHeight = wemjq(inputOccurence.getHTMLElement()).find(".mce-toolbar-grp").outerHeight(true);
            return (inputOccurence.getEl().getHeightWithoutPadding() - editorToolbarHeight + distToTopOfScrlblArea) > 0;
        }

        private calcDistToTopOfScrlbleArea(inputOccurence: Element): number {
            return inputOccurence.getEl().getOffsetTop() - this.getToolbarOffsetTop();
        }

        private getToolbarOffsetTop(delta: number = 0): number {
            var toolbar = wemjq(this.getHTMLElement()).closest(".form-panel").find(".wizard-step-navigator-and-toolbar"),
                stickyToolbarHeight = toolbar.outerHeight(true),
                stickyToolbarOffset = toolbar.offset().top;

            return stickyToolbarOffset + stickyToolbarHeight + delta;
        }

        private resetInputHeight() {
            wemjq(this.getHTMLElement()).height("auto");
        }

        private setStaticInputHeight() {
            wemjq(this.getHTMLElement()).height(wemjq(this.getHTMLElement()).height());
        }

        private getEditor(editorId: string, property: Property): TinyMceEditor {
            var editor = tinymce.get(editorId);

            if (property.hasNonNullValue()) {
                editor.setContent(property.getString());
            }

            return editor;
        }

        private newValue(s: string): Value {
            return new Value(s, ValueTypes.HTML_PART);
        }

        valueBreaksRequiredContract(value: Value): boolean {
            return value.isNull() || !value.getType().equals(ValueTypes.HTML_PART) || api.util.StringHelper.isBlank(value.getString());
        }

        hasInputElementValidUserInput(inputElement: api.dom.Element) {

            // TODO
            return true;
        }

        private createContentSelector(contentTypeNames?: api.schema.content.ContentTypeName[]): api.content.ContentComboBox {
            var loader = new api.content.ContentSummaryLoader(),
                contentSelector = api.content.ContentComboBox.create().setLoader(loader).setMaximumOccurrences(1).build(),
                focusedSelectorCls = "mce-content-selector-focused";

            if (contentTypeNames) {
                loader.setAllowedContentTypeNames(contentTypeNames);
            }

            contentSelector.addClass("mce-abs-layout-item mce-content-selector");

            contentSelector.onFocus((e) => {
                contentSelector.addClass(focusedSelectorCls);
            });

            contentSelector.onBlur((e) => {
                contentSelector.removeClass(focusedSelectorCls);
            });

            return contentSelector;
        }

        private addContentSelector(dialogEl: HTMLElement, placeholderCls: string, contentTypeNames?: api.schema.content.ContentTypeName[]) {
            var placeholder = wemjq(dialogEl).find(placeholderCls),
                contentSelector = this.createContentSelector(contentTypeNames);

            contentSelector.onOptionSelected((event: OptionSelectedEvent<ContentSummary>) => {
                placeholder.val(event.getOption().value);
            });

            contentSelector.onOptionDeselected(() => {
                placeholder.val("");
            });

            wemjq(contentSelector.getHTMLElement()).insertAfter(placeholder);

            placeholder.hide();

            if (placeholder.val()) {
                contentSelector.setValue(placeholder.val());
            }
        }

        private initSelectors(ui: boolean, dialogEl: HTMLElement) {
            var focusEl = wemjq(dialogEl).find(".mce-link-text");

            this.addContentSelector(dialogEl, ".mce-link-tab-content-placeholder");
            this.addContentSelector(dialogEl, ".mce-link-tab-media-placeholder", api.schema.content.ContentTypeName.getMediaTypes());

            if (focusEl) {
                focusEl.focus();
            }
        }
    }

    api.form.inputtype.InputTypeManager.register(new api.Class("TinyMCE", TinyMCE));
}