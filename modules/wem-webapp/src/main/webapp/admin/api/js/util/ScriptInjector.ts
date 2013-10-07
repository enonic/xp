module api_util {
    export class ScriptInjector {
        static inject(url:string, callback:() => void) {

            var load = true;
            //check all existing script tags in the page for the url
            jQuery('script[type="text/javascript"]')
                .each(function () {
                    return load = (url != jQuery(this).attr('data-url'));
                });
            if (load) {
                //didn't find it in the page, so load it
                jQuery.ajax(url, {
                    type: 'GET',
                    success: (data:any, status:string, xhr:JQueryXHR) => {
                        var node = document.getElementsByTagName("head")[0] || document.body;
                        if (node) {
                            var script = document.createElement("script");
                            script.setAttribute("type", "text/javascript");
                            script.setAttribute("data-url", url);
                            script.innerHTML = data;
                            node.appendChild(script);
                        }
                        callback();
                    },
                    dataType: 'script',
                    cache: true
                });
            }
            else {
                callback();
            }
        }
    }
}