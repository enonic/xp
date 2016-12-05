module api.form.inputtype.text {

    import DivEl = api.dom.DivEl;
    declare var CONFIG;

    import support = api.form.inputtype.support;
    import Property = api.data.Property;
    import Value = api.data.Value;
    import ValueType = api.data.ValueType;
    import ValueTypes = api.data.ValueTypes;
    import ContentSummary = api.content.ContentSummary;
    import Element = api.dom.Element;
    import OptionSelectedEvent = api.ui.selector.OptionSelectedEvent;
    import HTMLAreaBuilder = api.util.htmlarea.editor.HTMLAreaBuilder;
    import HTMLAreaHelper = api.util.htmlarea.editor.HTMLAreaHelper;
    import ModalDialog = api.util.htmlarea.dialog.ModalDialog;
    import ElementHelper = api.dom.ElementHelper;
    import ApplicationKey = api.application.ApplicationKey
    import RoleKeys = api.security.RoleKeys;
    import LoginResult = api.security.auth.LoginResult;
    import Promise = Q.Promise;

    export class HtmlArea extends support.BaseInputTypeNotManagingAdd<string> {

        private editors: HtmlAreaOccurrenceInfo[];
        private content: api.content.ContentSummary;
        private contentPath: api.content.ContentPath;
        private applicationKeys: ApplicationKey[];

        private focusListeners: {(event: FocusEvent): void}[] = [];

        private blurListeners: {(event: FocusEvent): void}[] = [];

        private tools: any = {
            include: "",
            exclude: ""
        };

        private authRequest: Promise<void>;
        private canSeeCode: boolean;

        constructor(config: api.content.form.inputtype.ContentInputTypeViewContext) {

            super(config);

            this.addClass("html-area");
            this.editors = [];
            this.contentPath = config.contentPath;
            this.content = config.content;
            this.applicationKeys = config.site ? config.site.getApplicationKeys() : [];

            this.readConfig(config.inputConfig);

            this.authRequest =
                new api.security.auth.IsAuthenticatedRequest().sendAndParse().then((loginResult: api.security.auth.LoginResult) => {
                    this.canSeeCode = loginResult.getPrincipals().some(principal => RoleKeys.ADMIN.equals(principal) ||
                                                                                    RoleKeys.CMS_ADMIN.equals(principal) ||
                                                                                    RoleKeys.CMS_EXPERT.equals(principal));
                });
        }

        private readConfig(inputConfig: { [element: string]: { [name: string]: string }[]; }): void {
            this.tools.include = inputConfig['include'];
            this.tools.exclude = inputConfig['exclude'];
        }

        getValueType(): ValueType {
            return ValueTypes.STRING;
        }

        newInitialValue(): Value {
            return super.newInitialValue() || ValueTypes.STRING.newValue("");
        }

        createInputOccurrenceElement(index: number, property: Property): api.dom.Element {
            if (!ValueTypes.STRING.equals(property.getType())) {
                property.convertValueType(ValueTypes.STRING);
            }

            var value = HTMLAreaHelper.prepareImgSrcsInValueForEdit(property.getString());
            var textAreaEl = new api.ui.text.TextArea(this.getInput().getName() + "-" + index, value);

            var editorId = textAreaEl.getId();

            var clazz = editorId.replace(/\./g, '_');
            textAreaEl.addClass(clazz);

            var textAreaWrapper = new api.dom.DivEl();

            this.editors.push({id: editorId, textAreaWrapper: textAreaWrapper, property: property, hasStickyToolbar: false});

            textAreaEl.onRendered(() => {
                if (this.authRequest.isFulfilled()) {
                    this.initEditor(editorId, property, textAreaWrapper);
                } else {
                    this.authRequest.then(() => {
                        this.initEditor(editorId, property, textAreaWrapper);
                    })
                }
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

        resetInputOccurrenceElement(occurrence: api.dom.Element) {
            occurrence.getChildren().forEach((child) => {
                if (ObjectHelper.iFrameSafeInstanceOf(child, api.ui.text.TextArea)) {
                    (<api.ui.text.TextArea>child).resetBaseValues();
                }
            })
        }

        private initEditor(id: string, property: Property, textAreaWrapper: Element): void {
            var focusedEditorCls = "html-area-focused";
            var baseUrl = CONFIG.assetsUri;

            var focusHandler = (e) => {
                this.resetInputHeight();
                textAreaWrapper.addClass(focusedEditorCls);

                this.notifyFocused(e);

                api.util.AppHelper.dispatchCustomEvent("focusin", this);
                new api.ui.selector.SelectorOnBlurEvent(this).fire();
            };

            var notifyValueChanged = this.notifyValueChanged.bind(this, id, textAreaWrapper);

            var isMouseOverRemoveOccurenceButton = false;

            var blurHandler = (e) => {
                //checking if remove occurence button clicked or not
                api.util.AppHelper.dispatchCustomEvent("focusout", this);

                if (!isMouseOverRemoveOccurenceButton) {
                    this.setStaticInputHeight();
                    textAreaWrapper.removeClass(focusedEditorCls);
                }
                this.notifyBlurred(e);
            };

            var keydownHandler = (e) => {
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
                } else if ((e.altKey) && e.keyCode === 9) {  // alt+tab for OSX
                    e.preventDefault();

                    let htmlAreaIframe = wemjq(textAreaWrapper.getHTMLElement()).find("iframe").get(0); // the one that event is triggered from
                    let activeElement = this.isNotActiveElement(htmlAreaIframe) ? htmlAreaIframe : <HTMLElement>document.activeElement; // check if focused element is html area that triggered event
                    let nextFocusable = api.dom.FormEl.getNextFocusable(api.dom.Element.fromHtmlElement(activeElement),
                        "iframe, input, select");

                    if (nextFocusable) {
                        if (this.isIframe(nextFocusable)) { // if iframe is next focusable then it is a html area and using it's own focus method
                            let nextId = nextFocusable.getId().replace("_ifr", "");
                            this.getEditor(nextId).focus();
                        }
                        else {
                            nextFocusable.giveFocus();
                        }
                    }
                }
            };

            var createDialogHandler = event => {
                api.util.htmlarea.dialog.HTMLAreaDialogHandler.createAndOpenDialog(event);
                textAreaWrapper.addClass(focusedEditorCls);
            };

            new HTMLAreaBuilder().setSelector('textarea.' + id.replace(/\./g, '_')).setAssetsUri(baseUrl).setInline(false).onCreateDialog(
                createDialogHandler).setFocusHandler(focusHandler.bind(this)).setBlurHandler(blurHandler.bind(this)).setKeydownHandler(
                keydownHandler).setKeyupHandler(notifyValueChanged).setNodeChangeHandler(
                notifyValueChanged).setContentPath(this.contentPath).setContent(this.content).setApplicationKeys(
                this.applicationKeys).setTools(this.tools).setCanSeeCode(this.canSeeCode).createEditor().then((editor: HtmlAreaEditor) => {
                this.setEditorContent(id, property);
                if (this.notInLiveEdit()) {
                    this.setupStickyEditorToolbarForInputOccurence(textAreaWrapper, id);
                }
                this.removeTooltipFromEditorArea(textAreaWrapper);

                var removeButtonEL = wemjq(textAreaWrapper.getParentElement().getParentElement().getHTMLElement()).find(
                    ".remove-button")[0];
                removeButtonEL.addEventListener("mouseover", () => {
                    isMouseOverRemoveOccurenceButton = true;
                });
                removeButtonEL.addEventListener("mouseleave", () => {
                    isMouseOverRemoveOccurenceButton = false;
                });

                this.onShown((event) => {
                    // invoke auto resize on shown in case contents have been updated while inactive
                    if (!!editor['contentAreaContainer'] || !!editor['bodyElement']) {
                        editor.execCommand('mceAutoResize', false, null, {skip_focus: true});
                    }
                });
            });
        }

        private setFocusOnEditorAfterCreate(inputOccurence: Element, id: string): void {
            inputOccurence.giveFocus = () => {
                var editor = this.getEditor(id);
                if (editor) {
                    editor.focus();
                    return true;
                } else {
                    return false;
                }
            };
        }

        private setupStickyEditorToolbarForInputOccurence(inputOccurence: Element, editorId: string) {
            var scrollHandler = api.util.AppHelper.debounce((e) => {
                this.updateStickyEditorToolbar(inputOccurence, this.getEditorInfo(editorId));
            }, 20, false);

            wemjq(this.getHTMLElement()).closest(".form-panel").on("scroll", (event) => {
                scrollHandler();
            });

            api.ui.responsive.ResponsiveManager.onAvailableSizeChanged(this, () => {
                this.updateEditorToolbarPos(inputOccurence);
                this.updateEditorToolbarWidth(inputOccurence, this.getEditorInfo(editorId));
            });

            this.onRemoved((event) => {
                api.ui.responsive.ResponsiveManager.unAvailableSizeChanged(this);
            });

            this.onOccurrenceRendered(() => {
                this.resetInputHeight();
            });

            this.onOccurrenceRemoved(() => {
                this.resetInputHeight();
            });
        }

        private updateStickyEditorToolbar(inputOccurence: Element, editorInfo: HtmlAreaOccurrenceInfo) {
            if (!this.editorTopEdgeIsVisible(inputOccurence) && this.editorLowerEdgeIsVisible(inputOccurence)) {
                if (!editorInfo.hasStickyToolbar) {
                    editorInfo.hasStickyToolbar = true;
                    inputOccurence.addClass("sticky-toolbar");
                    this.updateEditorToolbarWidth(inputOccurence, editorInfo);
                }
                this.updateEditorToolbarPos(inputOccurence);
            }
            else if (editorInfo.hasStickyToolbar) {
                editorInfo.hasStickyToolbar = false;
                inputOccurence.removeClass("sticky-toolbar");
                this.updateEditorToolbarWidth(inputOccurence, editorInfo);
            }
        }

        private updateEditorToolbarPos(inputOccurence: Element) {
            wemjq(inputOccurence.getHTMLElement()).find(".mce-toolbar-grp").css({top: this.getToolbarOffsetTop(1)});
        }

        private updateEditorToolbarWidth(inputOccurence: Element, editorInfo: HtmlAreaOccurrenceInfo) {
            if (editorInfo.hasStickyToolbar) {
                // Toolbar in sticky mode has position: fixed which makes it not
                // inherit width of its parent, so we have to explicitly set width 
                wemjq(inputOccurence.getHTMLElement()).find(".mce-toolbar-grp").width(inputOccurence.getEl().getWidth() - 3);
            }
            else {
                wemjq(inputOccurence.getHTMLElement()).find(".mce-toolbar-grp").width('auto');
            }
        }

        private editorTopEdgeIsVisible(inputOccurence: Element): boolean {
            return this.calcDistToTopOfScrlbleArea(inputOccurence) > 0;
        }

        private editorLowerEdgeIsVisible(inputOccurence: Element): boolean {
            var distToTopOfScrlblArea = this.calcDistToTopOfScrlbleArea(inputOccurence);
            var editorToolbarHeight = wemjq(inputOccurence.getHTMLElement()).find(".mce-toolbar-grp").outerHeight(true);
            var mceStatusToolbarHeight = wemjq(inputOccurence.getHTMLElement()).find(".mce-statusbar").outerHeight(true);
            return (inputOccurence.getEl().getHeightWithoutPadding() - editorToolbarHeight - mceStatusToolbarHeight +
                    distToTopOfScrlblArea) > 0;
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
            if (editor) {
                editor.setContent(property.hasNonNullValue() ? HTMLAreaHelper.prepareImgSrcsInValueForEdit(property.getString()) : "");
                HTMLAreaHelper.updateImageAlignmentBehaviour(editor);
            } else {
                console.log("Editor with id '" + editorId + "' not found")
            }
        }

        private notInLiveEdit(): boolean {
            return !(wemjq(this.getHTMLElement()).parents(".inspection-panel").length > 0);
        }

        private notifyValueChanged(id: string, occurrence: api.dom.Element) {
            var value = ValueTypes.STRING.newValue(HTMLAreaHelper.prepareEditorImageSrcsBeforeSave(this.getEditor(id)));
            this.notifyOccurrenceValueChanged(occurrence, value);
        }

        private newValue(s: string): Value {
            return new Value(s, ValueTypes.STRING);
        }

        private isNotActiveElement(htmlAreaIframe: HTMLElement): boolean {
            let activeElement = wemjq(document.activeElement).get(0);

            return htmlAreaIframe != activeElement;
        }

        private isIframe(element: Element): boolean {
            return element.getEl().getTagName().toLowerCase() === "iframe";
        }

        valueBreaksRequiredContract(value: Value): boolean {
            return value.isNull() || !value.getType().equals(ValueTypes.STRING) || api.util.StringHelper.isBlank(value.getString());
        }

        hasInputElementValidUserInput(inputElement: api.dom.Element) {

            // TODO
            return true;
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

        onFocus(listener: (event: FocusEvent) => void) {
            this.focusListeners.push(listener);
        }

        unFocus(listener: (event: FocusEvent) => void) {
            this.focusListeners = this.focusListeners.filter((curr) => {
                return curr !== listener;
            });
        }

        onBlur(listener: (event: FocusEvent) => void) {
            this.blurListeners.push(listener);
        }

        unBlur(listener: (event: FocusEvent) => void) {
            this.blurListeners = this.blurListeners.filter((curr) => {
                return curr !== listener;
            });
        }

        private notifyFocused(event: FocusEvent) {
            this.focusListeners.forEach((listener) => {
                listener(event);
            })
        }

        private notifyBlurred(event: FocusEvent) {
            this.blurListeners.forEach((listener) => {
                listener(event);
            })
        }


        private destroyEditor(id: string): void {
            var editor = this.getEditor(id)
            if (editor) {
                try {
                    editor.destroy(false);
                }
                catch (e) {
                    //error thrown in FF on tab close - XP-2624
                }
            }
        }

        private reInitEditor(id: string) {
            var savedEditor: HtmlAreaOccurrenceInfo = this.getEditorInfo(id);

            if (!!savedEditor) {
                this.initEditor(id, savedEditor.property, savedEditor.textAreaWrapper);
            }
        }

        private getEditorInfo(id: string): HtmlAreaOccurrenceInfo {
            return api.util.ArrayHelper.findElementByFieldValue(this.editors, "id", id);
        }

    }

    export interface HtmlAreaOccurrenceInfo {
        id: string;
        textAreaWrapper: Element;
        property: Property;
        hasStickyToolbar: boolean;
    }

    api.form.inputtype.InputTypeManager.register(new api.Class("HtmlArea", HtmlArea));
}