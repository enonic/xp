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

        private isInitializingEditor: boolean;

        private focusOnInit: boolean;

        private editorContainer: api.dom.DivEl;

        public static debug = false;

        private static DEFAULT_TEXT: string = "";

        private static EDITOR_FOCUSED_CLASS: string = "editor-focused";

        // special handling for click to allow dblclick event without triggering 2 clicks before it
        public static DBL_CLICK_TIMEOUT = 250;
        private singleClickTimer: number;
        private lastClicked: number;

        private modalDialog: ModalDialog;
        private currentDialogConfig;

        constructor(builder: TextComponentViewBuilder) {

            this.lastClicked = 0;
            this.liveEditModel = builder.parentRegionView.getLiveEditModel();
            this.textComponent = builder.component;
            this.isInitializingEditor = false;

            super(builder.
                setContextMenuActions(this.createTextContextMenuActions()).
                setPlaceholder(new TextPlaceholder()).
                setViewer(new TextComponentViewer()).
                setComponent(this.textComponent));

            this.addClassEx('text-view');

            this.initializeRootElement();

            this.rootElement.getHTMLElement().onpaste = this.handlePasteEvent.bind(this);

            this.onAdded(() => { // is triggered on item insert or move
                if (api.BrowserHelper.isFirefox() && !!tinymce.activeEditor) {
                    tinymce.activeEditor.fire('blur');
                }
                this.focusOnInit = true;
                this.addClass(TextComponentView.EDITOR_FOCUSED_CLASS);
                if (!this.htmlAreaEditor && !this.isInitializingEditor) {
                    this.initEditor();
                } else if (!!this.htmlAreaEditor) {
                    this.reInitEditor(); // on added, inline editor losses its root element of the editable area
                }
            });

            this.getPageView().appendContainerForTextToolbar();

            this.onRemoved(() => {
                this.destroyEditor();
            });

            var handleDialogCreated = (event) => {
                if (this.currentDialogConfig == event.getConfig()) {
                    this.modalDialog = event.getModalDialog();
                }
            };

            this.onMouseLeave(() => {
                if (this.getEl().hasClass(TextComponentView.EDITOR_FOCUSED_CLASS)) {
                    this.processEditorValue();
                }
            });

            api.liveedit.LiveEditPageDialogCreatedEvent.on(handleDialogCreated.bind(this));
        }

        private reInitEditor() {
            this.destroyEditor();
            this.editorContainer.remove();
            this.editorContainer = null;
            this.htmlAreaEditor = null;
        }

        private getContentId(): api.content.ContentId {
            return this.liveEditModel.getContent().getContentId();
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
                this.addClass(TextComponentView.EDITOR_FOCUSED_CLASS);
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
                this.removeClass(TextComponentView.EDITOR_FOCUSED_CLASS);
                if (api.BrowserHelper.isFirefox()) {
                    var activeEditor = tinymce.activeEditor;
                    if (!!activeEditor) {
                        activeEditor.fire('blur');
                    }
                }
            }

            this.toggleClass('edit-mode', flag);
            this.setDraggable(!flag);

            if (flag) {
                if (!this.htmlAreaEditor && !this.isInitializingEditor) {
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
            this.addClass(TextComponentView.EDITOR_FOCUSED_CLASS);
        }

        private onBlurHandler(e) {
            this.removeClass(TextComponentView.EDITOR_FOCUSED_CLASS);

            setTimeout(() => {
                if (!this.anyEditorHasFocus()) {
                    this.closePageTextEditMode();
                }
            }, 50);
        }

        private onKeydownHandler(e) {
            var saveShortcut = (e.keyCode == 83 && (e.ctrlKey || e.metaKey));

            if (saveShortcut) { //Cmd-S
                this.processEditorValue();
            }

            if (e.keyCode == 27 || saveShortcut) { // esc or Cmd-S
                this.closePageTextEditMode();
                this.removeClass(TextComponentView.EDITOR_FOCUSED_CLASS);
            }
        }

        private initEditor(): void {
            this.isInitializingEditor = true;
            var assetsUri = CONFIG.assetsUri,
                id = this.getId().replace(/\./g, '_');

            this.addClass(id);

            if (!this.editorContainer) {
                this.editorContainer = new api.dom.DivEl("tiny-mce-here");
                this.appendChild(this.editorContainer);
            }

            var forceEditorFocus: () => void = () => {
                this.htmlAreaEditor.focus();
                wemjq(this.htmlAreaEditor.getElement()).simulate("click");
            }

            new HTMLAreaBuilder().
                setSelector('div.' + id + ' .tiny-mce-here').
                setAssetsUri(assetsUri).
                setInline(true).
                onCreateDialog(event => {
                    this.currentDialogConfig = event.getConfig();
                }).
                setOnFocusHandler(this.onFocusHandler.bind(this)).
                setOnBlurHandler(this.onBlurHandler.bind(this)).
                setOnKeydownHandler(this.onKeydownHandler.bind(this)).
                setFixedToolbarContainer('.mce-toolbar-container').
                setContentId(this.getContentId()).
                createEditor().
                then((editor: HtmlAreaEditor) => {
                    this.htmlAreaEditor = editor;
                    if (!!this.textComponent.getText()) {
                        this.htmlAreaEditor.setContent(HTMLAreaHelper.prepareImgSrcsInValueForEdit(this.textComponent.getText()));
                    } else {
                        this.htmlAreaEditor.setContent(TextComponentView.DEFAULT_TEXT);
                        this.htmlAreaEditor.selection.select(this.htmlAreaEditor.getBody(), true);
                    }
                    if (this.focusOnInit) {
                        if (api.BrowserHelper.isFirefox()) {
                            setTimeout(() => {
                                forceEditorFocus();
                            }, 100);
                        } else {
                            forceEditorFocus();
                        }
                    }
                    this.focusOnInit = false;
                    this.isInitializingEditor = false;
                    HTMLAreaHelper.updateImageAlignmentBehaviour(editor);
                });
        }

        private anyEditorHasFocus(): boolean {
            var textItemViews = this.getPageView().getItemViewsByType(api.liveedit.text.TextItemType.get());

            var editorFocused = textItemViews.some((view: ItemView) => {
                return view.getEl().hasClass(TextComponentView.EDITOR_FOCUSED_CLASS);
            });

            var dialogVisible = !!this.modalDialog && this.modalDialog.isVisible();

            return editorFocused || dialogVisible;
        }

        private processEditorValue() {
            if (!this.htmlAreaEditor) {
                return;
            }

            if (this.isEditorEmpty()) {
                this.textComponent.setText(TextComponentView.DEFAULT_TEXT);
                // copy editor content over to the root html element
                this.rootElement.getHTMLElement().innerHTML = TextComponentView.DEFAULT_TEXT;
            } else {
                // copy editor raw content (without any processing!) over to the root html element
                this.rootElement.getHTMLElement().innerHTML = this.htmlAreaEditor.getContent({format: 'raw'});
                // but save processed text to the component
                this.textComponent.setText(HTMLAreaHelper.prepareEditorImageSrcsBeforeSave(this.htmlAreaEditor));
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