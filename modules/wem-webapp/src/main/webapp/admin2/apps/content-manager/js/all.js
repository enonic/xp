var admin;
(function (admin) {
    (function (app) {
        (function (handler) {
            var DeleteContentHandler = (function () {
                function DeleteContentHandler() { }
                DeleteContentHandler.prototype.doDelete = function (contentModels, callback) {
                    var _this = this;
                    var contentPaths = Ext.Array.map([].concat(contentModels), function (item) {
                        return item.get('path');
                    });
                    Admin.lib.RemoteService.content_delete({
                        'contentPaths': contentPaths
                    }, function (response) {
                        if(response) {
                            callback.call(_this, response.success, response.failures);
                        } else {
                            Ext.Msg.alert('Error', response ? response.error : 'Internal error occured.');
                        }
                    });
                };
                return DeleteContentHandler;
            })();
            handler.DeleteContentHandler = DeleteContentHandler;            
        })(app.handler || (app.handler = {}));
        var handler = app.handler;
    })(admin.app || (admin.app = {}));
    var app = admin.app;
})(admin || (admin = {}));
var admin;
(function (admin) {
    (function (ui) {
        var DeleteContentWindow = (function () {
            function DeleteContentWindow() {
                var _this = this;
                this.title = "Delete content(s)";
                this.deleteHandler = new admin.app.handler.DeleteContentHandler();
                this.template = '<div class="delete-container">' + '<tpl for=".">' + '<div class="delete-item">' + '<img class="icon" src="{data.iconUrl}"/>' + '<h4>{data.displayName}</h4>' + '<p>{data.type}</p>' + '</div>' + '</tpl>' + '</div>';
                var deleteCallback = function (obj, success, result) {
                    _this.container.hide();
                };
                var ct = this.container = new Ext.container.Container();
                ct.border = false;
                ct.floating = true;
                ct.shadow = false;
                ct.width = 500;
                ct.modal = true;
                ct.autoHeight = true;
                ct.maxHeight = 600;
                ct.cls = 'admin-window';
                ct.padding = 20;
                var header = new Ext.Component();
                header.region = 'north';
                header.tpl = '<h2>{title}</h2><tpl if="subtitle != undefined"><p>{subtitle}</p></tpl>';
                header.data = {
                    title: this.title
                };
                header.margin = '0 0 20 0';
                ct.add(header);
                var content = this.content = new Ext.Component();
                content.region = 'center';
                content.cls = 'dialog-info';
                content.border = false;
                content.height = 150;
                content.styleHtmlContent = true;
                content.tpl = this.template;
                ct.add(content);
                var buttonRow = new Ext.container.Container();
                buttonRow.layout = {
                    type: 'hbox',
                    pack: 'end'
                };
                var deleteButton = new Ext.button.Button();
                deleteButton.text = 'Delete';
                deleteButton.margin = '0 0 0 10';
                deleteButton.handler = function (btn, evt) {
                    _this.deleteHandler.doDelete(_this.data, deleteCallback);
                };
                buttonRow.add(deleteButton);
                var cancelButton = new Ext.button.Button();
                cancelButton.text = 'Cancel';
                cancelButton.margin = '0 0 0 10';
                cancelButton.handler = function (btn, evt) {
                    ct.hide();
                };
                buttonRow.add(cancelButton);
                ct.add(buttonRow);
            }
            DeleteContentWindow.prototype.setModel = function (model) {
                this.data = model;
                if(model) {
                    if(this.content) {
                        this.content.update(model);
                    }
                }
            };
            DeleteContentWindow.prototype.doShow = function () {
                this.container.show();
            };
            return DeleteContentWindow;
        })();
        ui.DeleteContentWindow = DeleteContentWindow;        
    })(admin.ui || (admin.ui = {}));
    var ui = admin.ui;
})(admin || (admin = {}));
var admin;
(function (admin) {
    (function (ui) {
        var FormComponent = (function () {
            function FormComponent() {
                var panel = new Ext.form.Panel();
                this.ext = panel;
            }
            FormComponent.prototype.getContentData = function () {
                var contentData = new API_content_data.ContentData();
                return contentData;
            };
            return FormComponent;
        })();
        ui.FormComponent = FormComponent;        
    })(admin.ui || (admin.ui = {}));
    var ui = admin.ui;
})(admin || (admin = {}));
var admin;
(function (admin) {
    (function (ui) {
        var FormItemSetComponent = (function () {
            function FormItemSetComponent() {
                var panel = new Ext.form.Panel();
                this.ext = panel;
            }
            return FormItemSetComponent;
        })();
        ui.FormItemSetComponent = FormItemSetComponent;        
    })(admin.ui || (admin.ui = {}));
    var ui = admin.ui;
})(admin || (admin = {}));
var admin;
(function (admin) {
    (function (ui) {
        var BaseInputComponent = (function () {
            function BaseInputComponent(input) {
                this.input = input;
            }
            BaseInputComponent.prototype.getInput = function () {
                return this.input;
            };
            BaseInputComponent.prototype.setValue = function (value, arrayIndex) {
                this.values[arrayIndex] = value;
            };
            return BaseInputComponent;
        })();
        ui.BaseInputComponent = BaseInputComponent;        
    })(admin.ui || (admin.ui = {}));
    var ui = admin.ui;
})(admin || (admin = {}));
var __extends = this.__extends || function (d, b) {
    function __() { this.constructor = d; }
    __.prototype = b.prototype;
    d.prototype = new __();
};
var admin;
(function (admin) {
    (function (ui) {
        var TextLine = (function (_super) {
            __extends(TextLine, _super);
            function TextLine(input) {
                        _super.call(this, input);
                var fieldContainer = new Ext.form.FieldContainer();
                fieldContainer.setFieldLabel('');
                fieldContainer.labelWidth = 110;
                fieldContainer.labelPad = 0;
                var textField = new Ext.form.Text();
                textField.enableKeyEvents = true;
                textField.displayNameSource = true;
                fieldContainer.add(textField);
                this.ext = fieldContainer;
            }
            TextLine.prototype.setValue = function (value, arrayIndex) {
                this.ext.down('textfield').setValue(value);
                _super.prototype.setValue.call(this, value, arrayIndex);
            };
            return TextLine;
        })(admin.ui.BaseInputComponent);
        ui.TextLine = TextLine;        
    })(admin.ui || (admin.ui = {}));
    var ui = admin.ui;
})(admin || (admin = {}));
//@ sourceMappingURL=all.js.map
