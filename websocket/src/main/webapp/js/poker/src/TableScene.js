var TableLayer = cc.Layer.extend({
    ctor: function () {
        //////////////////////////////
        // 1. super init first
        this._super();

        return true;
    },
    showPoker: function () {
        //var center=new HandCards("center",[0,3,8,9,13,21,22,40]);
        //this.addChild(center);


        var newVar = [0, 3, 8, 9, 10, 12, 13, 18, 19, 21, 22, 32, 40];
        //newVar.reverse();
        console.log(newVar);
        var unknows = [-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1];
        var west = new Player(this, "west", unknows);

        var south = new Player(this, "south", newVar);
        window.south=south;

        var east = new Player(this, "east", unknows);
        window.east=east;

        var center = new HandCards(this, "center", [3,5,7]);
        window.center=center;

        var belowCards = new BelowCards(this);
        belowCards.showUnknowStyle();

        window.belowCards = belowCards;
    }
});

var WaitingLayer=cc.Layer.extend({
    textLabel:null,
    updateCount:0,

    ctor: function () {
        //////////////////////////////
        // 1. super init first
        this._super();

        var size = cc.winSize;
        this.textLabel = new cc.LabelTTF("等待其他玩家","Arial", 12);
        this.textLabel.setFontFillColor(cc.color(0, 0, 0));
        this.textLabel.setPosition(size.width / 2, size.height/2);
        this.addChild(this.textLabel);
        this.schedule(this.updateProcessText, 1);
        return true;
    },
    updateProcessText:function(){
        this.updateCount++;
        var len=this.updateCount%6;
        var suffix="";
        for(var i=0;i<len;i++) {
            suffix += ".";
        }
        this.textLabel.setString("等待其他玩家"+suffix);
    },
    clearWaitingProcess:function(){
        this.unschedule(this.updateProcessText);
        this.removeFromParent();
    }
});

var TableScene = cc.Scene.extend({
    playerId:null,
    playerPos:null,
    waitLayer:null,
    tableLayer:null,

    onEnter: function () {
        this._super();
        this.addChild(new cc.LayerColor(cc.color(200, 255, 255, 180)));

        this.tableLayer = new TableLayer();
        //tableLayer.showPoker();
        this.addChild(this.tableLayer);

        this.waitingLayer = new WaitingLayer(this.tableLayer);
        this.addChild(this.waitingLayer);
        this.schedule(this.processServerResponse, 1);
        ServerRequest.AnonymousLogin(-1);
    },
    processServerResponse:function(){
        ws.doWork(this);
    }
});

