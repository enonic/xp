var xplauncher = function (assetsUri, doc) {
    if (assetsUri && !!doc) {
        var launcherJs = doc.createElement("script");
        launcherJs.setAttribute("type", "text/javascript");
        launcherJs.setAttribute("src", assetsUri + "/apps/launcher/js/_all.js");

        doc.getElementsByTagName("body")[0].appendChild(launcherJs);
    }
};