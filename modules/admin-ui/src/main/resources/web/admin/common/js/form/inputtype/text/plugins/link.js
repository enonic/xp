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

    function showDialog() {
        var data = {}, selection = editor.selection, dom = editor.dom, selectedElm, anchorElm, initialText;
        var win, onlyText, textListCtrl, linkTitleCtrl, value;
        var contentIdPrefix = "content://", mediaIdPrefix = "media://download/", subjectPrefix = "subject=", emailPrefix = "mailto:", targetBlank = "_blank";
        var tabNames = {
            content: "Content",
            media: "Download",
            url: "URL",
            email: "Email"
        };

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

        function isTabActive(tabName) {
            var activeTab = win.getEl().querySelectorAll(".mce-tab.mce-active")[0];
            return activeTab.innerText == tabName;
        }

        function getActiveTab(data) {
            if (data.email) {
                return 3;
            }
            if (data.mediaId) {
                return 2;
            }
            if (data.url) {
                return 1;
            }

            return 0;
        }

        function getHref(data) {
            if (isTabActive(tabNames.content) && data.contentId) {
                return contentIdPrefix + data.contentId;
            }

            if (isTabActive(tabNames.media) && data.mediaId) {
                return mediaIdPrefix + data.mediaId;
            }

            if (isTabActive(tabNames.email) && data.email) {
                return emailPrefix + data.email + (data.subject ? "?" + subjectPrefix + encodeURI(data.subject) : "");
            }

            return encodeURI(data.url);
        }

        function getTarget(data) {
            return ((isTabActive(tabNames.content) && data.targetContent) || (isTabActive(tabNames.url) && data.targetUrl));
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

        if (anchorElm) {
            data.href = dom.getAttrib(anchorElm, 'href');
            data.target = dom.getAttrib(anchorElm, 'target');

            if ((value = dom.getAttrib(anchorElm, 'title'))) {
                data.title = value;
            }

            if (data.href.startsWith(contentIdPrefix)) {
                data.contentId = data.href.replace(contentIdPrefix, "");
                data.targetContent = data.target;
            }
            else if (data.href.startsWith(mediaIdPrefix)) {
                data.mediaId = data.href.replace(mediaIdPrefix, "");
            } else if (data.href.startsWith(emailPrefix)) {
                var email = data.href.replace(emailPrefix, "").split("?");
                data.email = email[0];
                data.subject =
                (email.length > 1 && email[1].startsWith(subjectPrefix)) ? decodeURI(email[1].replace(subjectPrefix, "")) : "";
            }
            else {
                data.url = decodeURI(data.href);
                data.targetUrl = data.target;
            }
        }
        if (onlyText) {
            textListCtrl = {
                name: 'text',
                type: 'textbox',
                size: 40,
                classes: 'link-text',
                label: 'Text to display',
                onchange: function () {
                    data.text = this.value();
                }
            };
        }

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
            classes: 'link-dialog',
            body: [{
                type: "form",
                classes: 'link-form',
                items: [
                    textListCtrl,
                    linkTitleCtrl
                ]
            }, {
                type: "tabpanel",
                classes: 'link-panel',
                activeTab: getActiveTab(data),
                items: [{
                    title: tabNames.content,
                    type: "form",
                    classes: 'link-tab-content',
                    items: [{
                        name: 'contentId',
                        type: 'textbox',
                        classes: 'link-tab-content-placeholder',
                        size: 40,
                        label: 'Target'
                    }, {
                        name: 'targetContent',
                        type: 'checkbox',
                        label: 'Open new window',
                        checked: (data.targetContent == targetBlank)
                    }]
                }, {
                    title: tabNames.url,
                    type: "form",
                    classes: 'link-tab-url',
                    items: [{
                        name: 'url',
                        type: 'textbox',
                        size: 40,
                        label: 'URL'
                    }, {
                        name: 'targetUrl',
                        type: 'checkbox',
                        label: 'Open new window',
                        checked: (data.targetUrl == targetBlank)
                    }]   
                }, {
                    title: tabNames.media,
                    type: "form",
                    classes: 'link-tab-media',
                    items: [{
                        name: 'mediaId',
                        type: 'textbox',
                        classes: 'link-tab-media-placeholder',
                        size: 40,
                        label: 'Target'
                    }]
                }, {
                    title: tabNames.email,
                    type: "form",
                    classes: 'link-tab-email',
                    items: [{
                        name: 'email',
                        type: 'textbox',
                        size: 40,
                        label: 'Email'
                    }, {
                        name: 'subject',
                        type: 'textbox',
                        size: 40,
                        label: 'Subject'
                    }]
                }]
            }],
            onSubmit: function (e) {
                /*eslint dot-notation: 0*/
                var href, target;
                data = tinymce.extend(data, e.data);
                href = getHref(data);
                target = getTarget(data);

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
                        target: target ? targetBlank : "",
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
                if (href.indexOf('@') > 0 && href.indexOf('//') == -1 && href.indexOf(emailPrefix) == -1) {
                    delayedConfirm(
                        'The URL you entered seems to be an email address. Do you want to add the required mailto: prefix?',
                        function (state) {
                            if (state) {
                                href = emailPrefix + href;
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

        editor.execCommand("initSelectors", false, win.getEl());
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
