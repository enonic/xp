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

        private modalDialog: api.form.inputtype.text.htmlarea.ModalDialog;

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
            if (!ValueTypes.STRING.equals(property.getType())) {
                property.convertValueType(ValueTypes.STRING);
            }

            var value = this.processPropertyValue(property.getString());
            var textAreaEl = new api.ui.text.TextArea(this.getInput().getName() + "-" + index, value);

            var editorId = textAreaEl.getId();

            var clazz = editorId.replace(/\./g, '_');
            textAreaEl.addClass(clazz);

            var textAreaWrapper = new api.dom.DivEl();

            this.editors.push({id: editorId, textAreaWrapper: textAreaWrapper, property: property});

            textAreaEl.onRendered(() => {
                this.initEditor(editorId, property, textAreaWrapper);
            });
            textAreaEl.onRemoved(() => {
                this.destroyEditor(editorId);
            });

            textAreaWrapper.appendChild(textAreaEl);

            this.setFocusOnEditorAfterCreate(textAreaWrapper, editorId);

            return textAreaWrapper;
        }

        updateInputOccurrenceElement(occurrence: api.dom.Element, property: api.data.Property, unchangedOnly: boolean) {
            var textArea = <api.ui.text.TextArea> occurrence.getFirstChild();
            var id = textArea.getId();

            if (!unchangedOnly || !textArea.isDirty()) {
                this.setEditorContent(id, property);
            }
        }

        private initEditor(id: string, property: Property, textAreaWrapper: Element): void {
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
                formats: {
                    alignleft: [
                        {
                            selector: 'img,figure,p,h1,h2,h3,h4,h5,h6,td,th,tr,div,ul,ol,li',
                            styles: {textAlign: 'left'},
                            defaultBlock: 'div'
                        },
                        {selector: 'table', collapsed: false, styles: {'float': 'left'}}
                    ],
                    aligncenter: [
                        {
                            selector: 'img,figure,p,h1,h2,h3,h4,h5,h6,td,th,tr,div,ul,ol,li',
                            styles: {textAlign: 'center'},
                            defaultBlock: 'div'
                        },
                        {selector: 'table', collapsed: false, styles: {marginLeft: 'auto', marginRight: 'auto'}}
                    ],
                    alignright: [
                        {
                            selector: 'img,figure,p,h1,h2,h3,h4,h5,h6,td,th,tr,div,ul,ol,li',
                            styles: {textAlign: 'right'},
                            defaultBlock: 'div'
                        },
                        {selector: 'table', collapsed: false, styles: {'float': 'right'}}
                    ],
                    alignjustify: [
                        {
                            selector: 'img,figure,p,h1,h2,h3,h4,h5,h6,td,th,tr,div,ul,ol,li',
                            styles: {textAlign: 'justify'},
                            defaultBlock: 'div'
                        }
                    ]
                },
                menubar: false,
                statusbar: false,
                paste_as_text: true,
                plugins: ['autoresize', 'table', 'paste', 'charmap', 'code'],
                external_plugins: {
                    "link": baseUrl + "/common/js/form/inputtype/text/plugins/link.js",
                    "image": baseUrl + "/common/js/form/inputtype/text/plugins/image.js",
                    "anchor": baseUrl + "/common/js/form/inputtype/text/plugins/anchor.js"
                },
                object_resizing: "table",
                autoresize_min_height: 100,
                autoresize_bottom_margin: 0,
                height: 100,

                setup: (editor) => {
                    editor.addCommand("openLinkDialog", this.openLinkDialog, this);
                    editor.addCommand("openImageDialog", this.openImageDialog, this);
                    editor.addCommand("openAnchorDialog", this.openAnchorDialog, this);
                    editor.on('NodeChange', (e) => {
                        this.notifyValueChanged(id, textAreaWrapper);
                    });
                    editor.on('focus', (e) => {
                        this.resetInputHeight();
                        textAreaWrapper.addClass(focusedEditorCls);
                    });
                    editor.on('blur', (e) => {
                        this.setStaticInputHeight();
                        if (!(this.modalDialog && this.modalDialog.isVisible())) {
                            textAreaWrapper.removeClass(focusedEditorCls);
                        }
                    });
                    editor.on('keydown', (e) => {
                        if ((e.metaKey || e.ctrlKey) && e.keyCode === 83) {  // Cmd-S or Ctrl-S
                            e.preventDefault();

                            this.notifyValueChanged(id, textAreaWrapper);

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

                        if (e.keyCode == 46 || e.keyCode == 8) { // DELETE
                            var selectedNode = editor.selection.getRng().startContainer;
                            if (/^(FIGURE)$/.test(selectedNode.nodeName)) {
                                var previousEl = selectedNode.previousSibling;
                                e.preventDefault();
                                selectedNode.remove();
                                if (previousEl) {
                                    editor.selection.setNode(previousEl);
                                }
                                else {
                                    editor.focus();
                                }
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
                    this.updateImageAlignmentBehaviour(editor);
                    this.onShown((event) => {
                        // invoke auto resize on shown in case contents have been updated while inactive
                        editor.execCommand('mceAutoResize', false, null);
                    })
                }
            });
        }

        private setFocusOnEditorAfterCreate(inputOccurence: Element, id: string): void {
            inputOccurence.giveFocus = () => {
                var editor = this.getEditor(id);
                if (editor) {
                    editor.focus();
                    return true;
                } else {
                    console.log("Element.giveFocus(): Failed to give focus to HtmlArea element: id = " + this.getId());
                    return false;
                }
            };
        }

        private setupStickyEditorToolbarForInputOccurence(inputOccurence: Element) {
            wemjq(this.getHTMLElement()).closest(".form-panel").on("scroll", (event) => {
                this.updateStickyEditorToolbar(inputOccurence);
            });

            api.ui.responsive.ResponsiveManager.onAvailableSizeChanged(this, () => {
                this.updateEditorToolbarWidth();
                this.updateEditorToolbarPos(inputOccurence);
            });

            this.onRemoved((event) => {
                api.ui.responsive.ResponsiveManager.unAvailableSizeChanged(this);
            });

            this.onOccurrenceRendered(() => {
                this.resetInputHeight();
                this.updateEditorToolbarWidth();
            });

            this.onOccurrenceRemoved(() => {
                this.resetInputHeight();
                this.updateEditorToolbarWidth();
            });
        }

        private updateImageAlignmentBehaviour(editor) {
            var imgs = editor.getBody().querySelectorAll('img');

            for (let i = 0; i < imgs.length; i++) {
                this.changeImageParentAlignmentOnImageAlignmentChange(imgs[i]);
            }
        }

        private changeImageParentAlignmentOnImageAlignmentChange(img: HTMLImageElement) {
            var observer = new MutationObserver((mutations) => {
                mutations.forEach((mutation) => {
                    var alignment = (<HTMLElement>mutation.target).style["text-align"];
                    var keepOriginalSize = img.getAttribute("data-src").indexOf("keepSize=true") > 0;

                    var styleAttr;
                    switch (alignment) {
                    case 'justify':
                    case 'center':
                        styleAttr = "text-align: " + alignment;
                        break;
                    case 'left':
                        styleAttr = "float: left; margin: 15px;" + (keepOriginalSize ? "" : "width: 40%");
                        break;
                    case 'right':
                        styleAttr = "float: right; margin: 15px;" + (keepOriginalSize ? "" : "width: 40%");
                        break;
                    }

                    img.parentElement.setAttribute("style", styleAttr);
                    img.parentElement.setAttribute("data-mce-style", styleAttr);
                });
            });

            var config = {attributes: true, childList: false, characterData: false, attributeFilter: ["style"]};

            observer.observe(img, config);
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
            var editor = this.getEditor(editorId);
            if (property.hasNonNullValue() && editor) {
                editor.setContent(this.processPropertyValue(property.getString()));
            }
            else if (!editor) {
                console.log("Editor with id '" + editorId + "' not found")
            }
        }

        private notInLiveEdit(): boolean {
            return !(wemjq(this.getHTMLElement()).parents(".inspection-panel").length > 0);
        }

        private notifyValueChanged(id: string, occurrence: api.dom.Element) {
            var value = ValueTypes.STRING.newValue(this.processEditorContent(id));
            this.notifyOccurrenceValueChanged(occurrence, value);
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
            this.modalDialog = new LinkModalDialog(config);
            this.modalDialog.open();
        }

        private openImageDialog(config: HtmlAreaImage) {
            this.modalDialog = new ImageModalDialog(config, this.contentId);
            this.modalDialog.open();
        }

        private openAnchorDialog(editor: HtmlAreaEditor) {
            this.modalDialog = new AnchorModalDialog(editor);
            this.modalDialog.open();
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
            var editor = this.getEditor(id);
            if (editor) {
                try {
                    editor.destroy(false);
                }
                catch(e) {
                    //error thrown in FF on tab close - XP-2624
                }
            }
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
            var contentId = api.util.UriHelper.trimUrlParams(imgSrc.replace(HtmlArea.imagePrefix, api.util.StringHelper.EMPTY_STRING)),
                imageUrlResolver = new api.content.ContentImageUrlResolver().setContentId(new api.content.ContentId(contentId)).setTimestamp(new Date()),
                scalingApplied = imgSrc.indexOf("scale=") > 0,
                urlParams = api.util.UriHelper.decodeUrlParams(imgSrc.replace("&amp;", "&"));

            scalingApplied ? imageUrlResolver.setScale(urlParams["scale"]) : imageUrlResolver.setScaleWidth(true);

            if (!urlParams["keepSize"]) {
                imageUrlResolver.setSize(HtmlArea.maxImageWidth);
            }

            var imageUrl = imageUrlResolver.resolve();

            return "src=\"" + imageUrl + "\" data-src=\"" + imgSrc + "\"";
        }

        private processPropertyValue(propertyValue: string): string {
            var processedContent = propertyValue,
                regex = /<img.*?src="(.*?)"/g,
                imgSrcs;

            if (!processedContent) {
                return propertyValue;
            }

            while (processedContent.search(" src=\"" + HtmlArea.imagePrefix) > -1) {
                imgSrcs = regex.exec(processedContent);
                if (imgSrcs) {
                    imgSrcs.forEach((imgSrc: string) => {
                        if (imgSrc.indexOf(HtmlArea.imagePrefix) === 0) {
                            processedContent = processedContent.replace(" src=\"" + imgSrc + "\"", this.getConvertedImageSrc(imgSrc));
                        }
                    });
                }
            }
            return processedContent;
        }

        private processEditorContent(editorId: string): string {
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

            return processedContent;
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