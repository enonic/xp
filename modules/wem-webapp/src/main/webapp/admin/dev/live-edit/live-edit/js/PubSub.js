AdminLiveEdit.PubSub = $liveedit($liveedit({}));
$liveedit.subscribe = function () {
    AdminLiveEdit.PubSub.on.apply(AdminLiveEdit.PubSub, arguments);
};

$liveedit.unsubscribe = function () {
    AdminLiveEdit.PubSub.off.apply(AdminLiveEdit.PubSub, arguments);
};

$liveedit.publish = function () {
    AdminLiveEdit.PubSub.trigger.apply(AdminLiveEdit.PubSub, arguments);
};