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

        /*var to1 = cc.progressTo(5, 100);
         var to2 = cc.progressTo(8, 100);

         var left = new cc.ProgressTimer(new cc.Sprite(res.CloseSelected_png));
         left.setType(cc.ProgressTimer.TYPE_RADIAL);
         left.setPosition(cc.p(size.width / 4, size.height / 2));
         this.addChild(left);
         left.runAction(to1);

         var right = new cc.ProgressTimer(new cc.Sprite(res.CloseNormal_png));
         right.setType(cc.ProgressTimer.TYPE_RADIAL);
         right.setReverseDirection(true);
         right.setPosition(cc.p(size.width / 4 * 3, size.height / 2));
         this.addChild(right);
         right.runAction(cc.repeatForever(to2));*/

        var sprite = new cc.Sprite(res.HelloWorld_png);
        //sprite.setScale(0.5);
        sprite.attr({x: size.width / 2, y: size.height / 2});
        this.addChild(sprite);
        sprite.runAction(cc.repeatForever(cc.sequence(cc.scaleTo(5, 0.5), cc.scaleTo(5, 1))));

        return true;
    }
});

var HelloWorldScene = cc.Scene.extend({
    onEnter: function () {
        this._super();
        var layer = new HelloWorldLayer();
        this.addChild(layer);
    }
});

