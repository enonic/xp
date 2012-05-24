function postProcessAnchorElements()
{
    var anchors = document.getElementsByTagName('a');
    var anchor = null;
    for ( var i = 0; i < anchors.length; i++ )
    {
        anchor = anchors[i];
        if ( anchor.getAttribute('href') && anchor.getAttribute('rel') === 'external' )
        {
            anchor.target = '_blank';
        }
    }
}

function navigateTo( url )
{
    document.location.href = url;
}

window.onload = postProcessAnchorElements;