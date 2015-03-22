/**
 * Created by landy on 15/3/20.
 */
var PlayScene = cc.Scene.extend({
    space: null,
    gameLayer:null,
    shapesToRemove:[],

    onEnter: function () {
        this._super();
        this.initPhysics();

        this.shapesToRemove = [];
        this.gameLayer=new cc.Layer();

        this.gameLayer.addChild(new BackgroundLayer(this.space),0,TagOfLayer.background);
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
        this.space.addCollisionHandler(SpriteTag.runner, SpriteTag.coin,
            this.collisionCoinBegin.bind(this), null, null, null);
        this.space.addCollisionHandler(SpriteTag.runner, SpriteTag.rock,
            this.collisionRockBegin.bind(this), null, null, null);
    },
    update:function(dt) {
        this.space.step(dt);

        var animationLayer=this.gameLayer.getChildByTag(TagOfLayer.Animation);
        var eyeX = animationLayer.getEyeX();

        this.gameLayer.setPosition(cc.p(-eyeX, 0));

        /*for(var i=0;i<this.shapesToRemove.length;i++) {
            var shape = this.shapesToRemove[i];
            this.gameLayer.getChildByTag(TagOfLayer.background).removeObjectByShape(shape);
        }
        this.shapesToRemove = [];*/
        while(this.shapesToRemove.length>0) {
            var shape=this.shapesToRemove.pop();
            this.gameLayer.getChildByTag(TagOfLayer.background).removeObjectByShape(shape);
        }
    },
    collisionCoinBegin:function(arbiter,space) {
        var shapes=arbiter.getShapes();

        this.shapesToRemove.push(shapes[1]);

        var statusLayer = this.getChildByTag(TagOfLayer.Status);
        statusLayer.addCoin(1);
    },
    collisionRockBegin:function(arbiter,space) {
        cc.log("==Game Over....");
        cc.director.pause();
        this.addChild(new GameOverLayer());
    }

});