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
    import LinkModalDialog = api.form.inputtype.text.tiny.LinkModalDialog;

    export class TinyMCE extends support.BaseInputTypeNotManagingAdd<any,string> {

        private editors: TinyEditorOccurenceInfo[];

        constructor(config: api.form.inputtype.InputTypeViewContext<any>) {
            super(config);
            this.addClass("tinymce-editor");
            this.editors = [];
        }

        getValueType(): ValueType {
            return ValueTypes.HTML_PART;
        }

        newInitialValue(): Value {
            return ValueTypes.HTML_PART.newValue("");
        }

        createInputOccurrenceElement(index: number, property: Property): api.dom.Element {

            var textAreaEl = new api.ui.text.TextArea(this.getInput().getName() + "-" + index);
            var editorId = textAreaEl.getId();

            var clazz = editorId.replace(/\./g, '_');
            textAreaEl.addClass(clazz);

            var textAreaWrapper = new api.dom.DivEl();

            this.editors.push({id: editorId, textAreaWrapper: textAreaWrapper, property: property});

            textAreaEl.onRendered(() => {
                this.initEditor(editorId, property, textAreaWrapper);
            });

            textAreaWrapper.appendChild(textAreaEl);

            this.setFocusOnEditorAfterCreate(textAreaWrapper, editorId);

            return textAreaWrapper;
        }

        private initEditor(id: string, property: Property, textAreaWrapper: Element): void {
            var focusedEditorCls = "tinymce-editor-focused";
            var baseUrl = CONFIG.assetsUri;

            tinymce.init({
                selector:          'textarea.' + id.replace(/\./g, '_'),
                document_base_url: baseUrl + '/common/lib/tinymce/',
                skin_url:          baseUrl + '/common/lib/tinymce/skins/lightgray',
                content_css: baseUrl + '/common/styles/api/form/inputtype/text/tinymce-editor.css',
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
                    editor.addCommand("openLinkDialog", this.openLinkDialog, this);
                    editor.on('change', (e) => {
                        var value = this.newValue(this.getEditor(id).getContent());
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
                            var value = this.newValue(this.getEditor(id).getContent());
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
                    this.setEditorContent(id, property);
                    this.setupStickyEditorToolbarForInputOccurence(textAreaWrapper);
                    this.removeTooltipFromEditorArea(textAreaWrapper);
                }
            });
        }

        private setFocusOnEditorAfterCreate(inputOccurence: Element, id: string): void {
            inputOccurence.giveFocus = () => {
                try {
                    this.getEditor(id).focus();
                    return true;
                }
                catch (e) {
                    console.log("Element.giveFocus(): Failed to give focus to TinyMCE element: id = " + this.getId());
                    return false;
                }
            };
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

        private getEditor(editorId: string): TinyMceEditor {
            return tinymce.get(editorId);
        }

        private setEditorContent(editorId: string, property: Property): void {
            if (property.hasNonNullValue()) {
                this.getEditor(editorId).setContent(property.getString());
            }
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

        private openLinkDialog(linkConfig: LinkConfig) {
            var linkModalDialog = new LinkModalDialog(linkConfig.editor, linkConfig.link);
            linkModalDialog.open();
        }

        private removeTooltipFromEditorArea(inputOccurence: Element) {
            wemjq(inputOccurence.getHTMLElement()).find("iframe").removeAttr("title");
        }

        handleDnDStart(event: Event, ui: JQueryUI.SortableUIParams): void {
            super.handleDnDStart(event, ui);

            var editorId = wemjq('textarea', ui.item)[0].id;
            this.destroyEditor(editorId);
        }

        handleDnDStop(event: Event, ui: JQueryUI.SortableUIParams): void {
            var editorId = wemjq('textarea', ui.item)[0].id;

            this.reInitEditor(editorId);
            tinymce.execCommand('mceAddEditor', false, editorId);

            this.getEditor(editorId).focus();
        }

        private destroyEditor(id: string): void {
            this.getEditor(id).destroy(false);
        }

        private reInitEditor(id: string) {
            var savedEditor: TinyEditorOccurenceInfo = this.findElementByFieldValue(this.editors, "id", id);

            this.initEditor(id, savedEditor.property, savedEditor.textAreaWrapper);
        }

        private findElementByFieldValue<T>(array: Array<T>, field: string, value: any): T {
            var result: T;

            array.every((element: T) => {
                if (element[field] == value) {
                    result = element;
                    return false;
                }
                return true;
            });

            return result;
        }
    }

    export interface TinyEditorOccurenceInfo {
        id: string;
        textAreaWrapper: Element;
        property: Property;
    }

    interface LinkConfig {
        editor: TinyMceEditor
        link: HTMLElement
    }

    api.form.inputtype.InputTypeManager.register(new api.Class("TinyMCE", TinyMCE));
}