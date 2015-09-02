exports.send = function (message) {

    var bean = __.newBean('com.enonic.xp.lib.mail.SendMailHandler');

    bean.subject = __.nullOrValue(message.subject);
    bean.from = __.nullOrValue(message.from);
    bean.to = __.nullOrValue(message.to);
    bean.headers = __.nullOrValue(message.headers);

    return bean.send();

};
