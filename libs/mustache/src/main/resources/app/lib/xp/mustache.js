var service = __.getBean('com.enonic.xp.lib.mustache.MustacheService');

exports.render = function (view, model) {
    var processor = service.newProcessor();
    processor.view = view;
    processor.model = model;
    return processor.process();
};
