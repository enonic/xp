$(document).ready(function () {
	
	$('A[rel="external"]').click(function () {
		window.open($(this).attr('href'));
		return false;
	});
	
	
	$(".buttonH").hover(function () {
		$(this).addClass('hover');
	},
	function () {
		$(this).removeClass('hover');
	});
});