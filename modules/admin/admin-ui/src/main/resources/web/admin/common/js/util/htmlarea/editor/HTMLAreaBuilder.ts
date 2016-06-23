module api.util.htmlarea.editor {

    import CreateHtmlAreaDialogEvent = api.util.htmlarea.dialog.CreateHtmlAreaDialogEvent;
    import ApplicationKey = api.application.ApplicationKey

    export class HTMLAreaBuilder {

        private content: api.content.ContentSummary; // used for image dialog
        private contentPath: api.content.ContentPath; // used for macro dialog
        private applicationKeys: ApplicationKey[]; // used for macro dialog

        private assetsUri: string;
        private selector: string;
        private focusHandler: (e) => void;
        private blurHandler: (e) => void;
        private keydownHandler: (e) => void;
        private keyupHandler: (e) => void;
        private nodeChangeHandler: (e) => void;
        private createDialogListeners: {(event: CreateHtmlAreaDialogEvent): void}[] = [];
        private inline: boolean = false;
        private fixedToolbarContainer: string;
        private convertUrls: boolean = false;
        private hasActiveDialog: boolean = false;

        setAssetsUri(assetsUri: string): HTMLAreaBuilder {
            this.assetsUri = assetsUri;
            return this;
        }

        setSelector(selector: string): HTMLAreaBuilder {
            this.selector = selector;
            return this;
        }

        onCreateDialog(listener: (event: CreateHtmlAreaDialogEvent) => void) {
            this.createDialogListeners.push(listener);
            return this;
        }

        unCreateDialog(listener: (event: CreateHtmlAreaDialogEvent) => void) {
            this.createDialogListeners = this.createDialogListeners.filter((curr) => {
                return curr !== listener;
            });
            return this;
        }

        private notifyCreateDialog(event: CreateHtmlAreaDialogEvent) {
            this.createDialogListeners.forEach((listener) => {
                listener(event);
            })
        }

        setFocusHandler(focusHandler: (e) => void): HTMLAreaBuilder {
            this.focusHandler = focusHandler;
            return this;
        }

        setBlurHandler(blurHandler: (e) => void): HTMLAreaBuilder {
            this.blurHandler = blurHandler;
            return this;
        }

        setKeydownHandler(keydownHandler: (e) => void): HTMLAreaBuilder {
            this.keydownHandler = keydownHandler;
            return this;
        }

        setKeyupHandler(keyupHandler: (e) => void): HTMLAreaBuilder {
            this.keyupHandler = keyupHandler;
            return this;
        }

        setNodeChangeHandler(nodeChangeHandler: (e) => void): HTMLAreaBuilder {
            this.nodeChangeHandler = nodeChangeHandler;
            return this;
        }

        setInline(inline: boolean): HTMLAreaBuilder {
            this.inline = inline;
            return this;
        }

        setFixedToolbarContainer(fixedToolbarContainer: string): HTMLAreaBuilder {
            this.fixedToolbarContainer = fixedToolbarContainer;
            return this;
        }

        setContent(content: api.content.ContentSummary): HTMLAreaBuilder {
            this.content = content;
            return this;
        }

        setContentPath(contentPath: api.content.ContentPath): HTMLAreaBuilder {
            this.contentPath = contentPath;
            return this;
        }

        setConvertUrls(convertUrls: boolean): HTMLAreaBuilder {
            this.convertUrls = convertUrls;
            return this;
        }

        setApplicationKeys(applicationKeys: ApplicationKey[]): HTMLAreaBuilder {
            this.applicationKeys = applicationKeys;
            return this;
        }

        private checkRequiredFieldsAreSet() {
            if (!this.assetsUri || !this.selector || !this.content) {
                throw new Error("some reruired field(s) is(are) missing for tinymce editor");
            }
        }

        public createEditor(): wemQ.Promise<HtmlAreaEditor> {
            this.checkRequiredFieldsAreSet();

            var deferred = wemQ.defer<HtmlAreaEditor>();

            tinymce.init({
                selector: this.selector,
                document_base_url: this.assetsUri + '/common/lib/tinymce/',
                skin_url: this.assetsUri + '/common/lib/tinymce/skins/lightgray',
                content_css: this.assetsUri + '/common/styles/api/util/htmlarea/html-editor.css',
                theme_url: 'modern',
                inline: this.inline,
                fixed_toolbar_container: this.fixedToolbarContainer,
                convert_urls: this.convertUrls,

                toolbar: [
                    "styleselect | cut copy pastetext | bullist numlist outdent indent | charmap anchor image macro link unlink | table | code"
                ],

                formats: {
                    alignleft: [
                        {
                            selector: 'img,figure,p,h1,h2,h3,h4,h5,h6,td,th,tr,div,ul,ol,li',
                            styles: {textAlign: 'left'},
                            inline: 'span'
                        },
                        {selector: 'table', collapsed: false, styles: {'float': 'left'}}
                    ],
                    aligncenter: [
                        {
                            selector: 'img,figure,p,h1,h2,h3,h4,h5,h6,td,th,tr,div,ul,ol,li',
                            styles: {textAlign: 'center'},
                            inline: 'span'
                        },
                        {selector: 'table', collapsed: false, styles: {marginLeft: 'auto', marginRight: 'auto'}}
                    ],
                    alignright: [
                        {
                            selector: 'img,figure,p,h1,h2,h3,h4,h5,h6,td,th,tr,div,ul,ol,li',
                            styles: {textAlign: 'right'},
                            inline: 'span'
                        },
                        {selector: 'table', collapsed: false, styles: {'float': 'right'}}
                    ],
                    alignjustify: [
                        {
                            selector: 'img,figure,p,h1,h2,h3,h4,h5,h6,td,th,tr,div,ul,ol,li',
                            styles: {textAlign: 'justify'},
                            inline: 'span'
                        }
                    ]
                },
                menubar: false,
                statusbar: false,
                paste_as_text: true,
                browser_spellcheck: true,
                plugins: ['autoresize', 'table', 'paste', 'charmap', 'code'],
                external_plugins: {
                    "link": this.assetsUri + "/common/js/util/htmlarea/plugins/link.js",
                    "anchor": this.assetsUri + "/common/js/util/htmlarea/plugins/anchor.js",
                    "image": this.assetsUri + "/common/js/util/htmlarea/plugins/image.js",
                    "macro": this.assetsUri + "/common/js/util/htmlarea/plugins/macro.js"
                },
                object_resizing: "table",
                autoresize_min_height: 100,
                autoresize_bottom_margin: 0,

                setup: (editor) => {
                    editor.addCommand("openLinkDialog", this.notifyLinkDialog, this);
                    editor.addCommand("openAnchorDialog", this.notifyAnchorDialog, this);
                    editor.addCommand("openImageDialog", this.notifyImageDialog, this) ;
                    editor.addCommand("openMacroDialog", this.notifyMacroDialog, this);
                    editor.on('NodeChange', (e) => {
                        if (!!this.nodeChangeHandler) {
                            this.nodeChangeHandler(e);
                        }
                    });
                    editor.on('keyup', (e) => {
                        if (!!this.keyupHandler) {
                            this.keyupHandler(e);
                        }
                    });
                    editor.on('focus', (e) => {
                        if (!!this.focusHandler) {
                            this.focusHandler(e);
                        }
                    });
                    editor.on('blur', (e) => {
                        if (this.hasActiveDialog) {
                            e.stopImmediatePropagation();
                            this.hasActiveDialog = false;
                        }
                        if (!!this.blurHandler) {
                            this.blurHandler(e);
                        }
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

                        if (!!this.keydownHandler) {
                            this.keydownHandler(e);
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
                    deferred.resolve(editor);
                }
            });
            return deferred.promise;
        }

        private notifyLinkDialog(config) {
            let event = CreateHtmlAreaDialogEvent.create().
                setConfig(config).setType(api.util.htmlarea.dialog.HtmlAreaDialogType.LINK).setContent(this.content).
                build();
            this.publishCreateDialogEvent(event);
        }

        private notifyImageDialog(config) {
            let event = CreateHtmlAreaDialogEvent.create().
                setConfig(config).setType(api.util.htmlarea.dialog.HtmlAreaDialogType.IMAGE).setContent(this.content).
                build();
            this.publishCreateDialogEvent(event);
        }

        private notifyAnchorDialog(config) {
            let event = CreateHtmlAreaDialogEvent.create().
                setConfig(config).
                setType(api.util.htmlarea.dialog.HtmlAreaDialogType.ANCHOR).
                build();
            this.publishCreateDialogEvent(event);
        }

        private notifyMacroDialog(config) {
            let event = CreateHtmlAreaDialogEvent.create().
                setConfig(config).
                setType(api.util.htmlarea.dialog.HtmlAreaDialogType.MACRO).setContentPath(this.contentPath).setApplicationKeys(
                this.applicationKeys).
                build();
            this.publishCreateDialogEvent(event);
        }

        private publishCreateDialogEvent(event: CreateHtmlAreaDialogEvent) {
            this.hasActiveDialog = true;
            this.notifyCreateDialog(event);
            event.fire();
        }
    }
}