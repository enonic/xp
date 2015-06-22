exports.get = function (req) {
    var component = execute('portal.getComponent');

    return {
        body: execute('thymeleaf.render', {
            view: resolve('centered.html'),
            model: {
                centerRegion: component.regions["center"]
            }
        })
    };

};
