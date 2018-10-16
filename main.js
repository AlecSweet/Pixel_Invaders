$(document).ready(function(){

    var ParallaxManager, ParallaxPart;
    
    ParallaxPart = (function() {
      function ParallaxPart(el) {
        this.el = el;
        this.speed = parseFloat(this.el.getAttribute('data-parallax-speed'));
        this.maxScroll = parseInt(this.el.getAttribute('data-max-scroll'));
        this.curScrollY = 0;
        this.curPanX = 0;
        this.curPanY = 0;
        this.xPos = 0;
        this.yPos = 0;
      }
    
      ParallaxPart.prototype.scrollTranslate = function(scrollY) {
        if (scrollY <= this.maxScroll) { 
            this.curScrollY = -(scrollY * this.speed) * 1;
        }
        this.update();
      };

      ParallaxPart.prototype.panTranslate = function(x, y) {
        this.curPanX = x * this.speed * .34;
        this.curPanY = y * this.speed * .34;
        this.update();
      };

      ParallaxPart.prototype.update = function() {
        this.xPos = this.curPanX;
        this.yPos = this.curPanY + this.curScrollY;
        this.updateTransforms();
      };

      ParallaxPart.prototype.setYTransform = function(val) {
        this.el.style.webkitTransform = "translate3d(0, " + val + "px, 0)";
        this.el.style.MozTransform    = "translate3d(0, " + val + "px, 0)";
        this.el.style.OTransform      = "translate3d(0, " + val + "px, 0)";
        this.el.style.transform       = "translate3d(0, " + val + "px, 0)";
        this.el.style.msTransform     = "translateY(" + val + "px)";
      };

      ParallaxPart.prototype.panBackground = function(valX, valY) {
        this.el.style.webkitTransform = "translate3d(" + valX + "px, " + valY + "px, 0)";
        this.el.style.MozTransform    = "translate3d(" + valX + "px, " + valY + "px, 0)";
        this.el.style.OTransform      = "translate3d(" + valX + "px, " + valY + "px, 0)";
        this.el.style.transform       = "translate3d(" + valX + "px, " + valY + "px, 0)";
        this.el.style.msTransform     = "translate(" + valX + "px, " + valY + "px)";
        //$(this.el).css("background-position", valX + "px  " + valY +"px")
      };

      ParallaxPart.prototype.updateTransforms = function() {
        this.el.style.webkitTransform = "translate3d(" + this.xPos + "px, " + this.yPos + "px, 0)";
        this.el.style.MozTransform    = "translate3d(" + this.xPos + "px, " + this.yPos + "px, 0)";
        this.el.style.OTransform      = "translate3d(" + this.xPos + "px, " + this.yPos + "px, 0)";
        this.el.style.transform       = "translate3d(" + this.xPos + "px, " + this.yPos + "px, 0)";
        this.el.style.msTransform     = "translate(" + this.xPos + "px, " + this.yPos + "px)";
      }
    
      return ParallaxPart;
    
    })();
    
    ParallaxManager = (function() {
        ParallaxManager.prototype.layers = [];
        
        function ParallaxManager(elements) {
            if (Array.isArray(elements) && elements.length) {
                this.elements = elements;
            }
            if (typeof elements === 'object' && elements.item) {
            this.elements = Array.prototype.slice.call(elements);
            } else if (typeof elements === 'string') {
                this.elements = document.querySelectorAll(elements);
                if (this.elements.length === 0) {
                    throw new Error("Parallax: No elements found");
                }
                this.elements = Array.prototype.slice.call(this.elements);
            } else {
                throw new Error("Parallax: Element variable is not a querySelector string, Array, or NodeList");
            }
            for (var i in this.elements) {
                this.layers.push(new ParallaxPart(this.elements[i]));
            }
            
            window.addEventListener("scroll", this.onScroll.bind(this));   
            //window.addEventListener("mousemove", this.onPan(window.event).bind(this));
        }
    
        ParallaxManager.prototype.onScroll = function() {
            window.requestAnimationFrame(this.scrollHandler.bind(this));
        };
        
        ParallaxManager.prototype.scrollHandler = function() {
            var scrollY = Math.max(window.pageYOffset, 0);
            for (var i in this.layers) { 
                this.layers[i].scrollTranslate(scrollY); 
            }
        };

        ParallaxManager.prototype.panningHandler = function( event ) {
            var panX = event.pageX - ($(window).width() / 2) - window.pageXOffset;
            var panY = event.pageY - ($(window).height() / 2) - window.pageYOffset;
            for (var i in this.layers) {
                this.layers[i].panTranslate(-panX, -panY); 
            }
        }

        // ParallaxManager.prototype.onPan = function(e) {
        //     window.requestAnimationFrame(this.panningHandler(e).bind(this));
        // };
        
        // ParallaxManager.prototype.panningHandler = function(e) {
        //     var x = e.pageX - ($(window).width() / 2);
        //     var y = e.pageY - ($(window).height() / 2);
        //     for (var i in this.parts) { this.parts[i].pan(x, y); }
        // }; 

        return ParallaxManager;
    
    })();

    var paraManager = new ParallaxManager('.parallax-layer');

    $(document).on( "touchmove", paraManager.panningHandler.bind(paraManager));
});


















