$(document).ready(function() {
    resizeContent();

    $(window).resize(function() {
        resizeContent();
    });
});

function resizeContent() {
    //$(window).scrollTop(0);
    var height = $(window).height();
    $('#particles-container').height(height);
}
