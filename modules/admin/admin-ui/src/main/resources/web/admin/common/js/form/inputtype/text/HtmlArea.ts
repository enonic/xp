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
    import LinkModalDialog = api.form.inputtype.text.htmlarea.LinkModalDialog;
    import ImageModalDialog = api.form.inputtype.text.htmlarea.ImageModalDialog;
    import AnchorModalDialog = api.form.inputtype.text.htmlarea.AnchorModalDialog;

    export class HtmlArea extends support.BaseInputTypeNotManagingAdd<string> {

        private editors: HtmlAreaOccurrenceInfo[];
        private contentId: api.content.ContentId;

        static imagePrefix = "image://";
        static maxImageWidth = 640;

        private previousScrollPos: number = 0; // fix for XP-736
        private isScrollProhibited: boolean = false;

        constructor(config: api.content.form.inputtype.ContentInputTypeViewContext) {

            super(config);

            this.addClass("html-area");
            this.editors = [];
            this.contentId = config.contentId;
        }

        getValueType(): ValueType {
            return ValueTypes.STRING;
        }

        newInitialValue(): Value {
            return ValueTypes.STRING.newValue("");
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
            this.previousScrollPos = wemjq(this.getHTMLElement()).closest(".form-panel").scrollTop(); // XP-736

            var focusedEditorCls = "html-area-focused";
            var baseUrl = CONFIG.assetsUri;

            tinymce.init({
                selector: 'textarea.' + id.replace(/\./g, '_'),
                document_base_url: baseUrl + '/common/lib/tinymce/',
                skin_url: baseUrl + '/common/lib/tinymce/skins/lightgray',
                content_css: baseUrl + '/common/styles/api/form/inputtype/text/tinymce-editor.css',
                theme_url: 'modern',

                toolbar: [
                    "styleselect | cut copy pastetext | bullist numlist outdent indent | charmap anchor image link unlink | table | code"
                ],
                menubar: false,
                statusbar: false,
                paste_as_text: true,
                plugins: ['autoresize', 'table', 'paste', 'charmap', 'code'],
                external_plugins: {
                    "link": baseUrl + "/common/js/form/inputtype/text/plugins/link.js",
                    "image": baseUrl + "/common/js/form/inputtype/text/plugins/image.js",
                    "anchor": baseUrl + "/common/js/form/inputtype/text/plugins/anchor.js"
                },
                autoresize_min_height: 100,
                autoresize_bottom_margin: 0,
                height: 100,

                setup: (editor) => {
                    editor.addCommand("openLinkDialog", this.openLinkDialog, this);
                    editor.addCommand("openImageDialog", this.openImageDialog, this);
                    editor.addCommand("openAnchorDialog", this.openAnchorDialog, this);
                    editor.on('change', (e) => {
                        this.setPropertyValue(id, property);
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
                        if ((e.metaKey || e.ctrlKey) && e.keyCode === 83) {  // Cmd-S or Ctrl-S
                            e.preventDefault();

                            this.setPropertyValue(id, property);

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

                        if (e.keyCode == 46) { // DELETE
                            var selectedNode = editor.selection.getRng().startContainer;
                            if (/^(FIGURE)$/.test(selectedNode.nodeName)) {
                                editor.execCommand('mceRemoveNode', false, selectedNode);
                                editor.focus();
                            }
                        }
                    });

                    var dragParentElement;
                    editor.on('dragstart', (e) => {
                        dragParentElement = e.target.parentElement || e.target.parentNode;
                    });

                    editor.on('drop', (e) => {
                        if (dragParentElement) {
                            // prevent browser from handling the drop
                            e.preventDefault();

                            e.target.appendChild(dragParentElement);
                            dragParentElement = undefined;
                        }
                    });

                },
                init_instance_callback: (editor) => {
                    this.setEditorContent(id, property);
                    if (this.notInLiveEdit()) {
                        this.setupStickyEditorToolbarForInputOccurence(textAreaWrapper);
                    }
                    this.removeTooltipFromEditorArea(textAreaWrapper);
                    this.temporarilyDisableScrolling(); // XP-736
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
                    console.log("Element.giveFocus(): Failed to give focus to HtmlArea element: id = " + this.getId());
                    return false;
                }
            };
        }

        private setupStickyEditorToolbarForInputOccurence(inputOccurence: Element) {
            wemjq(this.getHTMLElement()).closest(".form-panel").on("scroll", (event) => {
                this.updateStickyEditorToolbar(inputOccurence);

                if (this.isScrollProhibited) {
                    wemjq(this.getHTMLElement()).closest(".form-panel").scrollTop(this.previousScrollPos);
                }
            });

            api.ui.responsive.ResponsiveManager.onAvailableSizeChanged(this, () => {
                this.updateEditorToolbarWidth();
                this.updateEditorToolbarPos(inputOccurence);
            });

            this.onRemoved((event) => {
                api.ui.responsive.ResponsiveManager.unAvailableSizeChanged(this);
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

        private temporarilyDisableScrolling() {
            this.isScrollProhibited = true;
            setTimeout(() => {
                this.isScrollProhibited = false;
            }, 300);
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
                offset = toolbar.offset(),
                stickyToolbarOffset = offset ? offset.top : 0;

            return stickyToolbarOffset + stickyToolbarHeight + delta;
        }

        private resetInputHeight() {
            wemjq(this.getHTMLElement()).height("auto");
        }

        private setStaticInputHeight() {
            wemjq(this.getHTMLElement()).height(wemjq(this.getHTMLElement()).height());
        }

        private getEditor(editorId: string): HtmlAreaEditor {
            return tinymce.get(editorId);
        }

        private setEditorContent(editorId: string, property: Property): void {
            if (property.hasNonNullValue()) {
                this.getEditor(editorId).setContent(this.propertyValue2Content(property.getString()));
            }
        }

        private notInLiveEdit(): boolean {
            return !(wemjq(this.getHTMLElement()).parents(".inspection-panel").length > 0);
        }

        private setPropertyValue(id: string, property: Property) {
            property.setValue(this.editorContent2PropertyValue(id));
        }

        private newValue(s: string): Value {
            return new Value(s, ValueTypes.STRING);
        }

        valueBreaksRequiredContract(value: Value): boolean {
            return value.isNull() || !value.getType().equals(ValueTypes.STRING) || api.util.StringHelper.isBlank(value.getString());
        }

        hasInputElementValidUserInput(inputElement: api.dom.Element) {

            // TODO
            return true;
        }

        private openLinkDialog(config: HtmlAreaAnchor) {
            var linkModalDialog = new LinkModalDialog(config);
            linkModalDialog.open();
        }

        private openImageDialog(config: HtmlAreaImage) {
            var imageModalDialog = new ImageModalDialog(config, this.contentId);
            imageModalDialog.open();
        }

        private openAnchorDialog(editor: HtmlAreaEditor) {
            var anchorModalDialog = new AnchorModalDialog(editor);
            anchorModalDialog.open();
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
            var savedEditor: HtmlAreaOccurrenceInfo = this.findElementByFieldValue(this.editors, "id", id);

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

        private getConvertedImageSrc(imgSrc: string): string {
            var contentId = imgSrc.replace(HtmlArea.imagePrefix, api.util.StringHelper.EMPTY_STRING),
                imageUrl = new api.content.ContentImageUrlResolver().
                    setContentId(new api.content.ContentId(contentId)).
                    setScaleWidth(true).
                    setSize(HtmlArea.maxImageWidth).
                    resolve();

            return "src=\"" + imageUrl + "\" data-src=\"" + imgSrc + "\"";
        }

        private propertyValue2Content(propertyValue: string) {
            var content = propertyValue,
                processedContent = propertyValue,
                regex = /<img.*?src="(.*?)"/g,
                imgSrcs, imgSrc;

            while ((imgSrcs = regex.exec(content)) != null) {
                imgSrc = imgSrcs[1];
                if (imgSrc.indexOf(HtmlArea.imagePrefix) === 0) {
                    processedContent = processedContent.replace("src=\"" + imgSrc + "\"", this.getConvertedImageSrc(imgSrc));
                }
            }

            return processedContent;
        }

        private editorContent2PropertyValue(editorId: string): Value {
            var content = this.getEditor(editorId).getContent(),
                processedContent = this.getEditor(editorId).getContent(),
                regex = /<img.*?data-src="(.*?)".*?>/g,
                imgTags, imgTag;

            while ((imgTags = regex.exec(content)) != null) {
                imgTag = imgTags[0];
                if (imgTag.indexOf("<img ") === 0 && imgTag.indexOf(HtmlArea.imagePrefix) > 0) {
                    var dataSrc = /<img.*?data-src="(.*?)".*?>/.exec(imgTag)[1],
                        src = /<img.*?src="(.*?)".*?>/.exec(imgTags[0])[1];

                    var convertedImg = imgTag.replace(src, dataSrc).replace(" data-src=\"" + dataSrc + "\"",
                        api.util.StringHelper.EMPTY_STRING);
                    processedContent = processedContent.replace(imgTag, convertedImg);
                }
            }

            return this.newValue(processedContent);
        }
    }

    export interface HtmlAreaOccurrenceInfo {
        id: string;
        textAreaWrapper: Element;
        property: Property;
    }

    export interface HtmlAreaAnchor {
        editor: HtmlAreaEditor
        element: HTMLElement
        text: string
        anchorList: string[]
        onlyTextSelected: boolean
    }

    export interface HtmlAreaImage {
        editor: HtmlAreaEditor
        element: HTMLElement
        container: HTMLElement
        callback: Function
    }

    api.form.inputtype.InputTypeManager.register(new api.Class("HtmlArea", HtmlArea));
}