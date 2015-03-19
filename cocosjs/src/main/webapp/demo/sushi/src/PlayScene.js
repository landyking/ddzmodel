/**
 * Created by landy on 2015/3/19.
 */
var PlayLayer = cc.Layer.extend({
    bgSprite:null,
    SushiSprites:null,
    ctor:function(){
        this._super();

        var size=cc.winSize;

        this.bgSprite = new cc.Sprite(res.BackGround_png);
        this.bgSprite.attr({
           x:size.width/2,
            y:size.height/2,
            rotation:180
        });
        this.addChild(this.bgSprite,0);

        this.SushiSprites=[];
        this.schedule(this.update, 1, 16 * 1024, 1);

        return true;
    },
    addSushi:function(){
        var sushi = new cc.Sprite(res.Sushi_png);
        var size=cc.winSize;
        var x=sushi.width/2+size.width/2*cc.random0To1();
        sushi.attr({
            x:x,
            y:size.height-30
        });
        this.SushiSprites.push(sushi);
        this.addChild(sushi,5);

        var dropAction = cc.moveTo(4, cc.p(sushi.x, -30));
        sushi.runAction(dropAction);
    },
    update:function(){
        this.addSushi();
        this.removeSushi();
    },
    removeSushi:function(){
        for(var i=0;i<this.SushiSprites.length;i++) {
            cc.log("removeSushi.......");
            //cc.log(this.SushiSprites[i].y);
            if(0>=this.SushiSprites[i].y) {
                cc.log("==============remove:" + i);
                this.SushiSprites[i].removeFromParent();
                this.SushiSprites[i]=undefined;
                this.SushiSprites.splice(i, 1);
                i=i-1;
            }
        }
    }
});

var PlayScene = cc.Scene.extend({
    onEnter:function(){
        this._super();

        var layer=new PlayLayer();
        this.addChild(layer);
    }
});