/**
 * Created by landy on 15/3/20.
 */
var MenuLayer = cc.Layer.extend({
    ctor: function () {
        this._super();
    },
    init: function () {
        this._super();

        var winsize = cc.director.getWinSize();

        var centerpos = cc.p(winsize.width / 2, winsize.height / 2);

        var spritebg = new cc.Sprite(res.helloBG_png);
        spritebg.setPosition(centerpos);
        this.addChild(spritebg);

        cc.MenuItemFont.setFontSize(60);

        var menuItemPlay = new cc.MenuItemSprite(
            new cc.Sprite(res.start_n_png),
            new cc.Sprite(res.start_s_png),
            this.onPlay,
            this
        );
        var menu = new cc.Menu(menuItemPlay);
        menu.setPosition(centerpos);
        this.addChild(menu);
    },
    onPlay: function () {
        cc.log("==onplay clicked")
        cc.director.runScene(new PlayScene());
    }
});

var MenuScene = cc.Scene.extend({
    onEnter: function () {
        this._super();
        var layer = new MenuLayer();
        layer.init();
        this.addChild(layer);
    }
});
