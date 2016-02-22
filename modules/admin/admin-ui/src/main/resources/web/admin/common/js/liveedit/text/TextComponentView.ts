module api.liveedit.text {

    declare var CONFIG;

    import ComponentView = api.liveedit.ComponentView;
    import RegionView = api.liveedit.RegionView;
    import TextComponent = api.content.page.region.TextComponent;

    import LinkModalDialog = api.util.htmlarea.dialog.LinkModalDialog;
    import AnchorModalDialog = api.util.htmlarea.dialog.AnchorModalDialog;
    import HtmlAreaAnchor = api.util.htmlarea.dialog.HtmlAreaAnchor;
    import TinymceEditorBuilder = api.util.htmlarea.editor.TinymceEditorBuilder;
    import TinymceContentHelper = api.util.htmlarea.editor.TinymceContentHelper;

    export class TextComponentViewBuilder extends ComponentViewBuilder<TextComponent> {
        constructor() {
            super();
            this.setType(TextItemType.get());
        }
    }

    export class TextComponentView extends ComponentView<TextComponent> {

        private textComponent: TextComponent;

        private rootElement: api.dom.Element;

        private tinyMceEditor;

        private initializingTinyMceEditor: boolean;

        private focusOnInit: boolean;

        public static debug = false;

        private static DEFAULT_TEXT: string = "<h2>Text</h2>";

        // special handling for click to allow dblclick event without triggering 2 clicks before it
        public static DBL_CLICK_TIMEOUT = 250;
        private singleClickTimer: number;
        private lastClicked: number;

        private modalDialog:api.util.htmlarea.dialog.ModalDialog;

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
                this.focusOnInit = true;
                this.addClass("editor-focused");
                if (!this.tinyMceEditor && !this.initializingTinyMceEditor) {
                    this.initEditor();
                }
            });

            this.getPageView().appendContainerForTextToolbar();


            this.onRemoved(() => {
                this.destroyEditor();
            });
        }

        private getContentId():api.content.ContentId {
            return this.getPageView().getLiveEditModel().getContent().getContentId();
        }

        private isAllTextSelected(): boolean {
            this.tinyMceEditor.selection.getContent() == this.tinyMceEditor.getContent();
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
            if (!!this.tinyMceEditor) {
                this.tinyMceEditor.focus();
            }
            api.liveedit.Highlighter.get().hide();
        }

        private doHandleClick(event: MouseEvent) {
            if (this.isEditMode()) {
                if (this.isActive()) {
                    return;
                }
                if (!!this.tinyMceEditor) {
                    this.tinyMceEditor.focus();
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
                if (this.tinyMceEditor) {
                    this.processMCEValue();
                    this.closePageTextEditMode();
                }
                this.removeClass("editor-focused");
            }

            this.toggleClass('edit-mode', flag);
            this.setDraggable(!flag);

            if (flag) {
                this.hideTooltip();

                if (!this.tinyMceEditor && !this.initializingTinyMceEditor) {
                    this.initEditor();
                }

                if (this.textComponent.isEmpty()) {
                    if (!!this.tinyMceEditor) {
                        this.tinyMceEditor.setContent(TextComponentView.DEFAULT_TEXT);
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
            this.processMCEValue();
            this.removeClass("editor-focused");

            setTimeout(() => {
                if (!this.anyEditorHasFocus()) {
                    this.setEditMode(false);
                }
            }, 100);
        }

        private onKeydownHandler(e) {
            if (e.keyCode == 27) { // esc
                this.processMCEValue();
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

            new TinymceEditorBuilder().
                setSelector('div.' + id + ' .tiny-mce-here').
                setAssetsUri(assetsUri).
                setInline(true).
                setOnFocusHandler(this.onFocusHandler.bind(this)).
                setOnBlurHandler(this.onBlurHandler.bind(this)).
                setOnKeydownHandler(this.onKeydownHandler.bind(this)).
                setFixedToolbarContainer('.mce-toolbar-container').
                setContentId(this.getContentId()).
                setUseInsertImage(false). // uncomment to enable inserting images
                createEditor().
                then((editor:HtmlAreaEditor) => {
                    this.tinyMceEditor = editor;
                    if (!!this.textComponent.getText()) {
                        this.tinyMceEditor.setContent(TinymceContentHelper.prepareImgSrcsInValueForEdit(this.textComponent.getText()));
                    } else {
                        this.tinyMceEditor.setContent(TextComponentView.DEFAULT_TEXT);
                        this.tinyMceEditor.selection.select(this.tinyMceEditor.getBody(), true);
                    }
                    if (this.focusOnInit) {
                        this.tinyMceEditor.focus();
                        wemjq(this.tinyMceEditor.getElement()).simulate("click");
                    }
                    this.focusOnInit = false;
                    this.initializingTinyMceEditor = false;
                });
        }

        private anyEditorHasFocus(): boolean {
            var textItemViews = this.getPageView().getItemViewsByType(api.liveedit.text.TextItemType.get());
            var result: boolean = false;

            var textView: api.liveedit.text.TextComponentView;
            textItemViews.forEach((view: ItemView) => {
                textView = <api.liveedit.text.TextComponentView> view;
                if (textView.getEl().hasClass("editor-focused")) {
                    result = true;
                    return;
                }
            });

            return result;
        }

        private openLinkDialog(config:HtmlAreaAnchor) {
            this.modalDialog = new LinkModalDialog(config);
            this.modalDialog.open();
        }

        private openAnchorDialog(editor: HtmlAreaEditor) {
            this.modalDialog = new AnchorModalDialog(editor);
            this.modalDialog.open();
        }

        private processMCEValue() {
            if (this.isMceEditorEmpty()) {
                this.textComponent.setText(TextComponentView.DEFAULT_TEXT);
                this.rootElement.getHTMLElement().innerHTML = TextComponentView.DEFAULT_TEXT;
            } else {
                var editorContent = TinymceContentHelper.prepareEditorImageSrcsBeforeSave(this.tinyMceEditor);
                this.textComponent.setText(editorContent);
                this.rootElement.getHTMLElement().innerHTML = editorContent;
            }
        }

        private isMceEditorEmpty(): boolean {
            var editorContent = this.tinyMceEditor.getContent();
            return editorContent.trim() === "" || editorContent == "<h2>&nbsp;</h2>";
        }

        private destroyEditor(): void {
            var editor = this.tinyMceEditor;
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
            if (!!this.tinyMceEditor) {
                this.tinyMceEditor.selection.select(this.tinyMceEditor.getBody(), true);
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