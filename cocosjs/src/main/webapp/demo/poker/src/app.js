var HelloWorldLayer = cc.Layer.extend({
    sprite: null,
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
        cc.textureCache.addImage(res.poker_png);

        //var center=new HandCards("center",[0,3,8,9,13,21,22,40]);
        //this.addChild(center);



        var left=new HandCards("left",[0,3,8,9,13,21,22,40]);
        this.addChild(left);

        var center=new HandCards("center",[0,3,8,9,13,21,22,40]);
        this.addChild(center);

        //var right=new HandCards("right",[0,3,8,9,13,21,22,40]);
        //this.addChild(right);
    },
    onTouchBegan: function () {
        cc.log("touch began");
    },
    cardClick: function (sender) {
        console.log(arguments);
    },
    testMenu: function (size) {
        var menuItem = new cc.MenuItemFont("hello", function () {
            cc.log("menuItem click");
        });
        var menuItem2 = new cc.MenuItemFont("world", function () {
            cc.log("menuItem2 click");
        });

        var menu = new cc.Menu(menuItem, menuItem2);
        menu.alignItemsVerticallyWithPadding(5);

        this.addChild(menu);
    }
});

var HelloWorldScene = cc.Scene.extend({
    onEnter: function () {
        this._super();
        var layer = new HelloWorldLayer();
        this.addChild(layer);
    }
});

