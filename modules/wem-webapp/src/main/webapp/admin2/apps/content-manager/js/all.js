var admin;
(function (admin) {
    (function (app) {
        (function (handler) {
            var DeleteContentHandler = (function () {
                function DeleteContentHandler() { }
                DeleteContentHandler.prototype.doDelete = function (spaces, callback) {
                    var me = this;
                    var spaceNames = Ext.Array.map([].concat(spaces), function (item) {
                        return item.get('name');
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
                this.headerTemplate = '<h2>{title}</h2><tpl if="subtitle != undefined"><p>{subtitle}</p></tpl>';
                this.listTemplate = '<div class="delete-container">' + '    <tpl for=".">' + '        <div class="delete-item">' + '            <img class="icon" src="{data.iconUrl}"/>' + '            <h4>{data.displayName}</h4>' + '            <p>{data.type}</p>' + '        </div>' + '    </tpl>' + '</div>';
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
                header.tpl = this.headerTemplate;
                header.data = {
                    title: this.title
                };
                header.margin = '0 0 20 0';
                ct.add(header);
                var content = new Ext.Component();
                content.itemId = 'modalDialog';
                content.cls = 'dialog-info';
                content.border = false;
                content.height = 150;
                content.styleHtmlContent = true;
                content.tpl = this.listTemplate;
                ct.add(content);
            }
            DeleteContentWindow.prototype.setModel = function (model) {
                this.data = model;
                if(model) {
                    var info = this.container.down('#modalDialog');
                    if(info) {
                        info.update(model);
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
//@ sourceMappingURL=all.js.map
