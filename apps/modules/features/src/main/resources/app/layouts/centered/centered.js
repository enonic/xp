var portal = require('/lib/xp/portal');

exports.get = function (req) {
    var component = portal.getComponent();

    return {
        body: execute('thymeleaf.render', {
            view: resolve('centered.html'),
            model: {
                centerRegion: component.regions["center"]
            }
        })
    };

};
