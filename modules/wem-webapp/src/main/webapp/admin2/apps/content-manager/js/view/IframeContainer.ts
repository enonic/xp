module admin.ui {

    export class IframeContainer {

        ext:Ext_panel_Panel;

        private url:string;
        private iFrameCls:string;

        constructor(url?:string, iFrameCls?:string) {
            var container = this;

            this.url = url;
            this.iFrameCls = iFrameCls || '';

            var panel = new Ext.panel.Panel({
                html: '<iframe style="border: 0 none; width: 100%; height: 420px;"></iframe>',
                autoScroll: false,
                styleHtmlContent: true,
                minHeight: 420,
                listeners: {
                    afterrender: () => {
                        if (container.url) {
                            container.load(container.url);
                        }
                    }
                }
            });

            this.ext = <Ext_panel_Panel> panel;
        }

        load(url:string):void {
            var iframe = this.getIframe();
            if (!Ext.isEmpty(url) && Ext.isDefined(iframe)) {
                iframe.dom.src = api_util.getAbsoluteUri(url);
                /*            if (this.iFrameCls) {
                 console.log(iframe.dom.contentDocument);
                 iframe.dom.contentDocument.body.className = "test";
                 }*/
            } else {
                iframe.update("<h2 class='message'>Page can't be found.</h2>");
            }
        }

        private getIframe():Ext_dom_Element {
            return this.ext.getEl().down('iframe');
        }

    }
}
