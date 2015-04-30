/*global tinymce:true */

tinymce.PluginManager.add('link', function (editor) {
    function createLinkList(callback) {
        return function () {
            var linkList = editor.settings.link_list;

            if (typeof linkList == "string") {
                tinymce.util.XHR.send({
                    url: linkList,
                    success: function (text) {
                        callback(tinymce.util.JSON.parse(text));
                    }
                });
            } else if (typeof linkList == "function") {
                linkList(callback);
            } else {
                callback(linkList);
            }
        };
    }

    function buildListItems(inputList, itemCallback, startItems) {
        function appendItems(values, output) {
            output = output || [];

            tinymce.each(values, function (item) {
                var menuItem = {text: item.text || item.title};

                if (item.menu) {
                    menuItem.menu = appendItems(item.menu);
                } else {
                    menuItem.value = item.value;

                    if (itemCallback) {
                        itemCallback(menuItem);
                    }
                }

                output.push(menuItem);
            });

            return output;
        }

        return appendItems(inputList, startItems || []);
    }

    function showDialog(linkList) {
        var data = {}, selection = editor.selection, dom = editor.dom, selectedElm, anchorElm, initialText;
        var win, onlyText, textListCtrl, linkListCtrl, relListCtrl, targetListCtrl, classListCtrl, linkTitleCtrl, value;
        var contentIdPrefix = "content://", targetBlank = "_blank";

        function linkListChangeHandler(e) {
            var textCtrl = win.find('#text');

            if (!textCtrl.value() || (e.lastControl && textCtrl.value() == e.lastControl.text())) {
                textCtrl.value(e.control.text());
            }

            win.find('#href').value(e.control.value());
        }

        function buildAnchorListControl(url) {
            var anchorList = [];

            tinymce.each(editor.dom.select('a:not([href])'), function (anchor) {
                var id = anchor.name || anchor.id;

                if (id) {
                    anchorList.push({
                        text: id,
                        value: '#' + id,
                        selected: url.indexOf('#' + id) != -1
                    });
                }
            });

            if (anchorList.length) {
                anchorList.unshift({text: 'None', value: ''});

                return {
                    name: 'anchor',
                    type: 'listbox',
                    label: 'Anchors',
                    values: anchorList,
                    onselect: linkListChangeHandler
                };
            }
        }

        function updateText() {
            if (!initialText && data.text.length === 0 && onlyText) {
                this.parent().parent().find('#text')[0].value(this.value());
            }
        }

        function urlChange(e) {
            var meta = e.meta || {};

            if (linkListCtrl) {
                linkListCtrl.value(editor.convertURL(this.value(), 'href'));
            }

            tinymce.each(e.meta, function (value, key) {
                win.find('#' + key).value(value);
            });

            if (!meta.text) {
                updateText.call(this);
            }
        }

        function isOnlyTextSelected(anchorElm) {
            var html = selection.getContent();

            // Partial html and not a fully selected anchor element
            if (/</.test(html) && (!/^<a [^>]+>[^<]+<\/a>$/.test(html) || html.indexOf('href=') == -1)) {
                return false;
            }

            if (anchorElm) {
                var nodes = anchorElm.childNodes, i;

                if (nodes.length === 0) {
                    return false;
                }

                for (i = nodes.length - 1; i >= 0; i--) {
                    if (nodes[i].nodeType != 3) {
                        return false;
                    }
                }
            }

            return true;
        }

        selectedElm = selection.getNode();
        anchorElm = dom.getParent(selectedElm, 'a[href]');

        if (!anchorElm && selectedElm.localName == 'p' && (value = selectedElm.lastElementChild)) {
            if (value.localName == 'a' && value.outerText == selectedElm.outerText) {
                anchorElm = value;
            }
        }

        onlyText = isOnlyTextSelected();

        data.text = initialText = anchorElm ? (anchorElm.innerText || anchorElm.textContent) : selection.getContent({format: 'text'});
        data.href = anchorElm ? dom.getAttrib(anchorElm, 'href') : '';

        if (anchorElm) {
            data.target = dom.getAttrib(anchorElm, 'target');

            if ((value = dom.getAttrib(anchorElm, 'title'))) {
                data.title = value;
            }
        }
        if (onlyText) {
            textListCtrl = {
                name: 'text',
                type: 'textbox',
                size: 40,
                label: 'Text to display',
                onchange: function () {
                    data.text = this.value();
                }
            };
        }

        if (linkList) {
            linkListCtrl = {
                type: 'listbox',
                label: 'Link list',
                values: buildListItems(
                    linkList,
                    function (item) {
                        item.value = editor.convertURL(item.value || item.url, 'href');
                    },
                    [{text: 'None', value: ''}]
                ),
                onselect: linkListChangeHandler,
                value: editor.convertURL(data.href, 'href'),
                onPostRender: function () {
                    linkListCtrl = this;
                }
            };
        }

        targetListCtrl = {
            name: 'target',
            type: 'checkbox',
            label: 'Open new window',
            checked: (data.target == targetBlank)
        };

        if (editor.settings.link_title !== false) {
            linkTitleCtrl = {
                name: 'title',
                type: 'textbox',
                label: 'Tooltip',
                value: data.title
            };
        }

        win = editor.windowManager.open({
            title: 'Insert link',
            data: data,

            bodyType: 'tabpanel',
            body: [{
                title: 'Content',
                type: "form",
                classes: 'link-tab-content',
                items: [
                    {
                        name: 'contentId',
                        type: 'textbox',
                        classes: 'link-tab-content-target',
                        size: 40,
                        label: 'Target',
                        value: (data.href.indexOf(contentIdPrefix) > -1) ? data.href.replace(contentIdPrefix, "") : ""
                    },
                    textListCtrl,
                    linkTitleCtrl,
                    targetListCtrl
                ]
            }, {
                title: 'Download',
                type: "form",
                classes: 'link-tab-download',
                items: [
                    {
                        name: 'href',
                        type: 'textbox',
                        classes: 'link-tab-download-target',
                        size: 40,
                        label: 'Target'
                    },
                    textListCtrl,
                    linkTitleCtrl
                ]
            }, {
                title: 'URL',
                type: "form",
                classes: 'link-tab-url',
                items: [
                    {
                        name: 'href',
                        type: 'filepicker',
                        filetype: 'file',
                        size: 40,
                        label: 'URL',
                        onchange: urlChange,
                        onkeyup: updateText
                    },
                    textListCtrl,
                    linkTitleCtrl,
                    targetListCtrl
                ]
            }, {
                title: 'Email',
                type: "form",
                classes: 'link-tab-email',
                items: [
                    {
                        name: 'email',
                        type: 'textbox',
                        size: 40,
                        label: 'Email'
                    },
                    textListCtrl,
                    {
                        name: 'subject',
                        type: 'textbox',
                        size: 40,
                        label: 'Subject'
                    }, {
                        name: 'body',
                        type: 'textbox',
                        size: 40,
                        label: 'Body'
                    }
                ]
            }],
            onSubmit: function (e) {
                /*eslint dot-notation: 0*/
                var href, contentId;
                data = tinymce.extend(data, e.data);
                href = data.href;
                contentId = data.contentId;
                if (data.contentId) {
                    href = contentIdPrefix + contentId;
                }

                // Delay confirm since onSubmit will move focus
                function delayedConfirm(message, callback) {
                    var rng = editor.selection.getRng();

                    window.setTimeout(function () {
                        editor.windowManager.confirm(message, function (state) {
                            editor.selection.setRng(rng);
                            callback(state);
                        });
                    }, 0);
                }

                function insertLink() {
                    var linkAttrs = {
                        href: href,
                        target: data.target ? targetBlank : null,
                        title: data.title ? data.title : null
                    };

                    if (anchorElm) {
                        editor.focus();

                        if (onlyText && data.text != initialText) {
                            if ("innerText" in anchorElm) {
                                anchorElm.innerText = data.text;
                            } else {
                                anchorElm.textContent = data.text;
                            }
                        }

                        dom.setAttribs(anchorElm, linkAttrs);

                        selection.select(anchorElm);
                        editor.undoManager.add();
                    } else {
                        if (onlyText) {
                            editor.insertContent(dom.createHTML('a', linkAttrs, dom.encode(data.text)));
                        } else {
                            editor.execCommand('mceInsertLink', false, linkAttrs);
                        }
                    }
                }

                if (!href) {
                    editor.execCommand('unlink');
                    return;
                }

                // Is email and not //user@domain.com
                if (href.indexOf('@') > 0 && href.indexOf('//') == -1 && href.indexOf('mailto:') == -1) {
                    delayedConfirm(
                        'The URL you entered seems to be an email address. Do you want to add the required mailto: prefix?',
                        function (state) {
                            if (state) {
                                href = 'mailto:' + href;
                            }

                            insertLink();
                        }
                    );

                    return;
                }

                // Is not protocol prefixed
                if ((editor.settings.link_assume_external_targets && !/^\w+:/i.test(href)) ||
                    (!editor.settings.link_assume_external_targets && /^\s*www\./i.test(href))) {
                    delayedConfirm(
                        'The URL you entered seems to be an external link. Do you want to add the required http:// prefix?',
                        function (state) {
                            if (state) {
                                href = 'http://' + href;
                            }

                            insertLink();
                        }
                    );

                    return;
                }

                insertLink();
            }
        });

        editor.execCommand("addContentSelector", false,
            win.getEl().querySelectorAll(".mce-link-tab-content .mce-link-tab-content-target")[0]);
    }

    editor.addButton('link', {
        icon: 'link',
        tooltip: 'Insert/edit link',
        shortcut: 'Meta+K',
        onclick: createLinkList(showDialog),
        stateSelector: 'a[href]'
    });

    editor.addButton('unlink', {
        icon: 'unlink',
        tooltip: 'Remove link',
        cmd: 'unlink',
        stateSelector: 'a[href]'
    });

    editor.addShortcut('Meta+K', '', createLinkList(showDialog));
    editor.addCommand('mceLink', createLinkList(showDialog));

    this.showDialog = showDialog;

    editor.addMenuItem('link', {
        icon: 'link',
        text: 'Insert/edit link',
        shortcut: 'Meta+K',
        onclick: createLinkList(showDialog),
        stateSelector: 'a[href]',
        context: 'insert',
        prependToContext: true
    });
});
