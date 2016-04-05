module api.util.htmlarea.editor {

    import ModalDialog = api.util.htmlarea.dialog.ModalDialog;
    import AnchorModalDialog = api.util.htmlarea.dialog.AnchorModalDialog;
    import ImageModalDialog = api.util.htmlarea.dialog.ImageModalDialog;
    import LinkModalDialog = api.util.htmlarea.dialog.LinkModalDialog;
    import HtmlAreaAnchor = api.util.htmlarea.dialog.HtmlAreaAnchor;
    import HtmlAreaImage = api.util.htmlarea.dialog.HtmlAreaImage;

    import CreateHtmlAreaDialogEvent = api.util.htmlarea.dialog.CreateHtmlAreaDialogEvent;

    export class HTMLAreaBuilder {

        private contentId: api.content.ContentId; // used for image dialog

        private assetsUri: string;
        private selector: string;
        private onFocusHandler: (e) => void;
        private onBlurHandler: (e) => void;
        private onKeydownHandler: (e) => void;
        private onNodeChangeHandler: (e) => void;
        private createDialogListeners: {(event: CreateHtmlAreaDialogEvent): void}[] = [];
        private inline: boolean = false;
        private fixedToolbarContainer: string;
        private convertUrls: boolean = false;

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

        setOnFocusHandler(onFocusHandler: (e) => void): HTMLAreaBuilder {
            this.onFocusHandler = onFocusHandler;
            return this;
        }

        setOnBlurHandler(onBlurHandler: (e) => void): HTMLAreaBuilder {
            this.onBlurHandler = onBlurHandler;
            return this;
        }

        setOnKeydownHandler(onKeydownHandler: (e) => void): HTMLAreaBuilder {
            this.onKeydownHandler = onKeydownHandler;
            return this;
        }

        setOnNodeChangeHandler(onChangeHandler: (e) => void): HTMLAreaBuilder {
            this.onNodeChangeHandler = onChangeHandler;
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

        setContentId(contentId: api.content.ContentId): HTMLAreaBuilder {
            this.contentId = contentId;
            return this;
        }

        setConvertUrls(convertUrls: boolean): HTMLAreaBuilder {
            this.convertUrls = convertUrls;
            return this;
        }

        private checkRequiredFieldsAreSet() {
            if (!this.assetsUri || !this.selector || !this.contentId) {
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
                    "styleselect | cut copy pastetext | bullist numlist outdent indent | charmap anchor image link unlink | table | code"
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
                plugins: ['autoresize', 'table', 'paste', 'charmap', 'code'],
                external_plugins: {
                    "link": this.assetsUri + "/common/js/util/htmlarea/plugins/link.js",
                    "anchor": this.assetsUri + "/common/js/util/htmlarea/plugins/anchor.js",
                    "image": this.assetsUri + "/common/js/util/htmlarea/plugins/image.js"
                },
                object_resizing: "table",
                autoresize_min_height: 100,
                autoresize_bottom_margin: 0,

                setup: (editor) => {
                    editor.addCommand("openLinkDialog", this.notifyLinkDialog, this);
                    editor.addCommand("openAnchorDialog", this.notifyAnchorDialog, this);
                    editor.addCommand("openImageDialog", this.notifyImageDialog, this) ;
                    editor.on('NodeChange', (e) => {
                        if (!!this.onNodeChangeHandler) {
                            this.onNodeChangeHandler(e);
                        }
                    });
                    editor.on('focus', (e) => {
                        if (!!this.onFocusHandler) {
                            this.onFocusHandler(e);
                        }
                    });
                    editor.on('blur', (e) => {
                        if (!!this.onBlurHandler) {
                            this.onBlurHandler(e);
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

                        if (!!this.onKeydownHandler) {
                            this.onKeydownHandler(e);
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
                setConfig(config).
                setType(api.util.htmlarea.dialog.HtmlAreaDialogType.LINK).
                build();
            this.publishCreateDialogEvent(event);
        }

        private notifyImageDialog(config) {
            let event = CreateHtmlAreaDialogEvent.create().
                setConfig(config).
                setType(api.util.htmlarea.dialog.HtmlAreaDialogType.IMAGE).
                setContentId(this.contentId).
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

        private publishCreateDialogEvent(event: CreateHtmlAreaDialogEvent) {
            this.notifyCreateDialog(event);
            event.fire();
        }
    }
}