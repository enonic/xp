var topic = (location.hash || '#content management').substring(1);

$(window).bind('load',function() {
    $(document).liveTwitter(topic ,{
        rpp: 20,
        filter: function(tweet){
            var twc = Processing.getInstanceById("twc");
            if(twc != null)
                setTimeout(function(){twc.addTweet(tweet)}, Math.random()*10000 + 1000);
        }
    })
});