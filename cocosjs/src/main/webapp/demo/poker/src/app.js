var HelloWorldLayer = cc.Layer.extend({
    sprite: null,
    showPoker: function (size) {
        var pokerTexture = cc.textureCache.addImage(res.poker_png);
        var unitWidth = pokerTexture.getContentSize().width / 13;
        var unitHeight = pokerTexture.getContentSize().height / 5;
        var sprite = new cc.Sprite(pokerTexture, cc.rect(0, 4 * unitHeight, unitWidth, unitHeight));
        //var sprite=new cc.Sprite(pokerTexture);
        sprite.attr({x: size.width / 2, y: size.height / 2});
        this.addChild(sprite);
    }, ctor: function () {
        //////////////////////////////
        // 1. super init first
        this._super();

        /////////////////////////////
        // 2. add a menu item with "X" image, which is clicked to quit the program
        //    you may modify it.
        // ask the window size
        var size = cc.winSize;

        //this.showPoker(size);
        this.testMenu(size);
        return true;
    },
    testMenu: function (size) {
        this.addChild(new cc.LayerColor(cc.color(200,255,255,180)));
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

