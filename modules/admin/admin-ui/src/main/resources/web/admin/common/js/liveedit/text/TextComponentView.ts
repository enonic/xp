module api.liveedit.text {

    declare var CONFIG;

    import ComponentView = api.liveedit.ComponentView;
    import RegionView = api.liveedit.RegionView;
    import TextComponent = api.content.page.region.TextComponent;

    import LinkModalDialog = api.util.htmlarea.dialog.LinkModalDialog;
    import AnchorModalDialog = api.util.htmlarea.dialog.AnchorModalDialog;
    import HtmlAreaAnchor = api.util.htmlarea.dialog.HtmlAreaAnchor;
    import HTMLAreaBuilder = api.util.htmlarea.editor.HTMLAreaBuilder;
    import HTMLAreaHelper = api.util.htmlarea.editor.HTMLAreaHelper;
    import ModalDialog = api.util.htmlarea.dialog.ModalDialog;

    export class TextComponentViewBuilder extends ComponentViewBuilder<TextComponent> {
        constructor() {
            super();
            this.setType(TextItemType.get());
        }
    }

    export class TextComponentView extends ComponentView<TextComponent> {

        private textComponent: TextComponent;

        private rootElement: api.dom.Element;

        private htmlAreaEditor;

        private initializingTinyMceEditor: boolean;

        private focusOnInit: boolean;

        public static debug = false;

        private static DEFAULT_TEXT: string = "<h2>Text</h2>";

        // special handling for click to allow dblclick event without triggering 2 clicks before it
        public static DBL_CLICK_TIMEOUT = 250;
        private singleClickTimer: number;
        private lastClicked: number;

        private modalDialog: ModalDialog;

        constructor(builder: TextComponentViewBuilder) {

            this.lastClicked = 0;
            this.liveEditModel = builder.parentRegionView.getLiveEditModel();
            this.textComponent = builder.component;
            this.initializingTinyMceEditor = false;

            super(builder.
                setContextMenuActions(this.createTextContextMenuActions()).
                setPlaceholder(new TextPlaceholder()).
                setTooltipViewer(new TextComponentViewer()));

            this.addClassEx('text-view');

            this.initializeRootElement();

            this.rootElement.getHTMLElement().onpaste = this.handlePasteEvent.bind(this);

            this.onAdded(() => {
                //TODO: this seems to be never triggered because element is already in DOM when parsed!!!
                this.focusOnInit = true;
                this.addClass("editor-focused");
                if (!this.htmlAreaEditor && !this.initializingTinyMceEditor) {
                    this.initEditor();
                }
            });

            this.getPageView().appendContainerForTextToolbar();


            this.onRemoved(() => {
                this.destroyEditor();
            });
        }

        private getContentId(): api.content.ContentId {
            return this.getPageView().getLiveEditModel().getContent().getContentId();
        }

        private isAllTextSelected(): boolean {
            this.htmlAreaEditor.selection.getContent() == this.htmlAreaEditor.getContent();
            return this.rootElement.getHTMLElement().innerText.trim() == window['getSelection']().toString();
        }

        private handlePasteEvent(event) {
            if (this.isAllTextSelected()) {
                this.rootElement.getHTMLElement().innerHTML = "";
            }
        }

        showTooltip() {
            if (!this.isEditMode()) {
                super.showTooltip();
            }
        }

        hideTooltip(hideParentTooltip: boolean = true) {
            if (!this.isEditMode()) {
                super.hideTooltip(hideParentTooltip);
            }
        }

        highlight() {
            var isDragging = DragAndDrop.get().isDragging();
            if (!this.isEditMode() && !isDragging) {
                super.highlight();
            }
        }

        unhighlight() {
            if (!this.isEditMode()) {
                super.unhighlight();
            }
        }

        private initializeRootElement() {
            for (var i = 0; i < this.getChildren().length; i++) {
                var child = this.getChildren()[i];
                if (child.getEl().getTagName().toUpperCase() == 'SECTION') {
                    this.rootElement = child;
                    // convert image urls in text component for web
                    child.setHtml(HTMLAreaHelper.prepareImgSrcsInValueForEdit(child.getHtml()), false);
                    break;
                }
            }
            if (!this.rootElement) {
                // create it in case of new component
                this.rootElement = new api.dom.SectionEl();
                this.prependChild(this.rootElement);
            }
        }

        isEmpty(): boolean {
            return !this.textComponent || this.textComponent.isEmpty();
        }

        private doHandleDbClick(event: MouseEvent) {
            if (this.isEditMode() && this.isActive()) {
                return;
            }

            this.focusOnInit = true;
            this.startPageTextEditMode();
            if (!!this.htmlAreaEditor) {
                this.htmlAreaEditor.focus();
            }
            api.liveedit.Highlighter.get().hide();
        }

        private doHandleClick(event: MouseEvent) {
            if (this.isEditMode()) {
                if (this.isActive()) {
                    return;
                }
                if (!!this.htmlAreaEditor) {
                    this.htmlAreaEditor.focus();
                }
                return;
            }

            super.handleClick(event);
        }

        handleClick(event: MouseEvent) {
            if (TextComponentView.debug) {
                console.group('Handling click [' + this.getId() + '] at ' + new Date().getTime());
                console.log(event);
            }

            event.stopPropagation();
            if (event.which == 3) { // right click
                event.preventDefault();
            }

            if (this.isEditMode() && this.isActive()) {
                if (TextComponentView.debug) {
                    console.log('Is in text edit mode, not handling click');
                    console.groupEnd();
                }
                return;
            }

            var timeSinceLastClick = new Date().getTime() - this.lastClicked;

            if (timeSinceLastClick > TextComponentView.DBL_CLICK_TIMEOUT) {
                this.singleClickTimer = setTimeout(() => {
                    if (TextComponentView.debug) {
                        console.log('no dblclick occured during ' + TextComponentView.DBL_CLICK_TIMEOUT + 'ms, notifying click', this);
                        console.groupEnd();
                    }

                    this.doHandleClick(event);
                }, TextComponentView.DBL_CLICK_TIMEOUT);

            } else {

                if (TextComponentView.debug) {
                    console.log('dblclick occured after ' + timeSinceLastClick + 'ms, notifying dbl click', this);
                    // end the group started by the first click first
                    console.groupEnd();
                    console.groupEnd();
                }
                clearTimeout(this.singleClickTimer);
                this.doHandleDbClick(event);
            }
            this.lastClicked = new Date().getTime();
        }

        isEditMode(): boolean {
            return this.hasClass('edit-mode');
        }

        isActive(): boolean {
            return this.hasClass('active');
        }

        setEditMode(flag: boolean) {
            if (!flag) {
                if (this.htmlAreaEditor) {
                    this.processEditorValue();
                }
                this.removeClass("editor-focused");
            }

            this.toggleClass('edit-mode', flag);
            this.setDraggable(!flag);

            if (flag) {
                this.hideTooltip();

                if (!this.htmlAreaEditor && !this.initializingTinyMceEditor) {
                    this.initEditor();
                }

                if (this.textComponent.isEmpty()) {
                    if (!!this.htmlAreaEditor) {
                        this.htmlAreaEditor.setContent(TextComponentView.DEFAULT_TEXT);
                    }
                    this.rootElement.setHtml(TextComponentView.DEFAULT_TEXT, false);
                    this.selectText();
                }
            }
        }

        private onFocusHandler(e) {
            this.addClass("editor-focused");
        }

        private onBlurHandler(e) {
            this.removeClass("editor-focused");

            setTimeout(() => {
                if (!this.anyEditorHasFocus()) {
                    this.closePageTextEditMode();
                }
            }, 100);
        }

        private onKeydownHandler(e) {
            if (e.keyCode == 27) { // esc
                this.closePageTextEditMode();
                this.removeClass("editor-focused");
            }
        }

        private initEditor(): void {
            this.initializingTinyMceEditor = true;
            var assetsUri = CONFIG.assetsUri,
                id = this.getId().replace(/\./g, '_');

            this.addClass(id);
            this.appendChild(new api.dom.DivEl("tiny-mce-here"));

            new HTMLAreaBuilder().
                setSelector('div.' + id + ' .tiny-mce-here').
                setAssetsUri(assetsUri).
                setInline(true).
                onDialogShown(dialog => this.modalDialog = dialog).
                onDialogHidden(dialog => this.modalDialog = undefined).
                setOnFocusHandler(this.onFocusHandler.bind(this)).
                setOnBlurHandler(this.onBlurHandler.bind(this)).
                setOnKeydownHandler(this.onKeydownHandler.bind(this)).
                setFixedToolbarContainer('.mce-toolbar-container').
                setContentId(this.getContentId()).
                setUseInsertImage(true). // uncomment to enable inserting images
                createEditor().
                then((editor: HtmlAreaEditor) => {
                    this.htmlAreaEditor = editor;
                    debugger;
                    if (!!this.textComponent.getText()) {
                        this.htmlAreaEditor.setContent(HTMLAreaHelper.prepareImgSrcsInValueForEdit(this.textComponent.getText()));
                    } else {
                        this.htmlAreaEditor.setContent(TextComponentView.DEFAULT_TEXT);
                        this.htmlAreaEditor.selection.select(this.htmlAreaEditor.getBody(), true);
                    }
                    if (this.focusOnInit) {
                        this.htmlAreaEditor.focus();
                        wemjq(this.htmlAreaEditor.getElement()).simulate("click");
                    }
                    this.focusOnInit = false;
                    this.initializingTinyMceEditor = false;
                });
        }

        private anyEditorHasFocus(): boolean {
            var textItemViews = this.getPageView().getItemViewsByType(api.liveedit.text.TextItemType.get());

            var editorFocused = textItemViews.some((view: ItemView) => {
                return view.getEl().hasClass("editor-focused");
            });

            var dialogVisible = !!this.modalDialog && this.modalDialog.isVisible();

            return editorFocused || dialogVisible;
        }

        private processEditorValue() {
            debugger;
            if (this.isEditorEmpty()) {
                this.textComponent.setText(TextComponentView.DEFAULT_TEXT);
                // copy editor content over to the root html element
                this.rootElement.getHTMLElement().innerHTML = TextComponentView.DEFAULT_TEXT;
            } else {
                var editorContent = HTMLAreaHelper.prepareEditorImageSrcsBeforeSave(this.htmlAreaEditor);
                this.textComponent.setText(editorContent);
                // copy editor raw content (without any processing!) over to the root html element
                this.rootElement.getHTMLElement().innerHTML = this.htmlAreaEditor.getContent({format : 'raw'});
            }
        }

        private isEditorEmpty(): boolean {
            var editorContent = this.htmlAreaEditor.getContent();
            return editorContent.trim() === "" || editorContent == "<h2>&nbsp;</h2>";
        }

        private destroyEditor(): void {
            var editor = this.htmlAreaEditor;
            if (editor) {
                try {
                    editor.destroy(false);
                }
                catch (e) {
                    //error thrown in FF on tab close - XP-2624
                }
            }
        }

        private selectText() {
            if (!!this.htmlAreaEditor) {
                this.htmlAreaEditor.selection.select(this.htmlAreaEditor.getBody(), true);
            }
        }

        private startPageTextEditMode() {
            this.deselect();
            var pageView = this.getPageView();
            if (!pageView.isTextEditMode()) {
                pageView.setTextEditMode(true);
            }
            this.giveFocus();
        }

        private closePageTextEditMode() {
            var pageView = this.getPageView();
            if (pageView.isTextEditMode()) {
                pageView.setTextEditMode(false);
            }
        }

        giveFocus() {
            if (!this.isEditMode()) {
                return false;
            }
            return this.rootElement.giveFocus();
        }

        private createTextContextMenuActions(): api.ui.Action[] {
            var actions: api.ui.Action[] = [];
            actions.push(new api.ui.Action('Edit').onExecuted(() => {
                this.startPageTextEditMode();
            }));
            return actions;
        }
    }
}