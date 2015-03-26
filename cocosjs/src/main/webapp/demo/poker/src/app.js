var HelloWorldLayer = cc.Layer.extend({
    ctor: function () {
        //////////////////////////////
        // 1. super init first
        this._super();

        /////////////////////////////
        // 2. add a menu item with "X" image, which is clicked to quit the program
        //    you may modify it.
        // ask the window size
        var size = cc.winSize;
        this.addChild(new cc.LayerColor(cc.color(200, 255, 255, 180)));

        this.showPoker(size);
        //this.testMenu(size);


        /*cc.eventManager.addListener(cc.EventListener.create({
         event: cc.EventListener.TOUCH_ONE_BY_ONE,
         //swallowTouches: true,
         onTouchBegan: function (touch, event) {
         var pos = touch.getLocation();
         var target = event.getCurrentTarget();
         console.log(pos);
         return true;
         }
         }), this);*/

        return true;
    },
    showPoker: function (size) {
        //var center=new HandCards("center",[0,3,8,9,13,21,22,40]);
        //this.addChild(center);


        var newVar = [0, 3, 8, 9, 10, 12, 13, 18, 19, 21, 22, 32, 40];
        newVar.sort(function (a, b) {
            return b - a;
        });
        //newVar.reverse();
        console.log(newVar);
        var unknows = [-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1];
        var left = new HandCards(this, "left", unknows);

        var center = new HandCards(this, "center", newVar);

        var right = new HandCards(this, "right", unknows);

        var belowCards = new BelowCards(this, [23, 34, 51]);
        belowCards.showUnknowStyle();

        window.belowCards=belowCards;
    }
});

var HelloWorldScene = cc.Scene.extend({
    onEnter: function () {
        this._super();
        var layer = new HelloWorldLayer();
        this.addChild(layer);
    }
});

