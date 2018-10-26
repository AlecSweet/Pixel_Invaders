
$(document).ready(function(){
    var ParallaxManager, ParallaxLayer;
    var high = screen.width > screen.height ? screen.width : screen.height;

    var temp = Math.round(high * .0375 * .8);
    $('.layer-4').css({
        'background-size': temp + 'px ' + temp + 'px',
        'top': 'calc(57% + ' + Math.round(high * .0291 * .8) + 'px)',
        'left': 'calc(57% + ' + Math.round(high * .0552 * .8) + 'px)',
    });

    temp = Math.round(high * .117 * .8);
    $('.layer-5').css({
        'background-size': temp + 'px ' + temp + 'px',
        'top': 'calc(57% - ' + Math.round(high * .0583 * .8)  + 'px)',
        'left': 'calc(57% - ' + Math.round(high * .0583 * .8) + 'px)',
    });

    ParallaxLayer = (function() {
        function ParallaxLayer(el) {
            this.el = el;
            this.speed = parseFloat(this.el.getAttribute('data-parallax-speed'));
            this.maxScroll = parseInt(this.el.getAttribute('data-max-scroll'));
            this.curScrollY = 0;
            this.curPanX = 0;
            this.curPanY = 0;
        }

        ParallaxLayer.prototype.scrollTranslate = function(scrollY) {
            this.curScrollY = Math.round(-scrollY * this.speed);
        };

        ParallaxLayer.prototype.panTranslate = function(x, y) {
            this.curPanX = Math.round(x * this.speed * .4);
            this.curPanY = Math.round(y * this.speed * .4);
        };

        ParallaxLayer.prototype.update = function() {
            var temp1 = this.curPanY + this.curScrollY;
            console.log(this.curPanX + ', ' + this.curPanY + ' : ' + this.curScrollY);
            $(this.el).css('transform', 'translate3d(' + this.curPanX + 'px, ' + temp1 + 'px, 0)' );
        };

        return ParallaxLayer;
    
    })();
    
    ParallaxManager = (function() {
        ParallaxManager.prototype.layers = [];
        
        function ParallaxManager(elements) {
            this.mouseX = 0;
            this.mouseY = 0;
            if (Array.isArray(elements) && elements.length) {
                this.elements = elements;
            }
            if (typeof elements === 'object' && elements.item) {
                this.elements = Array.prototype.slice.call(elements);
            } else if (typeof elements === 'string') {
                this.elements = document.querySelectorAll(elements);
                if (this.elements.length === 0) {
                    throw new Error('Parallax: No elements found');
                }
                this.elements = Array.prototype.slice.call(this.elements);
            } else {
                throw new Error('Parallax: Element variable is not a querySelector string, Array, or NodeList');
            }
            for (var i in this.elements) {
                this.layers.push(new ParallaxLayer(this.elements[i]));
            }
            
            window.setInterval(this.update.bind(this), 14);
            $(document).on('mousemove', function(event){
                this.mouseX = event.pageX - window.pageXOffset;
                this.mouseY = event.pageY - window.pageYOffset;
            }.bind(this));
            $(document).on('touchstart', function onFirstTouch() {
                $(document).off('touchstart');
                $(document).off('mousemove');
            }); 
        }

        ParallaxManager.prototype.update = function() {
            var scrollY = window.pageYOffset;//Math.max(window.pageYOffset, 0);
            var panX = this.mouseX - Math.round($(window).width() / 2) - window.pageXOffset;
            var panY = this.mouseY - Math.round($(window).height() / 2) - window.pageYOffset;
            for (var i in this.layers) {
                this.layers[i].scrollTranslate(scrollY); 
                this.layers[i].panTranslate(-panX, -panY); 
                this.layers[i].update();
            }
        }

        return ParallaxManager;
    })();

    var paraManager = new ParallaxManager('.parallax-layer');
});


















