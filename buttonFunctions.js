function initMainButtons(){ 
    var widthRatio = screen.width / 1920;
    $(".main-button").animate({
        left: "+=" + Math.round(50 * widthRatio) + "px"
    }, 0, "linear", function() {
    });
    $(".main-button").toggle();
}

function buttonMouseOver(x) {
    //x.style.height *= 1.1;
    //x.style.width = 1.1;
    //h = Math.round(parseInt(x.css("height"),10) * widthRatio);
    //w = Math.round(parseInt(x.css("width"),10) * widthRatio);
    var widthRatio = screen.width / 1920;
    x.src = x.getAttribute("data-mouseover-src");
    $("#" + x.getAttribute("id")).animate({
        opacity: 1,
        left: "-=" + Math.round(5 * widthRatio) + "px"
    }, 100, "linear", function() {
    });
}
        
function buttonMouseOut(x) {
    //x.style.height /= 1.1;
    //x.style.width /= 1.1;
    var widthRatio = screen.width / 1920;
    x.src = x.getAttribute("data-mouseout-src");
    $("#" + x.getAttribute("id")).animate({
        opacity: .8,
        left: "+=" + Math.round(5 * widthRatio) + "px"
    }, 100, "linear", function() {
    });
}

function scrollToSection(x) {
    $([document.documentElement, document.body]).animate({
        scrollTop: x.offset().top
    }, 500, "swing");
}

function toggleButtons(show) {
    var widthRatio = screen.width / 1920;
    window.setTimeout(function(){buttonAnim($("#trailer-button"))}, 0);
    window.setTimeout(function(){buttonAnim($("#features-button"))}, 50);
    window.setTimeout(function(){buttonAnim($("#media-button"))}, 100);
    window.setTimeout(function(){buttonAnim($("#algorithms-button"))}, 150);
    window.setTimeout(function(){buttonAnim($("#github-button"))}, 200);
    var buttonAnim = function(button){
        if(show == false){          
            button.animate({
                opacity: 0,
                left: "+=" + Math.round(50 * widthRatio) + "px",
            }, 50, "linear", function() {
                button.toggle(false);
            });
        } else {
            button.toggle(true);
            button.animate({
                opacity: .8,
                left: "-=" + Math.round(50 * widthRatio) + "px",
            }, 50, "linear", function() {
            });
        }
    };
    //window.setTimeout(function(){toggleButtons(false)}, 1200);
   // window.setTimeout(function(){toggleButtons(true)}, 2400);
}