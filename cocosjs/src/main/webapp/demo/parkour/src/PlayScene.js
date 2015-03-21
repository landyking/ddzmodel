/**
 * Created by landy on 15/3/20.
 */
var PlayScene = cc.Scene.extend({
    space: null,
    gameLayer:null,

    onEnter: function () {
        this._super();
        this.initPhysics();

        this.gameLayer=new cc.Layer();

        this.gameLayer.addChild(new BackgroundLayer(),0,TagOfLayer.background);
        this.gameLayer.addChild(new AnimationLayer(this.space),0,TagOfLayer.Animation);
        this.addChild(this.gameLayer);
        this.addChild(new StatusLayer(),0,TagOfLayer.Status);

        this.scheduleUpdate();
    },
    initPhysics: function () {
        this.space = new cp.Space();
        this.space.gravity = cp.v(0, -350);

        var wallBottom = new cp.SegmentShape(this.space.staticBody,
            cp.v(0, g_groundHight),
            cp.v(4294967295, g_groundHight),
            0);
        this.space.addStaticShape(wallBottom);
    },
    update:function(dt) {
        this.space.step(dt);

        var animationLayer=this.gameLayer.getChildByTag(TagOfLayer.Animation);
        var eyeX = animationLayer.getEyeX();

        this.gameLayer.setPosition(cc.p(-eyeX, 0));
    }

});