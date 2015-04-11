var StartMenuLayer = cc.Layer.extend({
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
        var helloLabel = new cc.LabelTTF("开始游戏", "Arial", 28);
        helloLabel.setFontFillColor(cc.color(0, 0, 0));
        var startItem=new cc.MenuItemLabel(
            helloLabel,
            function(){
                cc.director.runScene(new cc.TransitionFade(1,new TableScene(),false));
            },this);
        startItem.attr({
            x:size.width/2,
            y:size.height/2,
            anchorX:0.5,
            anchorY:0.5
        });

        var menu = new cc.Menu(startItem);
        menu.x=0;
        menu.y=0;
        this.addChild(menu,1);

        return true;
    }
});

var StartMenuScene = cc.Scene.extend({
    onEnter: function () {
        this._super();
        var layer = new StartMenuLayer();
        this.addChild(layer);
    }
});