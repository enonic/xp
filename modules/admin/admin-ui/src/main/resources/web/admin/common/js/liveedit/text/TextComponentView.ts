module api.liveedit.text {

    declare var CONFIG;

    import ComponentView = api.liveedit.ComponentView;
    import RegionView = api.liveedit.RegionView;
    import TextComponent = api.content.page.region.TextComponent;

    import LinkModalDialog = api.form.inputtype.text.htmlarea.LinkModalDialog;
    import AnchorModalDialog = api.form.inputtype.text.htmlarea.AnchorModalDialog;

    export class TextComponentViewBuilder extends ComponentViewBuilder<TextComponent> {
        constructor() {
            super();
            this.setType(TextItemType.get());
        }
    }

    export class TextComponentView extends ComponentView<TextComponent> {

        private textComponent: TextComponent;

        private rootElement: api.dom.Element;

        private tinyMceEditor: HtmlAreaEditor;

        private initializingTinyMceEditor: boolean;

        public static debug = false;

        private static DEFAULT_TEXT: string = "<h2>Text</h2>";

        // special handling for click to allow dblclick event without triggering 2 clicks before it
        public static DBL_CLICK_TIMEOUT = 250;
        private singleClickTimer: number;
        private lastClicked: number;

        private modalDialog: api.form.inputtype.text.htmlarea.ModalDialog;

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
                this.deactivateAllEditors();
                this.addClass("active");
                if (!this.tinyMceEditor && !this.initializingTinyMceEditor) {
                    this.initEditor();
                }
            });

            this.getPageView().appendContainerForTextToolbar();

            this.onRemoved(() => {
                this.destroyEditor();
            });
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

            this.deactivateAllEditors();
            this.startPageTextEditMode();
            this.setEditorActiveAndFocus();
            api.liveedit.Highlighter.get().hide();
        }

        private doHandleClick(event: MouseEvent) {
            if (this.isEditMode()) {
                if (this.isActive()) {
                    return;
                }
                this.deactivateAllEditors();
                this.setEditorActiveAndFocus();
                return;
            }

            super.handleClick(event);
        }

        private setEditorActiveAndFocus() {
            this.addClass("active");
            if (!!this.tinyMceEditor) {
                this.tinyMceEditor.focus();
            }
        }

        private deactivateAllEditors() {
            var textItemViews = this.getPageView().getItemViewsByType(api.liveedit.text.TextItemType.get());
            textItemViews.forEach((view: ItemView) => {
                view.removeClass("active");
            });
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
                this.removeClass("active");
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

        private initEditor(): void {
            this.initializingTinyMceEditor = true;
            var assetsUri = CONFIG.assetsUri,
                id = this.getId().replace(/\./g, '_');

            this.addClass(id);
            this.appendChild(new api.dom.DivEl("tiny-mce-here"));

            tinymce.init({
                selector: 'div.' + id + ' .tiny-mce-here',
                document_base_url: assetsUri + '/common/lib/tinymce/',
                skin_url: assetsUri + '/common/lib/tinymce/skins/lightgray',
                content_css: assetsUri + '/common/styles/api/form/inputtype/text/tinymce-editor.css',
                theme_url: 'modern',
                inline: true,
                fixed_toolbar_container: '.mce-toolbar-container',

                toolbar: [
                    "styleselect | cut copy pastetext | bullist numlist outdent indent | charmap anchor link unlink | code"
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
                    "link": assetsUri + "/common/js/form/inputtype/text/plugins/link.js",
                    "anchor": assetsUri + "/common/js/form/inputtype/text/plugins/anchor.js"
                },
                object_resizing: "table",
                autoresize_min_height: 100,
                autoresize_bottom_margin: 0,

                setup: (editor) => {
                    editor.addCommand("openLinkDialog", this.openLinkDialog, this);
                    editor.addCommand("openAnchorDialog", this.openAnchorDialog, this);
                    editor.on('change', (e) => {
                    });
                    editor.on('focus', (e) => {
                    });
                    editor.on('blur', (e) => {
                        if (!(this.modalDialog && this.modalDialog.isVisible())) {

                        }
                        this.processMCEValue();
                        this.removeClass("active");
                    });

                    editor.on('keydown', (e) => {

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

                        if (e.keyCode == 27) { // esc
                            this.processMCEValue();
                            this.closePageTextEditMode();
                            this.removeClass("active");
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
                    this.tinyMceEditor = editor;
                    if (!!this.textComponent.getText()) {
                        this.tinyMceEditor.setContent(this.textComponent.getText());
                    } else {
                        this.tinyMceEditor.setContent(TextComponentView.DEFAULT_TEXT);
                        this.tinyMceEditor.selection.select(this.tinyMceEditor.getBody(), true);
                    }
                    this.tinyMceEditor.focus();
                    this.initializingTinyMceEditor = false;
                }
            });
        }

        private openLinkDialog(config: api.form.inputtype.text.HtmlAreaAnchor) {
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
                var editorContent = this.tinyMceEditor.getContent();
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